
INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (1, 'Tasty Mix of Whatever you Have on Hand with a Caffeine Boost!', true, 'Trash Can', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (2, 1, 1, 15, true)
     , (5, 1, 1, 15, true)
     , (7, 1, 1, 15, true)
     , (22, 1, 1, 15, true)
     , (25, 1, 1, 220, true)
     , (29, 1, 1, 15, true)
     , (23, 1, 1, 15, true);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (2,
        'Rum and Coke, or the Cuba libre, is a highball cocktail consisting of cola, rum, and in many recipes lime juice on ice. Traditionally, the cola ingredient is Coca-Cola ("Coke") and the alcohol is a light rum such as Bacardi. However, the drink may be made with various types of rums and cola brands, and lime juice may or may not be included.'
           , true, 'Cuba libre', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (1, 2, 1, 75, true)
     , (23, 2, 1, 25, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (2, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (3,
        'A classic blend of two famous drinks. The fine, mild taste of Vodka is enriched with a little sweetness. It is not for nothing that it is one of the most popular drinks in the world. A classic!', true, 'Vodka-Cola', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (5, 3, 1, 25, true)
     , (1, 3, 1, 75, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (3, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (4, 'Cloyingly sweet margarita mixes have given this drink a bad name. A well-made version is a fresh mix of lime juice and tequila, with a hint of sweetener.
Since this recipe includes fresh juice, it should be shaken. Serve over ice in a glass with a salted rim.', true, 'Margarita', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (11, 4, 1, 30, true)
     , (19, 4, 1, 60, true)
     , (30, 4, 1, 30, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (4, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (5, 'The cosmo became almost ubiquitous in the ''90s thanks to the TV show Sex and the City, but this spin on the martini remains just as tasty today as when Carrie Bradshaw made it famous.

Build all ingredients in a shaker tine with ice and shake. Strain into a martini glass and garnish with lime wheel or zest.', true, 'Cosmopolitan', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (31, 5, 1, 45, true)
     , (30, 5, 2, 30, true)
     , (11, 5, 3, 15, true)
     , (6, 5, 4, 7, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (5, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (6, 'A favorite of bartenders all over the world, the Negroni is a simple three-ingredient cocktail.', true, 'Negroni', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (9, 6, 1, 30, true)
     , (7, 6, 1, 30, true)
     , (8, 6, 1, 30, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (6, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (7,
        'Popular for good reason, the Moscow Mule is one of the most refreshing things to sip on a hot summer day. Its suggested vessel, a copper mug, also just looks sharp.', true, 'Moscow Mule', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (11, 7, 1, 15, true)
     , (5, 7, 2, 60, true)
     , (10, 7, 2, 150, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (7, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (8,
        'The Kamikaze is made of equal parts vodka, triple sec and lime juice. Garnish is typically a wedge or twist of lime.', true, 'Kamikaze', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (5, 8, 1, 40, true)
     , (11, 8, 1, 40, true)
     , (12, 8, 1, 10, true)
     , (28, 8, 1, 40, true)
     , (2, 8, 1, 30, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (8, 2);


INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (9,
        'The Sex on the Beach cocktail is known as much for its provocative name as its fruity, refreshing taste. It’s unclear exactly when or where the drink was invented, but popular liquor-soaked lore points to a Florida bartender who created the drink in 1987 as part of a liquor distribution company’s promotion to sell peach schnapps. The only problem with that theory, however, is that the drink was already included in the 1982 "American Bartenders School Guide to Drinks."', true, 'Sex on the beach', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (5, 9, 1, 40, true)
     , (2, 9, 1, 20, true)
     , (36, 9, 1, 5, true)
     , (12, 9, 1, 5, true)
     , (15, 9, 1, 60, true)
     , (33, 9, 1, 60, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (9, 2);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (10,
        'An adult version of the classic Arnold Palmer, the Touchdown is the ultimate Super Bowl cocktail.', true, 'Touch down', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (5, 10, 1, 40, true)
     , (34, 10, 2, 20, true)
     , (12, 10, 3, 20, true)
     , (35, 10, 4, 40, true)
     , (36, 10, 5, 10, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (10, 2);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (11,
        'Probably as fattening as it is alcoholic, this is a huge, creamy dessert in a glass served with generous innuendo.', true, 'Screaming Orgasm', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (5, 11, 1, 20, true)
     , (37, 11, 1, 20, true)
     , (39, 11, 1, 20, true)
     , (38, 11, 1, 20, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (11, 2);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (12,
        'Apropos of the Halloween season, the Devil''s Tail is blood-red in appearance, a tad on the sweet side, and fairly deadly in the potency department.', true, 'Devils Tail', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (5, 12, 1, 20, true)
     , (23, 12, 2, 20, true)
     , (34, 12, 2, 20, true)
     , (11, 12, 2, 40, true)
     , (36, 12, 2, 5, true)
     , (12, 12, 2, 5, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (12, 2);


INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (13,
        'The Lady Killer drink recipe tastes mainly like pineapple and passion fruit juices, but with hints of orange, apricot and some citrus-herbal notes from the gin. It’s – literally – a pretty sweet drink, but most of the sweetness comes from fruit juices rather than liqueurs. And that makes it a refreshing, fruity cocktail.', true, 'Ladykiller', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (7, 13, 1, 30, true)
     , (2, 13, 1, 15, true)
     , (40, 13, 1, 15, true)
     , (35, 13, 1, 70, true)
     , (20, 13, 1, 70, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (13, 3);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (14, 'The Godfather is a duo mixed drink made of Scotch whisky (especially smoky whisky) and amaretto. Typically, the drink is served with ice in an old fashioned glass.', true, 'Godfather', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount, scale)
VALUES (4, 14, 1, 75, true)
     , (42, 14, 1, 25, true);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (14, 1);

SELECT setval('recipes_id_seq', 14, true);