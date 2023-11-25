alter table ingredients
    add column image BLOB;

alter table ingredients
    add column last_update INTEGER not null DEFAULT 0;

update ingredients
set last_update = CURRENT_TIMESTAMP;
