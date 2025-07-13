alter table glasses
    add column empty_weight INTEGER default null check (empty_weight > 0);
