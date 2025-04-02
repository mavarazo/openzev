import { OverviewComponent } from './containers/overview/overview.component'
import { Routes } from '@angular/router'
import { AddEditComponent } from './containers/add-edit/add-edit.component'

export const routes: Routes = [
    {
        path: '',
        component: OverviewComponent,
    },
    {
        path: 'add',
        component: AddEditComponent,
    },
    {
        path: ':id',
        children: [
            {
                path: 'edit',
                component: AddEditComponent,
            },
        ],
    },
]
