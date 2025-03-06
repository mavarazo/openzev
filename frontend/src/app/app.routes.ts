import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'meter-points',
    loadChildren: () => import('./modules/meter-points').then((_) => _.routes),
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'meter-points',
  },
];
