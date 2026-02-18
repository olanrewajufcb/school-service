-- =============================================
-- School Service Schema - V1
    --➡️ Create DB users via:

-- Terraform

-- Helm init scripts

-- DBA scripts

-- Cloud console
-- =============================================

-- Schema
CREATE SCHEMA IF NOT EXISTS school_schema AUTHORIZATION school_user;

GRANT USAGE, CREATE ON SCHEMA school_schema TO school_user;

-- Default privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA school_schema
GRANT ALL ON TABLES TO school_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA school_schema
GRANT ALL ON SEQUENCES TO school_user;

-- =====================
-- Types
-- =====================


-- =====================
-- Tables
-- =====================

CREATE TABLE school_schema.schools (
                                       school_id BIGSERIAL PRIMARY KEY,
                                       school_code VARCHAR(20) UNIQUE NOT NULL,
                                       school_name TEXT NOT NULL,

                                       type VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
                                       education_level VARCHAR(20) NOT NULL DEFAULT 'PRIMARY',

                                       address TEXT,
                                       phone TEXT,
                                       email TEXT,
                                       principal_name TEXT,

                                       max_students_per_class INT,
                                       school_capacity INT,

                                       academic_calendar VARCHAR,

                                       established_year DATE,
                                       status VARCHAR(20) DEFAULT 'ACTIVE',
                                       location VARCHAR (100) check ( location IN (
                                        'VILLAGE', 'TOWN', 'RURAL_AREA', 'OTHERS','URBAN'
                                                                                  ) ),
                                       ward VARCHAR(100),

                                       city TEXT,
                                       lga TEXT,
                                       state TEXT,

                                       deleted_at TIMESTAMPTZ DEFAULT NULL,
                                       created_at TIMESTAMPTZ DEFAULT NOW(),
                                       updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_school_code ON school_schema.schools (school_code);
CREATE INDEX idx_school_id ON school_schema.schools (school_id);
CREATE INDEX idx_school_name ON school_schema.schools (school_name);
CREATE INDEX idx_school_type ON school_schema.schools (type);
