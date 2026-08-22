UPDATE users SET email = LOWER(TRIM(email));

-- Preserve the earliest registration when historical data contains duplicates.
DELETE FROM users
WHERE id NOT IN (SELECT MIN(id) FROM users GROUP BY email);

ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT chk_users_email_normalized CHECK (email = LOWER(TRIM(email)));

