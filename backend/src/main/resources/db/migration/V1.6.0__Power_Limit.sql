alter table pumps
    add column power_consumption INTEGER not null default 0 check (power_consumption >= 0);
