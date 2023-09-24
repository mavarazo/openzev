import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddEditInvoiceComponent } from './add-edit-invoice/add-edit-invoice.component';
import { InvoiceComponent } from './invoice/invoice.component';

const routes: Routes = [
  {
    path: 'add',
    component: AddEditInvoiceComponent,
  },
  {
    path: ':id',
    component: InvoiceComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InvoicesRoutingModule {}