alter table recipes
    drop column in_public;

drop table user_owned_ingredients;

alter table ingredients
    add column in_bar boolean not null default false;

create table event_actions
(
    id            bigserial    not null primary key,
    dType         varchar(20) check (dType IN ('ExecPy', 'CallUrl', 'PlayAudio', 'DoNothing')),
    trigger       varchar(255) not null,
    comment       varchar(40)  null,
    on_repeat     boolean check (((dType IN ('ExecPy', 'CallUrl', 'DoNothing')) AND on_repeat IS NULL) OR
                                 (dType = 'PlayAudio' AND on_repeat IS NOT NULL)),
    fileName      varchar(255) check (((dType IN ('CallUrl', 'DoNothing')) AND fileName IS NULL) OR
                                      ((dType IN ('ExecPy', 'PlayAudio')) AND fileName IS NOT NULL)),
    file          oid check (((dType IN ('CallUrl', 'DoNothing')) AND file IS NULL) OR
                             ((dType IN ('ExecPy', 'PlayAudio')) AND file IS NOT NULL)),
    requestMethod varchar(10) check ((dType = 'CallUrl' AND requestMethod IS NOT NULL) OR
                                     ((dType IN ('ExecPy', 'PlayAudio', 'DoNothing')) AND requestMethod IS NULL)),
    url           varchar(255) check ((dType = 'CallUrl' AND url IS NOT NULL) OR
                                      ((dType IN ('ExecPy', 'PlayAudio', 'DoNothing')) AND url IS NULL))
);

create table event_actions_execution_groups
(
    id      bigserial   not null references event_actions on delete cascade,
    "group" varchar(40) not null,
    primary key (id, "group")
);