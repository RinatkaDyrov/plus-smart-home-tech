--DROP TABLE IF EXISTS warehouse_product;
--DROP TABLE IF EXISTS warehouse_address;

CREATE TABLE warehouse_product (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    width NUMERIC(10,2) NOT NULL CHECK (width  >= 1),
    height NUMERIC(10,2) NOT NULL CHECK (height >= 1),
    depth NUMERIC(10,2) NOT NULL CHECK (depth  >= 1),
    weight NUMERIC(10,2) NOT NULL CHECK (weight >= 1),
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE warehouse_address (
    id SMALLSERIAL PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(200) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50) NULL
);