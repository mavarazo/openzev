import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { BankAccountDto, BankAccountService } from '../../generated-source/api';

@Component({
  selector: 'app-bank-accounts',
  templateUrl: './bank-accounts.component.html',
  styleUrls: ['./bank-accounts.component.scss'],
})
export class BankAccountsComponent implements OnInit {
  bankAccounts$: Observable<BankAccountDto[]>;

  constructor(private bankAccountService: BankAccountService) {}

  ngOnInit(): void {
    this.bankAccounts$ = this.bankAccountService.getBankAccounts();
  }
}
