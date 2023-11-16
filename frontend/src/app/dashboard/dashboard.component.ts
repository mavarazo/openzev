import { Component } from '@angular/core';
import {
  AccountingOverviewDto,
  DashboardService,
  OwnerOverviewDto,
  UnitOverviewDto,
} from '../../generated-source/api';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  accountings$: Observable<AccountingOverviewDto>;
  owners$: Observable<OwnerOverviewDto>;
  units$: Observable<UnitOverviewDto>;

  constructor(private dashboardService: DashboardService) {
    this.accountings$ = this.dashboardService.getAccountingOverview();
    this.owners$ = this.dashboardService.getOwnerOverview();
    this.units$ = this.dashboardService.getUnitOverview();
  }
}
