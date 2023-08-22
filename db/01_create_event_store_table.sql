\c voidium_market;
CREATE TABLE IF NOT EXISTS event_store (
  event_id UUID PRIMARY KEY,
  type VARCHAR(255) NOT NULL,
  event_date TIMESTAMP WITH TIME ZONE NOT NULL,
  aggregate_name VARCHAR(255) NOT NULL,
  aggregate_id UUID NOT NULL,
  data JSONB NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE event_store TO voidium_market;
CREATE INDEX event_store_aggregate_id_idx ON event_store(aggregate_id);
CREATE INDEX event_store_aggregate_name_idx ON event_store(aggregate_name);
