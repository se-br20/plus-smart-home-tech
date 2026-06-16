CREATE TABLE IF NOT EXISTS shopping_carts (
                                              shopping_cart_id UUID PRIMARY KEY,
                                              username VARCHAR NOT NULL,
                                              active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_products (
                                                      shopping_cart_id UUID REFERENCES shopping_carts(shopping_cart_id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id)
    );