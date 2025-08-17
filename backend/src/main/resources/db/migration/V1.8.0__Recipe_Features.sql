ALTER TABLE recipes
    ADD COLUMN normal_name TEXT NOT NULL default '';
UPDATE recipes
SET normal_name = name;

ALTER TABLE collections
    ADD COLUMN normal_name TEXT NOT NULL default '';
UPDATE collections
SET normal_name = name;

ALTER TABLE ingredients
    ADD COLUMN normal_name TEXT NOT NULL default '';
UPDATE ingredients
SET normal_name = name;