ALTER TABLE invites
    ADD COLUMN token       VARCHAR(36)  NOT NULL DEFAULT gen_random_uuid()::text,
    ADD COLUMN status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN role        VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    ADD COLUMN accepted_at TIMESTAMP    NULL;

ALTER TABLE invites ADD CONSTRAINT uq_invites_token UNIQUE (token);
ALTER TABLE invites ALTER COLUMN token DROP DEFAULT;
