package com.mav.openzev.controller;

import com.mav.openzev.api.PaymentApi;
import com.mav.openzev.api.model.ModifiablePaymentDto;
import com.mav.openzev.api.model.PaymentDto;
import com.mav.openzev.mapper.PaymentMapper;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Payment;
import com.mav.openzev.repository.PaymentRepository;
import com.mav.openzev.service.InvoiceService;
import com.mav.openzev.service.PaymentService;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

  private final PaymentRepository paymentRepository;
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;

  private final InvoiceService invoiceService;

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<List<PaymentDto>> getPayments(final UUID invoiceId) {
    final Invoice invoice = invoiceService.findInvoiceOrFail(invoiceId);

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
    final Invoice invoice = invoiceService.findInvoiceOrFail(invoiceId);
    final Payment payment = paymentMapper.mapToPayment(modifiablePaymentDto);
    payment.setInvoice(invoice);
    return ResponseEntity.ok(paymentRepository.save(payment).getUuid());
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<PaymentDto> getPayment(final UUID paymentId) {
    final Payment payment = paymentService.findPaymentOrFail(paymentId);
    return ResponseEntity.ok(paymentMapper.mapToPaymentDto(payment));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changePayment(
      final UUID paymentId, final ModifiablePaymentDto modifiablePaymentDto) {
    final Payment payment = paymentService.findPaymentOrFail(paymentId);
    paymentMapper.updatePayment(modifiablePaymentDto, payment);
    return ResponseEntity.ok(paymentRepository.save(payment).getUuid());
  }

  @Override
  public ResponseEntity<Void> deletePayment(final UUID paymentId) {
    final Payment payment = paymentService.findPaymentOrFail(paymentId);
    paymentRepository.delete(payment);
    return ResponseEntity.noContent().build();
  }
}
