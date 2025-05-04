import { OverviewComponent } from './containers/overview/overview.component'
import { Routes } from '@angular/router'
import { AddEditComponent } from './containers/add-edit/add-edit.component'
import { DetailComponent } from './containers/detail/detail.component'

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
        path: ':unitId',
        children: [
            {
                path: '',
                component: DetailComponent,
            },
            {
                path: 'edit',
                component: AddEditComponent,
            },
        ],
    },
]
