import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { AccountingsRoutingModule } from './accountings-routing.module';
import { AddEditAccountingComponent } from './add-edit-accounting/add-edit-accounting.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccountingComponent } from './accounting/accounting.component';
import { SharedModule } from '../shared/shared.module';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { CoreModule } from '../core/core.module';
import { AccountingListComponent } from './accounting-list/accounting-list.component';
import { CardModule } from 'primeng/card';
import { PanelModule } from 'primeng/panel';
import { FieldsetModule } from 'primeng/fieldset';
import { StyleClassModule } from 'primeng/styleclass';
import { CalendarModule } from 'primeng/calendar';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { KeyFilterModule } from 'primeng/keyfilter';
import { InvoicesModule } from '../invoices/invoices.module';

@NgModule({
  declarations: [
    AddEditAccountingComponent,
    AccountingComponent,
    AccountingListComponent,
  ],
  imports: [
    CommonModule,
    AccountingsRoutingModule,
    CoreModule,
    SharedModule,
    InvoicesModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    BreadcrumbModule,
    CardModule,
    PanelModule,
    FieldsetModule,
    StyleClassModule,
    CalendarModule,
    InputTextModule,
    DropdownModule,
    KeyFilterModule,
  ],
  providers: [DatePipe],
})
export class AccountingsModule {}
