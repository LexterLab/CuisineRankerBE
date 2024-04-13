CREATE TABLE IF NOT EXISTS recipe_ingredients (
  id SERIAL PRIMARY KEY,
  recipe_id BIGINT,
  ingredient_id BIGINT,
  ingredient_amount DOUBLE PRECISION NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id),
  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

ALTER SEQUENCE recipe_ingredients_id_seq RESTART WITH 1;