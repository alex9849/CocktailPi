alter table recipes drop column in_public;

drop table user_owned_ingredients;
alter table ingredients add column in_bar boolean not null default false;