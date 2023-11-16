import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OwnersComponent } from './owners.component';
import { AddEditOwnerComponent } from './add-edit-owner/add-edit-owner.component';
import { OwnerComponent } from './owner/owner.component';

const routes: Routes = [
  {
    path: '',
    component: OwnersComponent,
  },
  {
    path: 'add',
    component: AddEditOwnerComponent,
  },
  {
    path: ':ownerId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: OwnerComponent,
      },
      {
        path: 'edit',
        component: AddEditOwnerComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OwnersRoutingModule {}
