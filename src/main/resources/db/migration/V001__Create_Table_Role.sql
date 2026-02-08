CREATE TABLE IF NOT EXISTS tb_role (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO
    tb_role (id, name)
VALUES
    (gen_random_uuid(), 'ROLE_ROOT'),
    (gen_random_uuid(), 'ROLE_ADMINISTRATOR'),
    (gen_random_uuid(), 'ROLE_USER');
