import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from './products.component';
import { AddEditProductComponent } from './add-edit-product/add-edit-product.component';
import { ProductComponent } from './product/product.component';

const routes: Routes = [
  {
    path: '',
    component: ProductsComponent,
  },
  {
    path: 'add',
    component: AddEditProductComponent,
  },
  {
    path: ':productId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: ProductComponent,
      },
      {
        path: 'edit',
        component: AddEditProductComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProductsRoutingModule {}
