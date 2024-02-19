import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddEditInvoiceComponent } from './add-edit-invoice/add-edit-invoice.component';
import { ReactiveFormsModule } from '@angular/forms';
import { InvoiceComponent } from './invoice/invoice.component';
import { InvoicesRoutingModule } from './invoices-routing.module';
import { SharedModule } from '../shared/shared.module';
import { CoreModule } from '../core/core.module';
import { InvoicesComponent } from './invoices.component';
import { AddEditItemComponent } from './add-edit-item/add-edit-item.component';

@NgModule({
  declarations: [
    AddEditInvoiceComponent,
    InvoiceComponent,
    InvoicesComponent,
    AddEditItemComponent,
  ],
  imports: [
    CommonModule,
    InvoicesRoutingModule,
    CoreModule,
    SharedModule,
    ReactiveFormsModule,
  ],
})
export class InvoicesModule {}
