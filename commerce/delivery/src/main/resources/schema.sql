CREATE TABLE IF NOT EXISTS addresses (
    id UUID PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(150) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50) NOT NULL DEFAULT '',
    CONSTRAINT unq_address_full UNIQUE (country, city, street, house, flat)
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    total_volume NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_weight NUMERIC(19, 4) NOT NULL DEFAULT 0,
    fragile BOOLEAN,
    from_address_id UUID NOT NULL,
    to_address_id UUID NOT NULL,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(50) NOT NULL,
    CONSTRAINT fk_delivery_from_address FOREIGN KEY (from_address_id) REFERENCES addresses(id),
    CONSTRAINT fk_delivery_to_address FOREIGN KEY (to_address_id) REFERENCES addresses(id)
);

CREATE INDEX IF NOT EXISTS idx_deliveries_from_address_id ON deliveries(from_address_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_to_address_id ON deliveries(to_address_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON deliveries(order_id);
