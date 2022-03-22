ALTER TABLE ingredients
    add column parent_group_id bigint NULL REFERENCES ingredients ON DELETE SET NULL;


-- forbid update of and to dtype-value ingredientGroup
CREATE OR REPLACE FUNCTION check_illegal_ingredient_dtype_change_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF (OLD.dtype != NEW.dtype AND (OLD.dtype = 'IngredientGroup' OR NEW.dtype = 'IngredientGroup')) THEN
        RAISE EXCEPTION 'IngredientGroup dtype may not be changed!';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER check_illegal_ingredient_dtype_change
    BEFORE UPDATE
    ON ingredients
    FOR EACH ROW
EXECUTE PROCEDURE check_illegal_ingredient_dtype_change_function();


-- check for illegal parent (only groups can be parents)
CREATE OR REPLACE FUNCTION check_illegal_ingredient_parent_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    is_illegal_parent bool;
BEGIN
    IF (NEW.parent_group_id IS NULL) THEN
        RETURN NEW;
    END IF;
    SELECT count(*) != 0
    INTO is_illegal_parent
    FROM ingredients
    WHERE id = NEW.parent_group_id
      AND dtype != 'IngredientGroup';

    if (NOT is_illegal_parent) THEN
        RETURN NEW;
    END IF;

    RAISE EXCEPTION 'parent_group_id must reference a group node' USING ERRCODE = '20808';
END;
$$;

CREATE TRIGGER check_illegal_ingredient_parent
    BEFORE INSERT OR UPDATE
    ON ingredients
    FOR EACH ROW
EXECUTE PROCEDURE check_illegal_ingredient_parent_function();


-- Check for cycles in ingredient hierarchy
CREATE OR REPLACE FUNCTION check_illegal_ingredient_cycle_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF EXISTS(
            WITH RECURSIVE list_parents(parent) AS (
                SELECT i.id AS child
                FROM ingredients AS i
                WHERE i.parent_group_id = NEW.id
                UNION ALL
                SELECT i.id AS child
                FROM ingredients AS i
                         join list_parents lp on i.parent_group_id = lp.parent
            )
            SELECT *
            FROM list_parents
            WHERE list_parents.parent = NEW.id
            LIMIT 1
        )
    THEN
        RAISE EXCEPTION 'Illegal cycle detected';
    ELSE
        RETURN NEW;
    END IF;
END
$$;

CREATE TRIGGER check_illegal_ingredient_cycle
    AFTER INSERT OR UPDATE
    ON ingredients
    FOR EACH ROW
EXECUTE PROCEDURE check_illegal_ingredient_cycle_function();

-- update ingredients in order to support IngredientGroup dtype
ALTER TABLE ingredients
    DROP CONSTRAINT ingredients_check;

ALTER TABLE ingredients
    DROP CONSTRAINT ingredients_alcohol_content_check;

ALTER TABLE ingredients
    DROP CONSTRAINT ingredients_dtype_check;

ALTER TABLE ingredients
    ALTER COLUMN alcohol_content DROP NOT NULL;

ALTER TABLE ingredients
    ALTER COLUMN unit DROP NOT NULL;

ALTER TABLE ingredients
    ALTER COLUMN in_bar DROP NOT NULL;

ALTER TABLE ingredients
    ALTER COLUMN in_bar DROP DEFAULT;

ALTER TABLE ingredients
    ADD CONSTRAINT ingredients_alcohol_content_check CHECK ((alcohol_content BETWEEN 0 AND 100 AND
                                                             alcohol_content IS NOT NULL AND
                                                             dtype IN ('ManualIngredient', 'AutomatedIngredient')) OR
                                                            alcohol_content IS NULL);

ALTER TABLE ingredients
    ADD CONSTRAINT ingredients_dType_check CHECK (dType IN ('ManualIngredient', 'AutomatedIngredient', 'IngredientGroup'));

ALTER TABLE ingredients
    ADD CONSTRAINT ingredients_pump_time_multiplier_check CHECK ((dType = 'AutomatedIngredient' AND pump_time_multiplier IS NOT NULL) OR
                                                                 pump_time_multiplier IS NULL);
ALTER TABLE ingredients
    ADD CONSTRAINT ingredients_parent_check CHECK (unit = 'MILLILITER' OR parent_group_id IS NULL);

UPDATE ingredients
SET unit = NULL
WHERE dtype = 'AutomatedIngredient';

ALTER TABLE ingredients
    ADD CONSTRAINT ingredients_unit_check CHECK ((dtype = 'ManualIngredient' AND unit IS NOT NULL) OR unit IS NULL);

ALTER TABLE ingredients
    ADD CONSTRAINT ingredients_in_bar_check CHECK ((dType IN ('ManualIngredient', 'AutomatedIngredient') AND
                                                    in_bar IS NOT NULL) OR in_bar IS NULL);

CREATE VIEW concrete_ingredient_dependencies AS
WITH RECURSIVE list_dependencies(leaf, current, parent) AS (
    SELECT i.id as leaf, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
    WHERE i.dtype != 'IngredientGroup'
    UNION ALL
    SELECT leaf, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
             join list_dependencies lp on i.id = lp.parent
)
SELECT leaf, current as is_a
FROM list_dependencies;


CREATE VIEW all_ingredient_dependencies AS
WITH RECURSIVE list_dependencies(leaf, current, parent) AS (
    SELECT i.id as leaf, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
    UNION ALL
    SELECT leaf, i.id as current, i.parent_group_id as parent
    FROM ingredients AS i
             join list_dependencies lp on i.id = lp.parent
)
SELECT leaf, current as is_a
FROM list_dependencies;

CREATE OR REPLACE FUNCTION is_ingredient_on_pump(ingredient_id bigint)
    RETURNS bool
    LANGUAGE plpgsql
AS
$$
BEGIN
    return EXISTS(
            SELECT *
            FROM concrete_ingredient_dependencies ide
                     JOIN ingredients i ON i.id = ide.leaf
                     JOIN pumps p ON i.id = p.current_ingredient_id AND i.dtype = 'AutomatedIngredient'
            WHERE ide.is_a = ingredient_id
        );
END;
$$;

CREATE OR REPLACE FUNCTION is_ingredient_in_bar(ingredient_id bigint)
    RETURNS bool
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF is_ingredient_on_pump(ingredient_id)
    THEN
        return true;
    END IF;

    return EXISTS(
            SELECT *
            FROM concrete_ingredient_dependencies ide
                     JOIN ingredients i ON i.id = ide.leaf
            WHERE ide.is_a = ingredient_id
              AND i.in_bar
        );
END;
$$;
