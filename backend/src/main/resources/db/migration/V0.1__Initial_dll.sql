create sequence hibernate_sequence start 1 increment 1;

create table users (
                       id bigserial not null,
                       email varchar(50) unique,
                       firstname varchar(20) not null,
                       is_account_non_locked boolean not null,
                       lastname varchar(20) not null,
                       password varchar(120),
                       role varchar(255) not null,
                       username varchar(20) not null unique,
                       primary key (id)
);

create table ingredients (
                             id bigserial not null,
                             alcohol_content int4 not null check (
                                         alcohol_content <= 100
                                     AND alcohol_content >= 0
                                 ),
                             name varchar(30) not null unique,
                             primary key (id)
);

create table pumps (
                       id bigserial not null,
                       gpio_pin int4 not null unique check (
                                   gpio_pin >= 1
                               AND gpio_pin <= 40
                           ),
                       time_per_cl_in_ms int4 not null check (time_per_cl_in_ms >= 1),
                       tube_capacity_in_ml int4 not null check (tube_capacity_in_ml >= 1),
                       current_ingredient_id int8 references ingredients,
                       primary key (id)
);

create table recipes (
                         id bigserial not null,
                         description varchar(3000) not null,
                         image oid,
                         in_public boolean not null,
                         name varchar(30) not null,
                         owner_id int8 not null references users on delete cascade,
                         primary key (id)
);

create table recipe_ingredients (
                                    ingredient_id int8 not null references ingredients on delete cascade,
                                    recipe_id int8 not null references recipes on delete cascade,
                                    production_step int4 not null,
                                    amount int4 not null check (amount >= 1),
                                    primary key (
                                                 ingredient_id, recipe_id, production_step
                                        )
);

create table tags (
                      id bigserial not null,
                      name varchar(30) not null unique,
                      primary key (id)
);

create table recipe_tags (
                             recipe_id int8 not null references recipes,
                             tag_id int8 not null references tags,
                             primary key (recipe_id, tag_id)
);