import { RouterModule, Routes } from '@angular/router';
import { accountingsResolver } from '../core/resolvers/accountings.resolver';
import { AddEditAccountingComponent } from './add-edit-accounting/add-edit-accounting.component';
import { AccountingComponent } from './accounting/accounting.component';
import { accountingResolver } from '../core/resolvers/accounting.resolver';
import { NgModule } from '@angular/core';
import { AccountingListComponent } from './accounting-list/accounting-list.component';

const routes: Routes = [
  {
    path: '',
    data: { breadcrumb: 'Accountings' },
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: AccountingListComponent,
        resolve: { accountings: accountingsResolver },
      },
      {
        path: 'add',
        component: AddEditAccountingComponent,
        data: { breadcrumb: 'Add' },
      },
      {
        path: ':accountingId',
        resolve: { accounting: accountingResolver },
        data: {
          breadcrumb: {
            alias: 'accountingSlug',
          },
        },
        children: [
          {
            path: '',
            pathMatch: 'full',
            component: AccountingComponent,
          },
          {
            path: 'invoices',
            loadChildren: () =>
              import('../invoices/invoices.module').then(
                (_) => _.InvoicesModule
              ),
          },
        ],
      },
      {
        path: 'edit/:accountingId',
        component: AddEditAccountingComponent,
        data: { breadcrumb: { alias: 'accountingSlug' } },
        resolve: { accounting: accountingResolver },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountingsRoutingModule {}
