\c voidium_market;
CREATE TABLE IF NOT EXISTS purchase_order_projections (
  purchase_order_id UUID PRIMARY KEY,
  units_count NUMERIC NOT NULL,
  price_per_unit NUMERIC NOT NULL,
  valid_to TIMESTAMP WITH TIME ZONE NOT NULL,
  fulfillment_status TEXT NOT NULL,
  data JSONB NOT NULL,
  version INT NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE purchase_order_projections TO voidium_market;
CREATE INDEX purchase_order_projections_fulfillment_status_idx ON purchase_order_projections(fulfillment_status);
