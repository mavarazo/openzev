import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AgreementsRoutingModule } from './agreements-routing.module';
import { AgreementsComponent } from './agreements.component';
import {
  NbActionsModule,
  NbButtonModule,
  NbCardModule,
  NbDatepickerModule,
} from '@nebular/theme';
import { AddEditAgreementComponent } from './add-edit-agreement/add-edit-agreement.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AgreementComponent } from './agreement/agreement.component';

@NgModule({
  declarations: [
    AgreementsComponent,
    AddEditAgreementComponent,
    AgreementComponent,
  ],
  imports: [
    CommonModule,
    AgreementsRoutingModule,
    NbButtonModule,
    NbCardModule,
    FormsModule,
    ReactiveFormsModule,
    NbDatepickerModule,
    NbActionsModule,
  ],
})
export class AgreementsModule {}