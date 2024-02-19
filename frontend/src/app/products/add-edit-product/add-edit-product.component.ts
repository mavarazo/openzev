import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  ModifiableProductDto,
  ProductDto,
  ProductService,
} from '../../../generated-source/api';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-edit-product',
  templateUrl: './add-edit-product.component.html',
  styleUrls: ['./add-edit-product.component.scss'],
})
export class AddEditProductComponent extends AbstractAddEditComponent<
  ProductDto,
  ModifiableProductDto
> {
  @Input() productId: string | null;

  override get isEdit(): boolean {
    return !!this.productId;
  }

  constructor(private productService: ProductService, private router: Router) {
    super();
  }

  override initForm() {
    return new FormGroup({
      subject: new FormControl(null, Validators.required),
      price: new FormControl(null, [
        Validators.required,
        Validators.pattern(/^\d*[.,]?\d{0,2}$/),
      ]),
      active: new FormControl(true, Validators.required),
      notes: new FormControl(null),
    });
  }

  override fetchEntity(): Observable<ProductDto> {
    return this.productService.getProduct(this.productId!);
  }

  override createEntity(item: ModifiableProductDto): Observable<any> {
    return this.productService.createProduct(item);
  }

  override onSuccessfullCreate(result: any) {
    this.router.navigate(['/products', result]);
  }

  override changeEntity(item: ModifiableProductDto): Observable<any> {
    return this.productService.changeProduct(this.productId!, item);
  }

  override onSuccessfullChange() {
    this.router.navigate(['/products', this.productId!]);
  }
}
