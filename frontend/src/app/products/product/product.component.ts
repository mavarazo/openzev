import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { ProductDto, ProductService } from '../../../generated-source/api';
import { Router } from '@angular/router';
import { AbstractDetailComponent } from '../../shared/components/abstract-detail.component';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss'],
})
export class ProductComponent extends AbstractDetailComponent<ProductDto> {
  @Input() productId: string;

  constructor(private router: Router, private productService: ProductService) {
    super();
  }

  override fetchEntity(): Observable<ProductDto> {
    return this.productService.getProduct(this.productId);
  }

  override deleteEntity(): Observable<any> {
    return this.productService.deleteProduct(this.productId);
  }

  override onSuccessfullDelete() {
    this.router.navigate(['/products']);
  }
}
