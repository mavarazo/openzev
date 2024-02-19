import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BankAccountsRoutingModule } from './bank-accounts-routing.module';
import { BankAccountsComponent } from './bank-accounts.component';
import { BankAccountComponent } from './bank-account/bank-account.component';
import { AddEditBankAccountComponent } from './add-edit-bank-account/add-edit-bank-account.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [
    BankAccountsComponent,
    BankAccountComponent,
    AddEditBankAccountComponent,
  ],
  imports: [
    CommonModule,
    BankAccountsRoutingModule,
    ReactiveFormsModule,
    SharedModule,
  ],
})
export class BankAccountsModule {}
