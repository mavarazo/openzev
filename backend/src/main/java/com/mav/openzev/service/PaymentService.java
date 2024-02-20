package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Payment;
import com.mav.openzev.repository.PaymentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public Payment findPaymentOrFail(final UUID paymentId) {
    return paymentRepository
        .findByUuid(paymentId)
        .orElseThrow(() -> NotFoundException.ofPaymentNotFound(paymentId));
  }
}
