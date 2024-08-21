CREATE SCHEMA IF NOT EXISTS test_schema;

DROP TABLE IF EXISTS test_schema.cars;

CREATE TABLE IF NOT EXISTS test_schema.cars
(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name       VARCHAR(255)            NOT NULL,
    created_at TIMESTAMP DEFAULT now() NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
