import { Component, Input } from '@angular/core';
import { filter, forkJoin, map, Observable, switchMap, takeUntil } from 'rxjs';
import {
  InvoiceDto,
  InvoiceService,
  ItemDto,
  ItemService,
  OwnerDto,
  OwnerService,
  PaymentDto,
  PaymentService,
  ProductDto,
  ProductService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { AbstractDetailComponent } from '../../shared/components/abstract-detail.component';
import { Router } from '@angular/router';

export interface CustomItemDto extends ItemDto {
  product?: ProductDto;
}

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss'],
})
export class InvoiceComponent extends AbstractDetailComponent<InvoiceDto> {
  @Input() invoiceId: string;

  unit$: Observable<UnitDto>;
  recipient$: Observable<OwnerDto>;
  items$: Observable<CustomItemDto[]>;
  payments$: Observable<PaymentDto[]>;

  total: number = 0;

  constructor(
    private router: Router,
    private invoiceService: InvoiceService,
    private unitService: UnitService,
    private ownerService: OwnerService,
    private itemService: ItemService,
    private productService: ProductService,
    private paymentService: PaymentService
  ) {
    super();
  }

  override ngOnInit() {
    super.ngOnInit();

    this.unit$ = this.entity$.pipe(
      filter((invoice) => !!invoice.unitId),
      switchMap((invoice) => this.unitService.getUnit(invoice.unitId!))
    );

    this.recipient$ = this.entity$.pipe(
      switchMap((invoice) => this.ownerService.getOwner(invoice.recipientId!))
    );

    this.loadItems();

    this.payments$ = this.paymentService.getPayments(this.invoiceId);
  }

  private loadItems() {
    this.items$ = this.itemService.getItems(this.invoiceId).pipe(
      switchMap((invoices) => {
        const customItems = invoices.map((invoice) => {
          if (invoice.amount) {
            this.total += invoice.amount;
          }
          return this.productService
            .getProduct(invoice.productId!)
            .pipe(
              map(
                (product: ProductDto) =>
                  ({ ...invoice, product: product } as CustomItemDto)
              )
            );
        });
        return forkJoin(customItems);
      })
    );
  }

  override fetchEntity(): Observable<InvoiceDto> {
    return this.invoiceService.getInvoice(this.invoiceId);
  }

  override deleteEntity(): Observable<any> {
    return this.invoiceService.deleteInvoice(this.invoiceId);
  }

  override onSuccessfullDelete() {
    this.router.navigate(['/invoices']);
  }

  deleteItem(item: ItemDto) {
    if (item?.id) {
      this.itemService
        .deleteItem(item.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          error: console.error,
          complete: () => {
            this.loadItems();
          },
        });
    }
  }

  deletePayment(payment: PaymentDto) {
    if (payment?.id) {
      this.paymentService
        .deletePayment(payment.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          error: console.error,
          complete: () => {
            this.loadItems();
          },
        });
    }
  }

  download() {
    this.invoiceService
      .getPdf(this.invoiceId, 'body', true)
      .pipe(takeUntil(this.destroy$))
      .subscribe((file: Blob) => {
        const pdfBlob = new Blob([file], { type: 'application/pdf' });
        const fileURL = URL.createObjectURL(pdfBlob);
        window.open(fileURL, '_blank');
      });
  }
}
