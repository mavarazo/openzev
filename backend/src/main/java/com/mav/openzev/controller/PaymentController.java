package com.mav.openzev.controller;

import com.mav.openzev.api.PaymentApi;
import com.mav.openzev.api.model.ModifiablePaymentDto;
import com.mav.openzev.api.model.PaymentDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.PaymentMapper;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Payment;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;

  private final InvoiceRepository invoiceRepository;

  @Override
  @Transactional
  public ResponseEntity<List<PaymentDto>> getPayments(final UUID invoiceId) {
    final Invoice invoice =
        invoiceRepository
            .findByUuid(invoiceId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));

    return ResponseEntity.ok(
        invoice.getPayments().stream()
            .sorted(Comparator.comparing(Payment::getReceived))
            .map(paymentMapper::mapToPaymentDto)
            .toList());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createPayment(
      final UUID invoiceId, final ModifiablePaymentDto modifiablePaymentDto) {
    final Invoice invoice =
        invoiceRepository
            .findByUuid(invoiceId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));

    final Payment payment = paymentMapper.mapToPayment(modifiablePaymentDto);
    payment.setInvoice(invoice);
    return ResponseEntity.ok(paymentRepository.save(payment).getUuid());
  }

  @Override
  public ResponseEntity<PaymentDto> getPayment(final UUID paymentId) {
    return ResponseEntity.ok(
        paymentRepository
            .findByUuid(paymentId)
            .map(paymentMapper::mapToPaymentDto)
            .orElseThrow(() -> NotFoundException.ofPaymentNotFound(paymentId)));
  }

  @Override
  public ResponseEntity<UUID> changePayment(
      final UUID paymentId, final ModifiablePaymentDto modifiablePaymentDto) {
    final Payment payment =
        paymentRepository
            .findByUuid(paymentId)
            .orElseThrow(() -> NotFoundException.ofPaymentNotFound(paymentId));

    paymentMapper.updatePayment(modifiablePaymentDto, payment);
    return ResponseEntity.ok(paymentRepository.save(payment).getUuid());
  }

  @Override
  public ResponseEntity<Void> deletePayment(final UUID paymentId) {
    return paymentRepository
        .findByUuid(paymentId)
        .map(
            payment -> {
              paymentRepository.delete(payment);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseThrow(() -> NotFoundException.ofPaymentNotFound(paymentId));
  }
}
