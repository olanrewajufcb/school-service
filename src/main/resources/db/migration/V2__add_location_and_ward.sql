-- Create enum if it does not exist
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_type t
    JOIN pg_namespace n ON n.oid = t.typnamespace
    WHERE t.typname = 'location_type'
      AND n.nspname = 'school_schema'
  ) THEN
CREATE TYPE school_schema.location_type AS ENUM (
      'VILLAGE', 'TOWN', 'RURAL_AREA', 'OTHERS'
    );
END IF;
END $$;

ALTER TABLE school_schema.schools
    ADD COLUMN IF NOT EXISTS location school_schema.location_type,
    ADD COLUMN IF NOT EXISTS ward VARCHAR(100);
