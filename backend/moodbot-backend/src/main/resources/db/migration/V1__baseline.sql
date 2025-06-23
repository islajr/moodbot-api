CREATE TABLE users
(
    id         BIGSERIAL       NOT NULL,
    username   VARCHAR(20) NOT NULL,
    email      VARCHAR(50) NOT NULL,
    password   VARCHAR(75) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);