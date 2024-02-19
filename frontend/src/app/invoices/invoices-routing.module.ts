import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddEditInvoiceComponent } from './add-edit-invoice/add-edit-invoice.component';
import { InvoiceComponent } from './invoice/invoice.component';
import { InvoicesComponent } from './invoices.component';
import { AddEditItemComponent } from './add-edit-item/add-edit-item.component';

const routes: Routes = [
  {
    path: '',
    component: InvoicesComponent,
  },
  {
    path: 'add',
    component: AddEditInvoiceComponent,
  },
  {
    path: ':invoiceId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: InvoiceComponent,
      },
      {
        path: 'edit',
        pathMatch: 'full',
        component: AddEditInvoiceComponent,
      },
      {
        path: 'items',
        children: [
          {
            path: 'add',
            component: AddEditItemComponent,
          },
          {
            path: ':itemId/edit',
            component: AddEditItemComponent,
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InvoicesRoutingModule {}
