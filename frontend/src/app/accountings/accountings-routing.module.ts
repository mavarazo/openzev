import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountingsComponent } from './accountings.component';
import { AddEditAccountingComponent } from './add-edit-accounting/add-edit-accounting.component';
import { AccountingComponent } from './accounting/accounting.component';

const routes: Routes = [
  {
    path: '',
    component: AccountingsComponent,
  },
  {
    path: 'add',
    component: AddEditAccountingComponent,
  },
  {
    path: ':id',
    component: AccountingComponent,
  },
  {
    path: 'edit/:id',
    component: AddEditAccountingComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountingsRoutingModule {}
