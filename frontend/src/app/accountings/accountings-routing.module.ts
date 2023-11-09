import { RouterModule, Routes } from '@angular/router';
import { AddEditAccountingComponent } from './add-edit-accounting/add-edit-accounting.component';
import { AccountingComponent } from './accounting/accounting.component';
import { NgModule } from '@angular/core';
import { AccountingsComponent } from './accountings.component';

const routes: Routes = [
  {
    path: '',
    component: AccountingsComponent,
  },
  {
    path: 'add',
    component: AddEditAccountingComponent,
  },
  {
    path: ':accountingId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: AccountingComponent,
      },
      {
        path: 'edit',
        component: AddEditAccountingComponent,
      },
      {
        path: 'invoices',
        loadChildren: () =>
          import('../invoices/invoices.module').then((_) => _.InvoicesModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountingsRoutingModule {}
