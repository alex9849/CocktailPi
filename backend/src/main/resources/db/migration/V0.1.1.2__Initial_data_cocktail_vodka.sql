
INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (8,
        'The Kamikaze is made of equal parts vodka, triple sec and lime juice. Garnish is typically a wedge or twist of lime.', true, 'Kamikaze', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (5, 8, 1, 40)
     , (11, 8, 1, 40)
     , (12, 8, 1, 10)
     , (28, 8, 1, 40)
     , (2, 8, 1, 30);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (8, 2);


INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (9,
        'The Sex on the Beach cocktail is known as much for its provocative name as its fruity, refreshing taste. It’s unclear exactly when or where the drink was invented, but popular liquor-soaked lore points to a Florida bartender who created the drink in 1987 as part of a liquor distribution company’s promotion to sell peach schnapps. The only problem with that theory, however, is that the drink was already included in the 1982 "American Bartenders School Guide to Drinks."', true, 'Sex on the beach', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (34, 9, 1, 40)
     , (5, 9, 1, 40)
     , (15, 9, 1, 40)
     , (20, 9, 1, 80)
     , (33, 9, 1, 60);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (9, 2);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (10,
        'An adult version of the classic Arnold Palmer, the Touchdown is the ultimate Super Bowl cocktail.', true, 'Touch down', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (5, 10, 1, 40)
     , (34, 10, 2, 20)
     , (12, 10, 3, 20)
     , (35, 10, 4, 40)
     , (36, 10, 5, 10);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (10, 2);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (11,
        'Probably as fattening as it is alcoholic, this is a huge, creamy dessert in a glass served with generous innuendo.', true, 'Screaming Orgasm', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (5, 11, 1, 20)
     , (37, 11, 1, 20)
     , (39, 11, 1, 20)
     , (38, 11, 1, 20);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (11, 2);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (12,
        'Apropos of the Halloween season, the Devil''s Tail is blood-red in appearance, a tad on the sweet side, and fairly deadly in the potency department.', true, 'Devils Tail', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (5, 12, 1, 20)
     , (23, 12, 2, 20)
     , (34, 12, 2, 20)
     , (11, 12, 2, 40)
     , (36, 12, 2, 5)
     , (12, 12, 2, 5);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (12, 2);