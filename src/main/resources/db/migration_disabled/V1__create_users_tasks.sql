-- ============================
-- USERS TABLE
-- ============================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255) NOT NULL,

    phone VARCHAR(50),
    city VARCHAR(100),
    country VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    bio TEXT,

    reliability_score INT DEFAULT 100,
    tasks_completed INT DEFAULT 0,
    tasks_posted INT DEFAULT 0,
    total_earnings DOUBLE PRECISION DEFAULT 0.0
);

-- ============================
-- TASKS TABLE
-- ============================

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deliveryOtpHash TEXT,

    category VARCHAR(255) NOT NULL,
    offered_price DOUBLE PRECISION NOT NULL DEFAULT 0,
    commission DOUBLE PRECISION NOT NULL DEFAULT 0,

    city VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,

    status VARCHAR(20) NOT NULL,   -- OPEN, ACCEPTED, COMPLETED, DELIVERED, CANCELLED

    poster_id BIGINT NOT NULL,
    accepted_by BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_task_poster FOREIGN KEY (poster_id)
        REFERENCES users(id),

    CONSTRAINT fk_task_accepted_by FOREIGN KEY (accepted_by)
        REFERENCES users(id)
);
