
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