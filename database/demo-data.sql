-- DEMONSTRATION DATA ONLY. Do not use these sample values in a real restaurant.
USE restaurante;

INSERT INTO categories (name, description)
SELECT 'DEMO · Entradas', 'Categoría de ejemplo para validar la interfaz'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='DEMO · Entradas');

INSERT INTO categories (name, description)
SELECT 'DEMO · Platos principales', 'Categoría de ejemplo para validar la interfaz'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='DEMO · Platos principales');

INSERT INTO products (category_id, name, description, price, available)
SELECT c.id, 'DEMO · Sopa del día', 'Producto de muestra; reemplázalo por tu menú real', 12.00, TRUE
FROM categories c
WHERE c.name='DEMO · Entradas'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name='DEMO · Sopa del día');

INSERT INTO products (category_id, name, description, price, available)
SELECT c.id, 'DEMO · Plato de la casa', 'Producto de muestra; reemplázalo por tu menú real', 24.50, TRUE
FROM categories c
WHERE c.name='DEMO · Platos principales'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name='DEMO · Plato de la casa');

INSERT INTO dining_tables (table_number, capacity, status)
SELECT 'DEMO · Mesa 1', 4, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM dining_tables WHERE table_number='DEMO · Mesa 1');

INSERT INTO inventory_items (name, unit, quantity, minimum_quantity)
SELECT 'DEMO · Insumo de muestra', 'kg', 5.000, 1.000
WHERE NOT EXISTS (SELECT 1 FROM inventory_items WHERE name='DEMO · Insumo de muestra');
