import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'configs',
    loadChildren: () =>
      import('./configs/configs.module').then((_) => _.ConfigsModule),
  },
  {
    path: 'dashboard',
    loadChildren: () =>
      import('./dashboard/dashboard.module').then((_) => _.DashboardModule),
  },
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
    path: 'owners',
    loadChildren: () =>
      import('./owners/owners.module').then((_) => _.OwnersModule),
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
