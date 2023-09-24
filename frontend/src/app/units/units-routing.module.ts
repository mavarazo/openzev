import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UnitsComponent } from './units.component';
import { AddEditUnitComponent } from './add-edit-unit/add-edit-unit.component';
import { UnitComponent } from './unit/unit.component';
import { AddEditOwnershipComponent } from './add-edit-ownership/add-edit-ownership.component';

const routes: Routes = [
  {
    path: '',
    component: UnitsComponent,
  },
  {
    path: 'add',
    component: AddEditUnitComponent,
  },
  {
    path: ':id',
    component: UnitComponent,
  },
  {
    path: 'edit/:id',
    component: AddEditUnitComponent,
  },
  {
    path: ':id/ownerships/add',
    component: AddEditOwnershipComponent,
  },
  {
    path: ':id/ownerships/edit/:ownershipId',
    component: AddEditOwnershipComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UnitsRoutingModule {}
