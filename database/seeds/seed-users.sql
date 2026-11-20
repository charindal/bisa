-- ============================================================
-- seed-users.sql
-- Seeds one demo account per non-SUPER_ADMIN role, so a freshly
-- deployed environment has something to log in as and demo besides
-- the bootstrapped Super Admin (which comes from SUPER_ADMIN_EMAIL /
-- SUPER_ADMIN_PASSWORD on first boot — see SuperAdminBootstrapRunner).
--
-- Idempotent: safe to run against an environment that already has
-- these accounts — each insert only fires when that specific email
-- doesn't exist yet.
--
-- Run via:
--   docker exec -i bisa-local-test_db_1 psql -U bisa_sis -d bisa_sis -f - < database/seeds/seed-users.sql
-- or through the "Seed Database" GitHub Actions workflow
-- (.github/workflows/seed-database.yml), target = users.
--
-- Logins:
--   registrar@bisa.local / Demo1234!  (ADMIN)
-- ⚠️  Demo accounts only — do not seed these on a real production
--     environment with real student/staff data. CHANGE OR REMOVE
--     before any real go-live.
-- ============================================================

INSERT INTO users (id, email, password_hash, first_name, last_name, role, enabled, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'registrar@bisa.local',
    '$2b$10$3fRJk4NLrC/BwV9K09N4ieoc0NBet3vqiruGu91uodEG4pAxylwx6',
    'Nomsa',
    'Dlamini',
    'ADMIN',
    true,
    now(),
    now()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'registrar@bisa.local'
);
