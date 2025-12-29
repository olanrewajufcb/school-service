-- Drop order: reverse dependency order
DROP TABLE IF EXISTS enrolment_history;
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS guardians;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS schools;

-- Optional: enums
--