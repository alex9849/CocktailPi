alter table pumps
    add column milli_watt INTEGER not null default 0 check (milli_watt >= 0);
