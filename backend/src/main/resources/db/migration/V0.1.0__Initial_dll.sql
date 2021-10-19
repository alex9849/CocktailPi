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
    alcohol_content      int4        not null check (
            alcohol_content <= 100
            AND alcohol_content >= 0
        ),
    unit                 varchar     not null,
    name                 varchar(30) not null unique,
    pump_time_multiplier float8 check (dType = 'ManualIngredient' OR pump_time_multiplier IS NOT NULL),
    primary key (id)
);

create table user_owned_ingredients
(
    user_id       bigserial NOT NULL references users (id) on delete cascade,
    ingredient_id bigserial NOT NULL references ingredients (id) on delete cascade,
    primary key (user_id, ingredient_id)
);

create table pumps
(
    id                    bigserial not null,
    gpio_pin              int4      not null unique check (
            gpio_pin >= 0
            AND gpio_pin <= 31
        ),
    time_per_cl_in_ms     int4      not null check (time_per_cl_in_ms >= 1),
    tube_capacity_in_ml   int4      not null check (tube_capacity_in_ml >= 1),
    current_ingredient_id int8      references ingredients on delete set null,
    primary key (id)
);

create table recipes
(
    id                     bigserial                   not null,
    description            varchar(3000)               not null,
    image                  oid,
    in_public              boolean                     not null,
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