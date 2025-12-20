CREATE TABLE delivery_proofs (
    id BIGSERIAL PRIMARY KEY,

    task_id BIGINT NOT NULL,
    doer_id BIGINT,
    type VARCHAR(50),         -- OTP or PHOTO
    payload TEXT,

    otp_hash TEXT,
    otp_expires_at TIMESTAMP,

    verified_by BIGINT,
    verified_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_dp_task FOREIGN KEY (task_id)
        REFERENCES tasks(id),

    CONSTRAINT fk_dp_doer FOREIGN KEY (doer_id)
        REFERENCES users(id),

    CONSTRAINT fk_dp_verified_by FOREIGN KEY (verified_by)
        REFERENCES users(id)
);
