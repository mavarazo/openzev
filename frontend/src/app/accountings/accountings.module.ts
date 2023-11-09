import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { AccountingsRoutingModule } from './accountings-routing.module';
import { AddEditAccountingComponent } from './add-edit-accounting/add-edit-accounting.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccountingComponent } from './accounting/accounting.component';
import { SharedModule } from '../shared/shared.module';
import { CoreModule } from '../core/core.module';
import { AccountingsComponent } from './accountings.component';

@NgModule({
  declarations: [
    AccountingsComponent,
    AddEditAccountingComponent,
    AccountingComponent,
  ],
  imports: [
    CommonModule,
    AccountingsRoutingModule,
    CoreModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  providers: [DatePipe],
})
export class AccountingsModule {}
