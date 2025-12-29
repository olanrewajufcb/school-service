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
ALTER DEFAULT PRIVILEGES FOR USER school_user
IN SCHEMA school_schema
GRANT ALL ON TABLES TO school_user;

ALTER DEFAULT PRIVILEGES FOR USER school_user
IN SCHEMA school_schema
GRANT ALL ON SEQUENCES TO school_user;

-- =====================
-- Types
-- =====================

CREATE TYPE school_schema.school_type AS ENUM (
  'PRIVATE', 'PUBLIC', 'OTHER'
);

CREATE TYPE school_schema.school_status AS ENUM (
  'ACTIVE', 'INACTIVE', 'CLOSED', 'SHUTDOWN'
);

CREATE TYPE school_schema.grade_level AS ENUM (
  'PRE_NURSERY', 'NURSERY', 'PRIMARY',
  'JUNIOR_SECONDARY', 'SENIOR_SECONDARY', 'OTHER'
);

CREATE TYPE school_schema.academic_calendar_type AS ENUM (
  'FIRST_TERM', 'SECOND_TERM', 'THIRD_TERM'
);

-- =====================
-- Tables
-- =====================

CREATE TABLE school_schema.schools (
                                       school_id BIGSERIAL PRIMARY KEY,
                                       school_code VARCHAR(20) UNIQUE NOT NULL,
                                       school_name TEXT NOT NULL,

                                       type school_schema.school_type NOT NULL DEFAULT 'PUBLIC',
                                       school_level school_schema.grade_level NOT NULL DEFAULT 'PRIMARY',

                                       address TEXT,
                                       phone TEXT,
                                       email TEXT,
                                       principal_name TEXT,

                                       max_students_per_class INT,
                                       school_capacity INT,

                                       academic_calendar school_schema.academic_calendar_type,

                                       established_year DATE,
                                       status school_schema.school_status DEFAULT 'ACTIVE',

                                       city TEXT,
                                       lga TEXT,
                                       state TEXT,

                                       deleted_at TIMESTAMPTZ DEFAULT NULL,
                                       created_at TIMESTAMPTZ DEFAULT NOW(),
                                       updated_at TIMESTAMPTZ DEFAULT NOW()
);
