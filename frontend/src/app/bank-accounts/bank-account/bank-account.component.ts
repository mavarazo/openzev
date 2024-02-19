import { Component, Input } from '@angular/core';
import { AbstractDetailComponent } from '../../shared/components/abstract-detail.component';
import {
  BankAccountDto,
  BankAccountService,
} from '../../../generated-source/api';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-bank-account',
  templateUrl: './bank-account.component.html',
  styleUrls: ['./bank-account.component.scss'],
})
export class BankAccountComponent extends AbstractDetailComponent<BankAccountDto> {
  @Input() bankAccountId: string;

  constructor(
    private router: Router,
    private bankAccountService: BankAccountService
  ) {
    super();
  }

  fetchEntity(): Observable<BankAccountDto> {
    return this.bankAccountService.getBankAccount(this.bankAccountId);
  }

  deleteEntity(): Observable<any> {
    return this.bankAccountService.deleteBankAccount(this.bankAccountId);
  }

  onSuccessfullDelete(): void {
    this.router.navigate(['/bank-accounts']);
  }
}
