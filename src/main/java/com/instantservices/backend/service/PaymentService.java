package com.instantservices.backend.service;

import com.instantservices.backend.model.Payment;

public interface PaymentService {
    /**
     * Place a hold (escrow) for the given task amount. Returns the saved Payment record.
     * In production this will call the gateway to create an authorization/hold and store gateway ids.
     *
     * @param taskId  task id
     * @param posterId poster user id (payer)
     * @param doerId  doer user id (payee)
     * @param amount  amount to hold
     */
    Payment holdFunds(Long taskId, Long posterId, Long doerId, Double amount);

    /**
     * Release a held payment (capture/transfer to doer).
     * Implementations must safely find a held payment for the task and release/capture it.
     *
     * @param taskId task id (implementation uses this to find the held payment)
     */
    Payment releaseFunds(Long taskId) throws RuntimeException;
}
