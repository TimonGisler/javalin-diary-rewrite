ALTER TABLE entry
    ALTER COLUMN creationDate TYPE TIMESTAMPTZ USING creationDate AT TIME ZONE 'UTC';
