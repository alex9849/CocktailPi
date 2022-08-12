ALTER TABLE pumps
    add power_state_high boolean default false not null;
ALTER TABLE pumps
    add is_pumped_up boolean default false not null;
