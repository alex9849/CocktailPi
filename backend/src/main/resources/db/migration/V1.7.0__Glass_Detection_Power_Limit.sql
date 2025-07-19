alter table pumps
    add column power_consumption INTEGER not null default 0 check (power_consumption >= 0);

alter table glasses
    add column empty_weight INTEGER default null check (empty_weight > 0);
