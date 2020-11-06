
INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (2,
        'Rum and Coke, or the Cuba libre, is a highball cocktail consisting of cola, rum, and in many recipes lime juice on ice. Traditionally, the cola ingredient is Coca-Cola ("Coke") and the alcohol is a light rum such as Bacardi. However, the drink may be made with various types of rums and cola brands, and lime juice may or may not be included.'
           , true, 'Cuba libre', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (1, 2, 1, 75)
     , (23, 2, 1, 25);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (2, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (3,
        'A classic blend of two famous drinks. The fine, mild taste of Vodka is enriched with a little sweetness. It is not for nothing that it is one of the most popular drinks in the world. A classic!', true, 'Vodka-Cola', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (5, 3, 1, 25)
     , (1, 3, 1, 75);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (3, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (4, 'Cloyingly sweet margarita mixes have given this drink a bad name. A well-made version is a fresh mix of lime juice and tequila, with a hint of sweetener.
Since this recipe includes fresh juice, it should be shaken. Serve over ice in a glass with a salted rim.', true, 'Margarita', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (11, 4, 1, 30)
     , (19, 4, 1, 60)
     , (30, 4, 1, 30);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (4, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (5, 'The cosmo became almost ubiquitous in the ''90s thanks to the TV show Sex and the City, but this spin on the martini remains just as tasty today as when Carrie Bradshaw made it famous.

Build all ingredients in a shaker tine with ice and shake. Strain into a martini glass and garnish with lime wheel or zest.', true, 'Cosmopolitan', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (31, 5, 1, 45)
     , (30, 5, 2, 30)
     , (11, 5, 3, 15)
     , (6, 5, 4, 7);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (5, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (6, 'A favorite of bartenders all over the world, the Negroni is a simple three-ingredient cocktail.', true, 'Negroni', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (9, 6, 1, 30)
     , (7, 6, 1, 30)
     , (8, 6, 1, 30);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (6, 1);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (7,
        'Popular for good reason, the Moscow Mule is one of the most refreshing things to sip on a hot summer day. Its suggested vessel, a copper mug, also just looks sharp.', true, 'Moscow Mule', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (11, 7, 1, 15)
     , (5, 7, 2, 60)
     , (10, 7, 2, 150);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (7, 1);