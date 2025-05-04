import { Routes } from '@angular/router'

export const routes: Routes = [
    {
        path: 'meter-points',
        loadChildren: () =>
            import('./modules/meter-points').then((_) => _.routes),
    },
    {
        path: 'consumptions',
        loadChildren: () =>
            import('./modules/consumptions').then((_) => _.routes),
    },
    {
        path: 'units',
        loadChildren: () => import('./modules/units').then((_) => _.routes),
    },
    {
        path: 'vendor-invoices',
        loadChildren: () =>
            import('./modules/vendor-invoices').then((_) => _.routes),
    },
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'meter-points',
    },
]
