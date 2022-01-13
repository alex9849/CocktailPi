alter table recipes
    drop column in_public;

drop table user_owned_ingredients;

alter table ingredients
    add column in_bar boolean not null default false;

create table event_actions
(
    id            bigserial    not null primary key,
    dType         varchar(20) check (dType = 'PlayAudio' OR dType = 'ExecPy' OR dType = 'CallUrl'),
    trigger       varchar(255) not null,
    description   varchar(40)  null,
    on_repeat      boolean check ((dType = 'CallUrl' AND on_repeat IS NULL) OR
                                 ((dType = 'ExecPy' OR dType = 'CallUrl') AND on_repeat IS NOT NULL)),
    fileName      varchar(255) check ((dType = 'CallUrl' AND fileName IS NULL) OR
                                      ((dType = 'ExecPy' OR dType = 'CallUrl') AND fileName IS NOT NULL)),
    file          oid check ((dType = 'CallUrl' AND file IS NULL) OR
                             ((dType = 'ExecPy' OR dType = 'CallUrl') AND file IS NOT NULL)),
    requestMethod varchar(10) check ((dType = 'CallUrl' AND file IS NOT NULL) OR
                                     ((dType = 'ExecPy' OR dType = 'CallUrl') AND file IS NULL)),
    url           varchar(255) check ((dType = 'CallUrl' AND file IS NOT NULL) OR
                                      ((dType = 'ExecPy' OR dType = 'CallUrl') AND file IS NULL))
);

create table event_actions_execution_groups
(
    id      bigserial   not null references event_actions on delete cascade,
    "group" varchar(25) not null,
    primary key (id, "group")
);