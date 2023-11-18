import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PropertiesComponent } from './properties.component';
import { AddEditPropertyComponent } from './add-edit-property/add-edit-property.component';
import { PropertyComponent } from './property/property.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: PropertiesComponent,
  },
  {
    path: 'add',
    component: AddEditPropertyComponent,
  },
  {
    path: ':propertyId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: PropertyComponent,
      },
      {
        path: 'edit',
        component: AddEditPropertyComponent,
      },
      {
        path: 'units',
        loadChildren: () =>
          import('../units/units.module').then((_) => _.UnitsModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PropertiesRoutingModule {}
