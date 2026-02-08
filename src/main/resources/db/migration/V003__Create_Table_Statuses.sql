CREATE TABLE IF NOT EXISTS tb_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO
    tb_status (id, name)
VALUES
    (gen_random_uuid(), 'active'),
    (gen_random_uuid(), 'muted'),
    (gen_random_uuid(), 'banned');
