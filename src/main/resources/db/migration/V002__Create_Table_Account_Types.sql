CREATE TABLE IF NOT EXISTS tb_account_type (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO
    tb_account_type (id, name)
VALUES
    (gen_random_uuid(), 'standard'),
    (gen_random_uuid(), 'buyer'),
    (gen_random_uuid(), 'premium');
