\c voidium_market;
CREATE TABLE IF NOT EXISTS purchase_order_projections (
  purchase_order_id UUID PRIMARY KEY,
  data JSONB NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE purchase_order_projections TO voidium_market;
