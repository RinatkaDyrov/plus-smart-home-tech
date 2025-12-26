CREATE TABLE orders (
  order_id UUID PRIMARY KEY,
  shopping_cart_id UUID,
  payment_id UUID NULL,
  delivery_id UUID NULL,
  state VARCHAR NOT NULL,
  delivery_weight NUMERIC(10, 2),
  delivery_volume NUMERIC(10, 2),
  fragile BOOLEAN NOT NULL DEFAULT FALSE,
  product_price NUMERIC(12,2) NOT NULL
  delivery_price NUMERIC(12,2) NOT NULL
  total_price NUMERIC(12,2) NOT NULL
);