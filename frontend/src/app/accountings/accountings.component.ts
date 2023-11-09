import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountingDto, AccountingService } from '../../generated-source/api';

@Component({
  selector: 'app-accountings',
  templateUrl: './accountings.component.html',
  styleUrls: ['./accountings.component.scss'],
})
export class AccountingsComponent implements OnInit {
  accountings$: Observable<AccountingDto[]>;

  constructor(private accountingService: AccountingService) {}

  ngOnInit(): void {
    this.accountings$ = this.accountingService.getAccountings();
  }
}
