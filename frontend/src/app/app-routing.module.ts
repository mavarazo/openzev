import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'bank-accounts',
    loadChildren: () =>
      import('./bank-accounts/bank-accounts.module').then(
        (_) => _.BankAccountsModule
      ),
  },
  {
    path: 'dashboard',
    loadChildren: () =>
      import('./dashboard/dashboard.module').then((_) => _.DashboardModule),
  },
  {
    path: 'invoices',
    loadChildren: () =>
      import('./invoices/invoices.module').then((_) => _.InvoicesModule),
  },
  {
    path: 'owners',
    loadChildren: () =>
      import('./owners/owners.module').then((_) => _.OwnersModule),
  },
  {
    path: 'products',
    loadChildren: () =>
      import('./products/products.module').then((_) => _.ProductsModule),
  },
  {
    path: 'representatives',
    loadChildren: () =>
      import('./representatives/representatives.module').then(
        (_) => _.RepresentativesModule
      ),
  },
  {
    path: 'settings',
    loadChildren: () =>
      import('./settings/settings.module').then((_) => _.SettingsModule),
  },
  {
    path: 'units',
    loadChildren: () =>
      import('./units/units.module').then((_) => _.UnitsModule),
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { bindToComponentInputs: true })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
