CREATE TABLE IF NOT EXISTS payments (
                                        payment_id UUID PRIMARY KEY,
                                        order_id UUID NOT NULL,
                                        total_payment NUMERIC,
                                        delivery_total NUMERIC,
                                        fee_total NUMERIC,
                                        product_total NUMERIC,
                                        state VARCHAR NOT NULL
);