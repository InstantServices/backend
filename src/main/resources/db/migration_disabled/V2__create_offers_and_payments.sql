-- ============================
-- OFFERS TABLE
-- ============================

CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    proposed_commission DOUBLE PRECISION NOT NULL,

    message TEXT,
    expected_time VARCHAR(255),

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, ACCEPTED, REJECTED

    task_id BIGINT,
    user_id BIGINT,

    CONSTRAINT fk_offer_task FOREIGN KEY (task_id)
        REFERENCES tasks(id),

    CONSTRAINT fk_offer_user FOREIGN KEY (user_id)
        REFERENCES users(id)
);

-- ============================
-- PAYMENTS TABLE
-- ============================

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,

    task_id BIGINT NOT NULL,
    poster_id BIGINT NOT NULL,
    doer_id BIGINT NOT NULL,

    amount DOUBLE PRECISION NOT NULL,

    status VARCHAR(50) NOT NULL,
    gateway_txn_id TEXT,
    hold_id TEXT,
    gateway_status VARCHAR(255),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payment_task FOREIGN KEY (task_id)
        REFERENCES tasks(id),

    CONSTRAINT fk_payment_poster FOREIGN KEY (poster_id)
        REFERENCES users(id),

    CONSTRAINT fk_payment_doer FOREIGN KEY (doer_id)
        REFERENCES users(id)
);
