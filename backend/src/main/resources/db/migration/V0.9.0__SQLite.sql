create table users
(
    id                    INTEGER not null,
    is_account_non_locked BOOLEAN not null,
    password              TEXT,
    role                  TEXT    not null,
    username              TEXT    not null unique,
    primary key (id)
);

CREATE TABLE gpio_boards
(
    id          INTEGER not null PRIMARY KEY,
    name        text    not null unique,
    dType       text    not null,
    board_model text check (dType != 'i2c' or board_model IS NOT NULL),
    i2c_address INTEGER check (dType != 'i2c' or i2c_address IS NOT NULL)
);

CREATE TABLE gpio_pins
(
    pin_nr INTEGER not null,
    board  INTEGER not null REFERENCES gpio_boards ON DELETE CASCADE,
    PRIMARY KEY (board, pin_nr)
);

create table ingredients
(
    id                   INTEGER not null,
    dType                TEXT    not null,
    alcohol_content      INTEGER,
    parent_group_id      INTEGER REFERENCES ingredients ON DELETE SET NULL CHECK (parent_group_id != id),
    bottle_size          INTEGER,
    unit                 TEXT,
    in_bar               BOOLEAN,
    name                 TEXT    not null unique,
    pump_time_multiplier REAL,
    constraint check_nn_bottle_size check (dType != 'AutomatedIngredient' OR bottle_size IS NOT NULL),
    constraint check_range_alcohol_content check (alcohol_content BETWEEN 0 AND 100),
    constraint check_nn_pump_time_multiplier check (dType != 'AutomatedIngredient' OR pump_time_multiplier IS NOT NULL),
    constraint check_not_self_parent_group_id check (parent_group_id != id),
    primary key (id)
);

create table pumps
(
    id                    INTEGER not null,
    dType                 TEXT    not null,
    name                  TEXT unique,
    completed             BOOLEAN not null,
    tube_capacity         REAL check (tube_capacity >= 0),
    current_ingredient_id INTEGER references ingredients on delete set null,
    filling_level_in_ml   INTEGER not null check (filling_level_in_ml >= 0),
    is_pumped_up          BOOLEAN not null default false,
    dc_pin_board          INTEGER,
    dc_pin_nr             INTEGER,
    time_per_cl_in_ms     INTEGER check (time_per_cl_in_ms >= 1),
    is_power_state_high   BOOLEAN,
    acceleration          INTEGER check (acceleration BETWEEN 1 and 500000),
    step_pin_board        INTEGER,
    step_pin_nr           INTEGER,
    enable_pin_board      INTEGER,
    enable_pin_nr         INTEGER,
    steps_per_cl          INTEGER check (steps_per_cl >= 1),
    max_steps_per_second  INTEGER check (max_steps_per_second BETWEEN 1 and 500000),
    primary key (id),
    FOREIGN KEY (dc_pin_board, dc_pin_nr) REFERENCES gpio_pins ON DELETE RESTRICT,
    FOREIGN KEY (step_pin_board, step_pin_nr) REFERENCES gpio_pins ON DELETE RESTRICT,
    FOREIGN KEY (enable_pin_board, enable_pin_nr) REFERENCES gpio_pins ON DELETE RESTRICT
);

CREATE TABLE glasses
(
    id   INTEGER not null,
    name TEXT    not null,
    size INTEGER not null,
    primary key (id),
    CONSTRAINT unique_name unique (name),
    CONSTRAINT size_range check (size BETWEEN 10 AND 5000)
);

create table recipes
(
    id          INTEGER not null,
    description TEXT,
    image       BLOB,
    name        TEXT    not null,
    owner_id    INTEGER not null references users on delete cascade,
    last_update INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,
    glass_id    INTEGER references glasses on delete set null,
    primary key (id)
);

create table production_steps
(
    recipe_id INTEGER not null references recipes on delete cascade,
    dType     TEXT    not null,
    message   TEXT check (dType != 'WrittenInstruction' OR (message IS NOT NULL AND length(message) > 0)),
    "order"   INTEGER not null,
    primary key (recipe_id, "order")
);

create table production_step_ingredients
(
    recipe_id     INTEGER not null,
    "order"       INTEGER not null,
    ingredient_id INTEGER not null references ingredients on delete cascade,
    amount        INTEGER not null check (amount >= 1),
    scale         BOOLEAN not null,
    boostable     BOOLEAN not null default false,
    primary key (ingredient_id, recipe_id, "order"),
    foreign key (recipe_id, "order") references production_steps on delete cascade
);

create table categories
(
    id   INTEGER not null,
    name TEXT    NOT NULL,
    primary key (id)
);

CREATE TABLE recipe_categories
(
    recipe_id     INTEGER not null references recipes on delete cascade,
    categories_id INTEGER not null references categories on delete cascade,
    primary key (recipe_id, categories_id)
);

create table collections
(
    id          INTEGER NOT NULL,
    name        TEXT    NOT NULL UNIQUE,
    description TEXT    NOT NULL,
    image       BLOB,
    owner_id    INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    last_update INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
);

CREATE TABLE collection_recipes
(
    recipe_id     INTEGER not null references recipes on delete cascade,
    collection_id INTEGER not null references collections on delete cascade,
    primary key (recipe_id, collection_id)
);

create table event_actions
(
    id            INTEGER not null primary key,
    dType         TEXT check (dType IN ('ExecPy', 'CallUrl', 'PlayAudio', 'DoNothing')),
    trigger       TEXT    not null,
    comment       TEXT    null,
    audio_device  TEXT,
    on_repeat     BOOLEAN check (((dType IN ('ExecPy', 'CallUrl', 'DoNothing')) AND on_repeat IS NULL) OR
                                 (dType = 'PlayAudio' AND on_repeat IS NOT NULL)),
    volume        DECIMAL(3) check (((dType IN ('ExecPy', 'CallUrl', 'DoNothing')) AND volume IS NULL) OR
                                    (dType = 'PlayAudio' AND volume IS NOT NULL AND (volume BETWEEN 0 and 100))),
    fileName      TEXT check (((dType IN ('CallUrl', 'DoNothing')) AND fileName IS NULL) OR
                              ((dType IN ('ExecPy', 'PlayAudio')) AND fileName IS NOT NULL)),
    file          BLOB check (((dType IN ('CallUrl', 'DoNothing')) AND file IS NULL) OR
                              ((dType IN ('ExecPy', 'PlayAudio')) AND file IS NOT NULL)),
    requestMethod TEXT check ((dType = 'CallUrl' AND requestMethod IS NOT NULL) OR
                              ((dType IN ('ExecPy', 'PlayAudio', 'DoNothing')) AND requestMethod IS NULL)),
    url           TEXT check ((dType = 'CallUrl' AND url IS NOT NULL) OR
                              ((dType IN ('ExecPy', 'PlayAudio', 'DoNothing')) AND url IS NULL)),
    constraint event_actions_audio_device_check check ((dType = 'PlayAudio' AND audio_device IS NOT NULL) OR audio_device IS NULL)
);

create table event_actions_execution_groups
(
    id      INTEGER not null references event_actions on delete cascade,
    "group" TEXT    not null,
    primary key (id, "group")
);

CREATE TABLE options
(
    key       TEXT not null primary key,
    value     TEXT,
    pin_board INTEGER,
    pin_nr    INTEGER,
    FOREIGN KEY (pin_board, pin_nr) REFERENCES gpio_pins ON DELETE RESTRICT
);

CREATE VIEW all_ingredient_dependencies AS
WITH RECURSIVE list_dependencies(child, current, parent)
                   AS (SELECT i.id as child, i.id as current, i.parent_group_id as parent
                       FROM ingredients AS i
                       UNION ALL
                       SELECT child, i.id as current, i.parent_group_id as parent
                       FROM ingredients AS i
                                join list_dependencies lp on i.id = lp.parent)
SELECT child, current as is_a
FROM list_dependencies;


CREATE VIEW concrete_ingredient_dependencies AS
WITH RECURSIVE list_dependencies(leaf, current, parent)
                   AS (SELECT i.id as leaf, i.id as current, i.parent_group_id as parent
                       FROM ingredients AS i
                       WHERE i.dtype != 'IngredientGroup'
                       UNION ALL
                       SELECT leaf, i.id as current, i.parent_group_id as parent
                       FROM ingredients AS i
                                join list_dependencies lp on i.id = lp.parent)
SELECT leaf, current as is_a
FROM list_dependencies;


CREATE TRIGGER check_illegal_ingredient_cycle
    BEFORE UPDATE
    ON ingredients
    WHEN new.parent_group_id IS NOT NULL
BEGIN
SELECT CASE
           WHEN EXISTS(WITH RECURSIVE list_parents(id, parent_id)
                                          AS (SELECT i.id AS id, i.parent_group_id AS parent_id
                                              FROM ingredients AS i
                                              WHERE i.parent_group_id = NEW.id --My id to insert
                                              UNION ALL
                                              SELECT i.id AS id, i.parent_group_id AS parent_id
                                              FROM ingredients AS i
                                                       join list_parents lp on i.parent_group_id = lp.id)
                       SELECT *
                       from list_parents
                       WHERE list_parents.id = NEW.parent_group_id --My own (new) parent;
                       LIMIT 1) THEN
               RAISE(ABORT, 'Illegal cycle detected')
           END;
END;


CREATE TRIGGER check_illegal_ingredient_parent_insert
    BEFORE INSERT
    ON ingredients
    WHEN NEW.parent_group_id IS NOT NULL
BEGIN
SELECT CASE
           WHEN EXISTS(SELECT i.id
                       FROM ingredients i
                       WHERE i.id = NEW.parent_group_id
                         and i.dType != 'IngredientGroup') THEN
               RAISE(ABORT, 'Parent must be an IngredientGroup!')
           END;
END;


CREATE TRIGGER check_illegal_ingredient_parent_update
    BEFORE UPDATE
    ON ingredients
    WHEN NEW.parent_group_id IS NOT NULL
BEGIN
SELECT CASE
           WHEN EXISTS(SELECT i.id
                       FROM ingredients i
                       WHERE i.id = NEW.parent_group_id
                         and i.dType != 'IngredientGroup') THEN
               RAISE(ABORT, 'Parent must be an IngredientGroup!')
           END;
END;


CREATE TRIGGER check_illegal_ingredient_dtype_update
    BEFORE UPDATE
    ON ingredients
BEGIN
SELECT CASE
           WHEN (old.dType == 'IngredientGroup' OR new.dType == 'IngredientGroup') AND old.dType != new.dType THEN
               RAISE(ABORT, 'Cant\t update from or to IngredientGroup!')
           END;
END;
