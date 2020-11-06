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