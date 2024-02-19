import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RepresentativesComponent } from './representatives.component';
import { AddEditRepresentativeComponent } from './add-edit-representative/add-edit-representative.component';
import { RepresentativeComponent } from './representative/representative.component';

const routes: Routes = [
  {
    path: '',
    component: RepresentativesComponent,
  },
  {
    path: 'add',
    component: AddEditRepresentativeComponent,
  },
  {
    path: ':representativeId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: RepresentativeComponent,
      },
      {
        path: 'edit',
        component: AddEditRepresentativeComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RepresentativesRoutingModule {}
