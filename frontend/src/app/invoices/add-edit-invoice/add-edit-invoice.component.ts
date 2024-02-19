import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import {
  InvoiceDirection,
  InvoiceDto,
  InvoiceService,
  InvoiceStatus,
  ModifiableInvoiceDto,
  OwnerDto,
  OwnerService,
  OwnershipService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { combineLatest, Observable, switchMap } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';

@Component({
  selector: 'app-add-edit-invoice',
  templateUrl: './add-edit-invoice.component.html',
  styleUrls: ['./add-edit-invoice.component.scss'],
})
export class AddEditInvoiceComponent extends AbstractAddEditComponent<
  InvoiceDto,
  ModifiableInvoiceDto
> {
  @Input() invoiceId: string | null;

  units$: Observable<UnitDto[]>;
  owners$: Observable<OwnerDto[]>;

  get statuses(): InvoiceStatus[] {
    return Object.values(InvoiceStatus);
  }

  get directions(): InvoiceDirection[] {
    return Object.values(InvoiceDirection);
  }

  override get isEdit(): boolean {
    return !!this.invoiceId;
  }

  constructor(
    private router: Router,
    private invoiceService: InvoiceService,
    private unitService: UnitService,
    private ownershipService: OwnershipService,
    private ownerService: OwnerService
  ) {
    super();
  }

  override fetchEntity(): Observable<InvoiceDto> {
    return this.invoiceService.getInvoice(this.invoiceId!);
  }

  override ngOnInit(): void {
    super.ngOnInit();

    this.units$ = this.unitService.getUnits();

    this.loadAllOwners();

    this.form.get('unitId')?.valueChanges.subscribe((unitId) => {
      if (!unitId) {
        this.loadAllOwners();
      } else {
        this.owners$ = this.ownershipService.getOwnerships(unitId).pipe(
          switchMap((ownerships) => {
            const result = ownerships.map((ownership) =>
              this.ownerService.getOwner(ownership.ownerId!)
            );
            return combineLatest([...result]);
          })
        );
      }
    });
  }

  private loadAllOwners() {
    this.owners$ = this.ownerService.getOwners();
  }

  override initForm() {
    return new FormGroup({
      unitId: new FormControl(null),
      recipientId: new FormControl(null, Validators.required),
      status: new FormControl(InvoiceStatus.Draft, Validators.required),
      direction: new FormControl(null, Validators.required),
      subject: new FormControl(null, Validators.required),
      dueDate: new FormControl(null, Validators.required),
      notes: new FormControl(null),
    });
  }

  override createEntity(item: ModifiableInvoiceDto): Observable<any> {
    return this.invoiceService.createInvoice(item);
  }

  override onSuccessfullCreate(result: any) {
    this.router.navigate(['/invoices', result]);
  }

  override changeEntity(item: ModifiableInvoiceDto): Observable<any> {
    return this.invoiceService.changeInvoice(this.invoiceId!, item);
  }

  override onSuccessfullChange(result: any) {
    this.router.navigate(['/invoices', result]);
  }
}
