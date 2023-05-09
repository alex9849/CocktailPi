create sequence hibernate_sequence start 1 increment 1;

create table users
(
    id                    bigserial    not null,
    email                 varchar(50) unique,
    firstname             varchar(20)  not null,
    is_account_non_locked boolean      not null,
    lastname              varchar(20)  not null,
    password              varchar(120),
    role                  varchar(255) not null,
    username              varchar(20)  not null unique,
    primary key (id)
);

create table ingredients
(
    id                   bigserial   not null,
    dType                varchar(20) check (dType = 'ManualIngredient' OR dType = 'AutomatedIngredient'),
    alcohol_content      int4 check (
                alcohol_content <= 100
            AND alcohol_content >= 0
        ),
    parent_group_id      bigint      NULL REFERENCES ingredients ON DELETE SET NULL,
    unit                 varchar,
    in_bar               boolean,
    name                 varchar(30) not null unique,
    pump_time_multiplier float8 check (dType = 'ManualIngredient' OR pump_time_multiplier IS NOT NULL),
    primary key (id),
    CONSTRAINT ingredients_in_bar_check CHECK ((dType IN ('ManualIngredient', 'AutomatedIngredient') AND
                                                in_bar IS NOT NULL) OR in_bar IS NULL),
    CONSTRAINT ingredients_unit_check CHECK ((dtype = 'ManualIngredient' AND unit IS NOT NULL) OR unit IS NULL),
    CONSTRAINT ingredients_parent_check CHECK (unit = 'MILLILITER' OR parent_group_id IS NULL),
    CONSTRAINT ingredients_pump_time_multiplier_check CHECK ((dType = 'AutomatedIngredient' AND pump_time_multiplier IS NOT NULL) OR
                                                             pump_time_multiplier IS NULL),
    CONSTRAINT ingredients_dType_check CHECK (dType IN ('ManualIngredient', 'AutomatedIngredient', 'IngredientGroup')),
    CONSTRAINT ingredients_alcohol_content_check CHECK ((alcohol_content BETWEEN 0 AND 100 AND
                                                         alcohol_content IS NOT NULL AND
                                                         dtype IN ('ManualIngredient', 'AutomatedIngredient')) OR
                                                        alcohol_content IS NULL)
);

create table pumps
(
    id                    bigserial             not null,
    bcm_pin               int4                  not null unique check (
                bcm_pin >= 0
            AND bcm_pin <= 31
        ),
    time_per_cl_in_ms     int4                  not null check (time_per_cl_in_ms >= 1),
    tube_capacity_in_ml   double precision      not null check (tube_capacity_in_ml >= 1),
    current_ingredient_id int8                  references ingredients on delete set null,
    filling_level_in_ml   int4    default 0     not null,
    is_power_state_high   boolean default false not null,
    is_pumped_up          boolean default false not null,
    primary key (id)
);

create table recipes
(
    id                     bigserial                   not null,
    description            varchar(3000)               not null,
    image                  oid,
    name                   varchar(30)                 not null,
    owner_id               int8                        not null references users on delete cascade,
    last_update            timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    default_amount_to_fill int8                        NOT NULL DEFAULT 250,
    primary key (id)
);

create table production_steps
(
    recipe_id int8 not null references recipes on delete cascade,
    dType     varchar(20) check (dType = 'AddIngredients' OR dType = 'WrittenInstruction'),
    message   varchar(500) check (dType != 'WrittenInstruction' OR (message IS NOT NULL AND length(message) > 0)),
    "order"   int4 not null,
    primary key (recipe_id, "order")
);

create table production_step_ingredients
(
    recipe_id     int8 not null,
    "order"       int4 not null,
    ingredient_id int8 not null references ingredients on delete cascade,
    amount        int4 not null check (amount >= 1),
    scale         bool not null,
    boostable     bool not null default false,
    primary key (ingredient_id, recipe_id, "order"),
    foreign key (recipe_id, "order") references production_steps on delete cascade
);

create table categories
(
    id   bigserial   not null,
    name varchar(15) NOT NULL,
    primary key (id)
);

CREATE TABLE recipe_categories
(
    recipe_id     int8 not null references recipes on delete cascade,
    categories_id int8 not null references categories on delete cascade,
    primary key (recipe_id, categories_id)
);

create table collections
(
    id          bigserial                   NOT NULL,
    name        varchar(20)                 NOT NULL,
    description varchar(2000)               NOT NULL,
    completed   bool                        NOT NULL,
    image       oid,
    owner_id    bigserial                   NOT NULL REFERENCES users ON DELETE CASCADE,
    last_update timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
);

CREATE TABLE collection_recipes
(
    recipe_id     int8 not null references recipes on delete cascade,
    collection_id int8 not null references collections on delete cascade,
    primary key (recipe_id, collection_id)
);

create table event_actions
(
    id            bigserial    not null primary key,
    dType         varchar(20) check (dType IN ('ExecPy', 'CallUrl', 'PlayAudio', 'DoNothing')),
    trigger       varchar(255) not null,
    comment       varchar(40)  null,
    audio_device  text,
    on_repeat     boolean check (((dType IN ('ExecPy', 'CallUrl', 'DoNothing')) AND on_repeat IS NULL) OR
                                 (dType = 'PlayAudio' AND on_repeat IS NOT NULL)),
    volume        numeric(3) check (((dType IN ('ExecPy', 'CallUrl', 'DoNothing')) AND volume IS NULL) OR
                                    (dType = 'PlayAudio' AND volume IS NOT NULL AND (volume BETWEEN 0 and 100))),
    fileName      varchar(255) check (((dType IN ('CallUrl', 'DoNothing')) AND fileName IS NULL) OR
                                      ((dType IN ('ExecPy', 'PlayAudio')) AND fileName IS NOT NULL)),
    file          oid check (((dType IN ('CallUrl', 'DoNothing')) AND file IS NULL) OR
                             ((dType IN ('ExecPy', 'PlayAudio')) AND file IS NOT NULL)),
    requestMethod varchar(10) check ((dType = 'CallUrl' AND requestMethod IS NOT NULL) OR
                                     ((dType IN ('ExecPy', 'PlayAudio', 'DoNothing')) AND requestMethod IS NULL)),
    url           varchar(255) check ((dType = 'CallUrl' AND url IS NOT NULL) OR
                                      ((dType IN ('ExecPy', 'PlayAudio', 'DoNothing')) AND url IS NULL)),
    constraint event_actions_audio_device_check check ((dType = 'PlayAudio' AND audio_device IS NOT NULL) OR audio_device IS NULL)
);

create table event_actions_execution_groups
(
    id      bigserial   not null references event_actions on delete cascade,
    "group" varchar(40) not null,
    primary key (id, "group")
);

CREATE TABLE options
(
    key   varchar not null primary key,
    value varchar not null
);

-- forbid update of and to dtype-value ingredientGroup
CREATE OR REPLACE FUNCTION check_illegal_ingredient_dtype_change_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF (OLD.dtype != NEW.dtype AND (OLD.dtype = 'IngredientGroup' OR NEW.dtype = 'IngredientGroup')) THEN
        RAISE EXCEPTION 'IngredientGroup dtype may not be changed!';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER check_illegal_ingredient_dtype_change
    BEFORE UPDATE
    ON ingredients
    FOR EACH ROW
EXECUTE PROCEDURE check_illegal_ingredient_dtype_change_function();


-- check for illegal parent (only groups can be parents)
CREATE OR REPLACE FUNCTION check_illegal_ingredient_parent_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    is_illegal_parent bool;
BEGIN
    IF (NEW.parent_group_id IS NULL) THEN
        RETURN NEW;
    END IF;
    SELECT count(*) != 0
    INTO is_illegal_parent
    FROM ingredients
    WHERE id = NEW.parent_group_id
      AND dtype != 'IngredientGroup';

    if (NOT is_illegal_parent) THEN
        RETURN NEW;
    END IF;

    RAISE EXCEPTION 'parent_group_id must reference a group node' USING ERRCODE = '20808';
END;
$$;

CREATE TRIGGER check_illegal_ingredient_parent
    BEFORE INSERT OR UPDATE
    ON ingredients
    FOR EACH ROW
EXECUTE PROCEDURE check_illegal_ingredient_parent_function();


-- Check for cycles in ingredient hierarchy
CREATE OR REPLACE FUNCTION check_illegal_ingredient_cycle_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF EXISTS(
            WITH RECURSIVE list_parents(parent) AS (
                SELECT i.id AS child
                FROM ingredients AS i
                WHERE i.parent_group_id = NEW.id
                UNION ALL
                SELECT i.id AS child
                FROM ingredients AS i
                         join list_parents lp on i.parent_group_id = lp.parent
            )
            SELECT *
            FROM list_parents
            WHERE list_parents.parent = NEW.id
            LIMIT 1
        )
    THEN
        RAISE EXCEPTION 'Illegal cycle detected';
    ELSE
        RETURN NEW;
    END IF;
END
$$;

CREATE TRIGGER check_illegal_ingredient_cycle
    AFTER INSERT OR UPDATE
    ON ingredients
    FOR EACH ROW
EXECUTE PROCEDURE check_illegal_ingredient_cycle_function();

CREATE VIEW concrete_ingredient_dependencies AS
WITH RECURSIVE list_dependencies(leaf, current, parent) AS (
    SELECT i.id as leaf, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
    WHERE i.dtype != 'IngredientGroup'
    UNION ALL
    SELECT leaf, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
             join list_dependencies lp on i.id = lp.parent
)
SELECT leaf, current as is_a
FROM list_dependencies;


CREATE VIEW all_ingredient_dependencies AS
WITH RECURSIVE list_dependencies(child, current, parent) AS (
    SELECT i.id as child, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
    UNION ALL
    SELECT child, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
             join list_dependencies lp on i.id = lp.parent
)
SELECT child, current as is_a
FROM list_dependencies;

CREATE OR REPLACE FUNCTION is_ingredient_on_pump(ingredient_id bigint)
    RETURNS bool
    LANGUAGE plpgsql
AS
$$
BEGIN
    return EXISTS(
            SELECT *
            FROM concrete_ingredient_dependencies ide
                     JOIN ingredients i ON i.id = ide.leaf
                     JOIN pumps p ON i.id = p.current_ingredient_id AND i.dtype = 'AutomatedIngredient'
            WHERE ide.is_a = ingredient_id
        );
END;
$$;

CREATE OR REPLACE FUNCTION is_ingredient_in_bar(ingredient_id bigint)
    RETURNS bool
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF is_ingredient_on_pump(ingredient_id)
    THEN
        return true;
    END IF;

    return EXISTS(
            SELECT *
            FROM concrete_ingredient_dependencies ide
                     JOIN ingredients i ON i.id = ide.leaf
            WHERE ide.is_a = ingredient_id
              AND i.in_bar
        );
END;
$$;
