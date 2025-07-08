CREATE TABLE users
(
    id         BIGSERIAL       NOT NULL,
    username   VARCHAR(20) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(75) NOT NULL,
    is_email_verified BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);