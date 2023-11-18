import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddEditUnitComponent } from './add-edit-unit/add-edit-unit.component';
import { UnitComponent } from './unit/unit.component';
import { AddEditOwnershipComponent } from './add-edit-ownership/add-edit-ownership.component';

const routes: Routes = [
  {
    path: 'add',
    component: AddEditUnitComponent,
  },
  {
    path: ':unitId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: UnitComponent,
      },
      {
        path: 'edit',
        component: AddEditUnitComponent,
      },
      {
        path: 'ownerships',
        children: [
          {
            path: 'add',
            component: AddEditOwnershipComponent,
          },
          {
            path: ':ownershipId/edit',
            component: AddEditOwnershipComponent,
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UnitsRoutingModule {}
