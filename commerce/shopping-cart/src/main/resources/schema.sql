--DROP TABLE IF EXISTS shopping_cart_item;
--DROP TABLE IF EXISTS shopping_cart;

CREATE TABLE shopping_cart (
    shopping_cart_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE UNIQUE INDEX ux_shopping_cart_username_active
    ON shopping_cart (username)
    WHERE status = 'ACTIVE';

CREATE TABLE shopping_cart_item (
    id BIGSERIAL PRIMARY KEY,
    cart_id UUID NOT NULL REFERENCES shopping_cart(shopping_cart_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
);

CREATE UNIQUE INDEX ux_cart_item_cart_product
    ON shopping_cart_item (cart_id, product_id);
