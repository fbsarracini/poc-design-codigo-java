-- Backfill OWNER memberships from existing account owners before dropping the column
INSERT INTO memberships (account_id, user_id, role)
SELECT a.id, a.owner_id, 'OWNER'
FROM accounts a
WHERE NOT EXISTS (
    SELECT 1 FROM memberships m
    WHERE m.account_id = a.id AND m.user_id = a.owner_id
);

ALTER TABLE accounts DROP CONSTRAINT fk_accounts_owner;
ALTER TABLE accounts DROP COLUMN owner_id;
