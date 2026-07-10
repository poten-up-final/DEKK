ALTER TABLE admins
    ADD COLUMN department VARCHAR(100),
    ADD COLUMN status VARCHAR(50);

UPDATE admins
SET department = 'UNKNOWN'
WHERE department IS NULL;

UPDATE admins
SET status = 'ACTIVE'
WHERE status IS NULL;

ALTER TABLE admins
    ALTER COLUMN department SET NOT NULL,
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE admins
    ADD CONSTRAINT chk_admins_status CHECK (status IN ('ACTIVE', 'SUSPENDED'));
