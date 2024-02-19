import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankAccountsComponent } from './bank-accounts.component';
import { AddEditBankAccountComponent } from './add-edit-bank-account/add-edit-bank-account.component';
import { BankAccountComponent } from './bank-account/bank-account.component';

const routes: Routes = [
  {
    path: '',
    component: BankAccountsComponent,
  },
  {
    path: 'add',
    component: AddEditBankAccountComponent,
  },
  {
    path: ':bankAccountId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: BankAccountComponent,
      },
      {
        path: 'edit',
        component: AddEditBankAccountComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BankAccountsRoutingModule {}
