import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UnitsRoutingModule } from './units-routing.module';
import { UnitsComponent } from './units.component';
import {
  NbActionsModule,
  NbAlertModule,
  NbButtonModule,
  NbCardModule,
  NbDatepickerModule,
  NbIconModule,
  NbListModule,
} from '@nebular/theme';
import { ReactiveFormsModule } from '@angular/forms';
import { AddEditUnitComponent } from './add-edit-unit/add-edit-unit.component';
import { UnitComponent } from './unit/unit.component';
import { AddEditOwnershipComponent } from './add-edit-ownership/add-edit-ownership.component';

@NgModule({
  declarations: [
    UnitsComponent,
    AddEditUnitComponent,
    UnitComponent,
    AddEditOwnershipComponent,
  ],
  imports: [
    CommonModule,
    UnitsRoutingModule,
    ReactiveFormsModule,
    NbButtonModule,
    NbCardModule,
    NbIconModule,
    NbDatepickerModule,
    NbListModule,
    NbAlertModule,
    NbActionsModule,
  ],
})
export class UnitsModule {}
