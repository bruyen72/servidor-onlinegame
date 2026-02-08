CREATE TABLE IF NOT EXISTS tb_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    registration_date TIMESTAMPTZ NOT NULL,
    last_login TIMESTAMPTZ NOT NULL,
    last_recover_password TIMESTAMPTZ NOT NULL,
    receive_emails BOOLEAN NOT NULL,
    role_id UUID REFERENCES tb_role(id),
    account_type_id UUID REFERENCES tb_account_type(id),
    status_id UUID REFERENCES tb_status(id)
);
