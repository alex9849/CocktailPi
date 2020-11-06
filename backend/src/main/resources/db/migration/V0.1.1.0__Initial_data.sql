INSERT INTO ingredients (id, alcohol_content, name)
VALUES (1, 0, 'Cola')
     , (2, 38, 'Triple sec')
     , (3, 40, 'Tequila')
     , (4, 40, 'Whiskey')
     , (5, 40, 'Vodka')
     , (6, 0, 'Cranberry Juice')
     , (7, 38, 'Gin')
     , (8, 25, 'Campari')
     , (9, 20, 'Sweet Vermouth')
     , (14, 12, 'Champagne')
     , (10, 0, 'Ginger Beer')
     , (12, 0, 'Lemon Juice')
     , (11, 0, 'Lime Juice')
     , (15, 0, 'Orange Juice')
     , (13, 0, 'Simple Syrup')
     , (16, 16, 'Dry Vermouth')
     , (17, 38, 'Dark Rum')
     , (18, 40, 'Cognac')
     , (19, 38, 'Silver Tequila')
     , (20, 0, 'Pineapple Juice')
     , (21, 5, 'Mexican Beer')
     , (22, 21, 'Blue Curaçao')
     , (23, 40, 'Rum')
     , (24, 75, 'Gold Rum')
     , (25, 0, 'Red Bull')
     , (26, 17, 'Chambord Liqueur')
     , (27, 21, 'Malibu')
     , (28, 0, 'Sprite')
     , (29, 18, 'Peach Schnapps')
     , (30, 40, 'Cointreau')
     , (31, 40, 'Citrus Codka')
     , (32, 40, 'Grand Marnier')
     , (33, 0, 'Cranberry Nectar')
     , (34, 24, 'Apricot Brandy')
     , (35, 0, 'Maracuja Nectar')
     , (36, 0, 'Grenadine Syrup')
     , (37, 20, 'Kahlúa')
     , (38, 17, 'Baileys')
     , (39, 40, 'Cream')
     , (40, 20, 'Peach Liquor');
SELECT setval('ingredients_id_seq', 40, true);

INSERT INTO categories (id, name)
VALUES (1, 'Classics')
    , (2, 'Vodka drinks')
    , (3, 'Fancy drinks');
SELECT setval('categories_id_seq', 3, true);

INSERT INTO users (id, email, firstname, is_account_non_locked, lastname, password, role, username)
VALUES (1, 'admin@localhost.local', 'Admin', true, 'Example',
        '$2a$10$foQL7xSRCK53J/G.MIauWOnllOS9.vyIT5RtUQT25t5ref07MtCfe', 'ROLE_ADMIN', 'admin');
SELECT setval('users_id_seq', 1, true);

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (1, 'Tasty Mix of Whatever you Have on Hand with a Caffeine Boost!', true, 'Trash Can', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (2, 1, 1, 15)
     , (5, 1, 1, 15)
     , (7, 1, 1, 15)
     , (22, 1, 1, 15)
     , (25, 1, 1, 220)
     , (29, 1, 1, 15)
     , (23, 1, 1, 15);

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

INSERT INTO recipes (id, description, in_public, name, owner_id, last_update)
VALUES (13,
        'The Lady Killer drink recipe tastes mainly like pineapple and passion fruit juices, but with hints of orange, apricot and some citrus-herbal notes from the gin. It’s – literally – a pretty sweet drink, but most of the sweetness comes from fruit juices rather than liqueurs. And that makes it a refreshing, fruity cocktail.', true, 'Ladykiller', 1, CURRENT_TIMESTAMP);
INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount)
VALUES (7, 13, 1, 30)
     , (2, 13, 1, 15)
     , (40, 13, 1, 15)
     , (35, 13, 1, 70)
     , (20, 13, 1, 70);
INSERT INTO recipe_categories (recipe_id, categories_id)
VALUES (13, 3);


SELECT setval('recipes_id_seq', 13, true);