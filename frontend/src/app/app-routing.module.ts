import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'accountings',
    loadChildren: () =>
      import('./accountings/accountings.module').then(
        (_) => _.AccountingsModule
      ),
  },
  {
    path: 'agreements',
    loadChildren: () =>
      import('./agreements/agreements.module').then((_) => _.AgreementsModule),
  },
  {
    path: 'invoices',
    loadChildren: () =>
      import('./invoices/invoices.module').then((_) => _.InvoicesModule),
  },
  {
    path: 'units',
    loadChildren: () =>
      import('./units/units.module').then((_) => _.UnitsModule),
  },
  {
    path: 'users',
    loadChildren: () =>
      import('./users/users.module').then((_) => _.UsersModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
