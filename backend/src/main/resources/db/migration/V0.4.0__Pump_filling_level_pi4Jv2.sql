alter table pumps add column filling_level_in_ml int4 default 0 not null;

alter table pumps rename column gpio_pin to bcm_pin;