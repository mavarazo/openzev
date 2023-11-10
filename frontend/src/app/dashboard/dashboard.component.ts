import { Component } from '@angular/core';
import {
  AccountingOverviewDto,
  DashboardService,
  UnitOverviewDto,
  UserOverviewDto,
} from '../../generated-source/api';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  accountings$: Observable<AccountingOverviewDto>;
  units$: Observable<UnitOverviewDto>;
  users$: Observable<UserOverviewDto>;

  constructor(private dashboardService: DashboardService) {
    this.accountings$ = this.dashboardService.getAccountingOverview();
    this.units$ = this.dashboardService.getUnitOverview();
    this.users$ = this.dashboardService.getUserOverview();
  }
}
