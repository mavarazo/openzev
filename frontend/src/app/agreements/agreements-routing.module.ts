import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AgreementsComponent } from './agreements.component';
import { AddEditAgreementComponent } from './add-edit-agreement/add-edit-agreement.component';
import { AgreementComponent } from './agreement/agreement.component';

const routes: Routes = [
  {
    path: '',
    component: AgreementsComponent,
  },
  {
    path: 'add',
    component: AddEditAgreementComponent,
  },
  {
    path: ':id',
    component: AgreementComponent,
  },
  {
    path: 'edit/:id',
    component: AddEditAgreementComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AgreementsRoutingModule {}
