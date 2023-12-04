import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UnitsRoutingModule } from './units-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { AddEditUnitComponent } from './add-edit-unit/add-edit-unit.component';
import { UnitComponent } from './unit/unit.component';
import { AddEditOwnershipComponent } from './add-edit-ownership/add-edit-ownership.component';
import { UnitsComponent } from './units.component';

@NgModule({
  declarations: [
    AddEditUnitComponent,
    UnitComponent,
    AddEditOwnershipComponent,
    UnitsComponent,
  ],
  imports: [CommonModule, UnitsRoutingModule, ReactiveFormsModule],
  exports: [],
})
export class UnitsModule {}
