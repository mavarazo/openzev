import { Component, Input } from '@angular/core';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';
import {
  BankAccountDto,
  BankAccountService,
  ModifiableBankAccountDto,
} from '../../../generated-source/api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-edit-bank-account',
  templateUrl: './add-edit-bank-account.component.html',
  styleUrls: ['./add-edit-bank-account.component.scss'],
})
export class AddEditBankAccountComponent extends AbstractAddEditComponent<
  BankAccountDto,
  ModifiableBankAccountDto
> {
  @Input() bankAccountId: string | null;

  get isEdit(): boolean {
    return !!this.bankAccountId;
  }

  constructor(
    private bankAccountService: BankAccountService,
    private router: Router
  ) {
    super();
  }

  override initForm() {
    return new FormGroup({
      iban: new FormControl(null, [
        Validators.required,
        Validators.pattern(/^[A-Z]{2}(?: ?\d){18,20}$/),
      ]),
      name: new FormControl(null, Validators.required),
      street: new FormControl(null),
      houseNr: new FormControl(null),
      postalCode: new FormControl(null),
      city: new FormControl(null),
      active: new FormControl(null),
    });
  }

  fetchEntity(): Observable<BankAccountDto> {
    return this.bankAccountService.getBankAccount(this.bankAccountId!);
  }

  createEntity(entity: ModifiableBankAccountDto): Observable<any> {
    return this.bankAccountService.createBankAccount(entity);
  }

  onSuccessfullCreate(result: any): void {
    this.router.navigate(['/bank-accounts', result]);
  }

  changeEntity(entity: ModifiableBankAccountDto): Observable<any> {
    return this.bankAccountService.changeBankAccount(
      this.bankAccountId!,
      entity
    );
  }

  onSuccessfullChange(result: any): void {
    this.router.navigate(['/bank-accounts', this.bankAccountId!]);
  }
}
