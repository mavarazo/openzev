import { Component, Input } from '@angular/core';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';
import {
  ItemDto,
  ItemService,
  ModifiableItemDto,
  ProductDto,
  ProductService,
} from '../../../generated-source/api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { EMPTY, map, Observable, takeUntil } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-edit-item',
  templateUrl: './add-edit-item.component.html',
  styleUrls: ['./add-edit-item.component.scss'],
})
export class AddEditItemComponent extends AbstractAddEditComponent<
  ItemDto,
  ModifiableItemDto
> {
  @Input() itemId: string | null;
  @Input() invoiceId: string;

  products$: Observable<ProductDto[]>;
  selectedProduct$: Observable<ProductDto | undefined> = EMPTY;

  override get isEdit(): boolean {
    return !!this.itemId;
  }

  constructor(
    private router: Router,
    private itemService: ItemService,
    private productService: ProductService
  ) {
    super();
  }

  override ngOnInit() {
    super.ngOnInit();

    this.products$ = this.productService
      .getProducts()
      .pipe(map((products) => products.filter((product) => product.active)));

    this.form
      .get('productId')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((productId) => {
        this.products$
          .pipe(
            map((products) =>
              products.find(
                (product) => !!product.id && product.id === productId
              )
            )
          )
          .subscribe((product) => {
            this.form
              .get('price')
              ?.setValue(product ? product.price : undefined);
          });
      });

    this.form
      .get('quantity')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.calculateAmount();
      });

    this.form
      .get('price')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.calculateAmount();
      });
  }

  private calculateAmount() {
    const quantity = +this.form.get('quantity')?.value;
    const price = +this.form.get('price')?.value;

    this.form.get('amount')?.setValue(quantity * price);
  }

  initForm(): FormGroup {
    return new FormGroup({
      productId: new FormControl(null),
      quantity: new FormControl(null, Validators.required),
      price: new FormControl(null, [
        Validators.required,
        Validators.pattern(/^\d*[.,]?\d{0,2}$/),
      ]),
      amount: new FormControl(null, [
        Validators.required,
        Validators.pattern(/^\d*[.,]?\d{0,2}$/),
      ]),
      notes: new FormControl(null),
    });
  }
  fetchEntity(): Observable<ItemDto> {
    return this.itemService.getItem(this.itemId!);
  }
  createEntity(item: ModifiableItemDto): Observable<string> {
    return this.itemService.createItem(this.invoiceId!, item);
  }
  onSuccessfullCreate(result: any): void {
    this.router.navigate(['/invoices', this.invoiceId]);
  }
  changeEntity(item: ModifiableItemDto): Observable<any> {
    return this.itemService.changeItem(this.itemId!, item);
  }
  onSuccessfullChange(result: any): void {
    this.router.navigate(['/invoices', this.invoiceId]);
  }
}
