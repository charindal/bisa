-- ============================================================
-- V2__remove_student_lecturer_roles.sql
-- Removes all users with STUDENT or LECTURER roles and 
-- adds a constraint to only allow SUPER_ADMIN and ADMIN roles.
-- ============================================================

-- Delete all users with STUDENT or LECTURER roles
DELETE FROM users WHERE role IN ('STUDENT', 'LECTURER');

-- Add constraint to enforce only valid roles
ALTER TABLE users ADD CONSTRAINT chk_valid_role 
  CHECK (role IN ('SUPER_ADMIN', 'ADMIN'));
