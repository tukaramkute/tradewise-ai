CREATE TABLE users (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(30) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone_number VARCHAR(16) NOT NULL,

    password_hash VARCHAR(100) NOT NULL,

    country VARCHAR(100) NOT NULL,
    time_zone VARCHAR(100) NOT NULL,
    preferred_currency CHAR(3) NOT NULL,
    profile_image_url VARCHAR(2048),

    status VARCHAR(30) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    updated_date TIMESTAMPTZ NOT NULL,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_by VARCHAR(100),
    deleted_date TIMESTAMPTZ
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL
        REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE UNIQUE INDEX uq_users_username_active
    ON users (LOWER(username))
    WHERE deleted = FALSE;

CREATE UNIQUE INDEX uq_users_email_active
    ON users (LOWER(email))
    WHERE deleted = FALSE;

CREATE UNIQUE INDEX uq_users_phone_active
    ON users (phone_number)
    WHERE deleted = FALSE;

CREATE INDEX ix_users_status
    ON users(status);

CREATE INDEX ix_users_created_date
    ON users(created_date);