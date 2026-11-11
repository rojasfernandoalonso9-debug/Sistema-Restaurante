CREATE DATABASE IF NOT EXISTS restaurante CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE restaurante;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'STAFF',
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  INDEX idx_users_active_role (active, role)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500),
  active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS dining_tables (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  table_number VARCHAR(30) NOT NULL UNIQUE,
  capacity INT UNSIGNED NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  version BIGINT NOT NULL DEFAULT 0,
  CHECK (capacity > 0), CHECK (status IN ('AVAILABLE','OCCUPIED','RESERVED')), INDEX idx_table_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  name VARCHAR(160) NOT NULL,
  description VARCHAR(1000),
  price DECIMAL(12,2) NOT NULL,
  image_url VARCHAR(500),
  available BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CHECK (price >= 0), INDEX idx_products_name (name), INDEX idx_products_available (available)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(160) NOT NULL UNIQUE,
  unit VARCHAR(30) NOT NULL,
  quantity DECIMAL(12,3) NOT NULL DEFAULT 0,
  minimum_quantity DECIMAL(12,3) NOT NULL DEFAULT 0,
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CHECK (quantity >= 0), CHECK (minimum_quantity >= 0), INDEX idx_inventory_stock (quantity, minimum_quantity)
) ENGINE=InnoDB;

-- Receta: cantidad del insumo necesaria para preparar una unidad del producto.
CREATE TABLE IF NOT EXISTS product_ingredients (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  inventory_item_id BIGINT NOT NULL,
  quantity_per_product DECIMAL(12,3) NOT NULL,
  CONSTRAINT fk_recipe_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_inventory FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id),
  CONSTRAINT uk_recipe_product_item UNIQUE (product_id, inventory_item_id),
  CHECK (quantity_per_product > 0), INDEX idx_recipe_inventory (inventory_item_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS restaurant_orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  table_id BIGINT,
  table_status_before_order VARCHAR(20),
  created_by BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  total DECIMAL(12,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_orders_table FOREIGN KEY (table_id) REFERENCES dining_tables(id) ON DELETE SET NULL,
  CONSTRAINT fk_orders_user FOREIGN KEY (created_by) REFERENCES users(id),
  CHECK (total >= 0), CHECK (status IN ('PENDING','PREPARING','READY','DELIVERED','CANCELLED')), INDEX idx_orders_status_date (status, created_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT UNSIGNED NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  subtotal DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES restaurant_orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
  CHECK (quantity > 0), CHECK (unit_price >= 0), CHECK (subtotal >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory_movements (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  inventory_item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  movement_type VARCHAR(20) NOT NULL,
  quantity DECIMAL(12,3) NOT NULL,
  note VARCHAR(500),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_movement_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id),
  CONSTRAINT fk_movement_user FOREIGN KEY (user_id) REFERENCES users(id),
  CHECK (quantity > 0), CHECK (movement_type IN ('IN','OUT','ADJUSTMENT')), INDEX idx_movements_item_date (inventory_item_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sales (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  payment_method VARCHAR(20) NOT NULL,
  total DECIMAL(12,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
  voided_by BIGINT,
  void_reason VARCHAR(500),
  voided_at TIMESTAMP(6) NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_sales_order FOREIGN KEY (order_id) REFERENCES restaurant_orders(id),
  CONSTRAINT fk_sales_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_sales_void_user FOREIGN KEY (voided_by) REFERENCES users(id),
  CHECK (total >= 0), CHECK (payment_method IN ('CASH','CARD','TRANSFER','OTHER')), CHECK (status IN ('COMPLETED','VOIDED')), INDEX idx_sales_date_status (created_at, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employees (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNIQUE,
  full_name VARCHAR(160) NOT NULL,
  job_title VARCHAR(100) NOT NULL,
  phone VARCHAR(40),
  employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  CHECK (employment_status IN ('ACTIVE','INACTIVE')),
  CONSTRAINT fk_employee_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Registros iniciales de operación. No incluyen cuentas ni datos comerciales ficticios.
CREATE TABLE IF NOT EXISTS business_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  record_type VARCHAR(32) NOT NULL,
  name VARCHAR(160) NOT NULL,
  description VARCHAR(1000),
  status VARCHAR(80),
  price DECIMAL(12,2),
  stock INT,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  email VARCHAR(190),
  role VARCHAR(30),
  capacity INT,
  table_name VARCHAR(80),
  INDEX idx_record_type (record_type),
  INDEX idx_record_name (name),
  CHECK (price IS NULL OR price >= 0), CHECK (stock IS NULL OR stock >= 0), CHECK (capacity IS NULL OR capacity >= 0)
) ENGINE=InnoDB;
