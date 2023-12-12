\c voidium_market;
CREATE TABLE IF NOT EXISTS sale_order_projections (
  sale_order_id UUID PRIMARY KEY,
  data JSONB NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE sale_order_projections TO voidium_market;
