package com.mav.openzev.controller;

import com.mav.openzev.api.DashboardApi;
import com.mav.openzev.api.model.AccountingOverviewDto;
import com.mav.openzev.api.model.OwnerOverviewDto;
import com.mav.openzev.api.model.UnitOverviewDto;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.OwnerRepository;
import com.mav.openzev.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

  private final AccountingRepository accountingRepository;
  private final UnitRepository unitRepository;
  private final OwnerRepository ownerRepository;

  @Override
  public ResponseEntity<AccountingOverviewDto> getAccountingOverview() {
    final long count = accountingRepository.count();
    return ResponseEntity.ok(new AccountingOverviewDto().total((int) count));
  }

  @Override
  public ResponseEntity<OwnerOverviewDto> getOwnerOverview() {
    final long count = ownerRepository.count();
    return ResponseEntity.ok(new OwnerOverviewDto().total((int) count));
  }

  @Override
  public ResponseEntity<UnitOverviewDto> getUnitOverview() {
    final long count = unitRepository.count();
    return ResponseEntity.ok(new UnitOverviewDto().total((int) count));
  }
}
