INSERT INTO recipe_ingredients (recipe_id, ingredient_id, ingredient_amount)
VALUES
    (1, 1, 200)
ON CONFLICT DO NOTHING;