import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InvoicesRoutingModule } from './invoices-routing.module';
import { AddEditInvoiceComponent } from './add-edit-invoice/add-edit-invoice.component';
import {
  NbActionsModule,
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
} from '@nebular/theme';
import { ReactiveFormsModule } from '@angular/forms';
import { InvoiceComponent } from './invoice/invoice.component';

@NgModule({
  declarations: [AddEditInvoiceComponent, InvoiceComponent],
  imports: [
    CommonModule,
    InvoicesRoutingModule,
    NbCardModule,
    ReactiveFormsModule,
    NbCheckboxModule,
    NbButtonModule,
    NbActionsModule,
  ],
})
export class InvoicesModule {}
