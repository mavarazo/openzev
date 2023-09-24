import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AccountingsRoutingModule } from './accountings-routing.module';
import { AccountingsComponent } from './accountings.component';
import {
  NbActionsModule,
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbDatepickerModule,
  NbIconModule,
} from '@nebular/theme';
import { AddEditAccountingComponent } from './add-edit-accounting/add-edit-accounting.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccountingComponent } from './accounting/accounting.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [
    AccountingsComponent,
    AddEditAccountingComponent,
    AccountingComponent,
  ],
  imports: [
    CommonModule,
    AccountingsRoutingModule,
    SharedModule,
    NbButtonModule,
    NbCardModule,
    FormsModule,
    ReactiveFormsModule,
    NbDatepickerModule,
    NbCheckboxModule,
    NbActionsModule,
    NbIconModule,
  ],
})
export class AccountingsModule {}
