alter table recipes
    drop column in_public;

drop table user_owned_ingredients;

alter table ingredients
    add column in_bar boolean not null default false;

create table event_actions
(
    id      bigserial    not null primary key,
    dType   varchar(20) check (dType = 'Demo1' OR dType = 'Demo2'),
    trigger varchar(255) not null
);

create table event_actions_execution_groups
(
    id      bigserial    not null references event_actions,
    "group" varchar(255) not null,
    primary key (id, "group")
);