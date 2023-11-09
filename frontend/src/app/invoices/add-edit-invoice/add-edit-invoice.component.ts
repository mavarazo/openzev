import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
  AccountingDto,
  AccountingService,
  AgreementDto,
  AgreementService,
  InvoiceService,
  ModifiableInvoiceDto,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { EMPTY, Observable, share, Subject, switchMap, takeUntil } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-edit-invoice',
  templateUrl: './add-edit-invoice.component.html',
  styleUrls: ['./add-edit-invoice.component.scss'],
})
export class AddEditInvoiceComponent implements OnInit, OnDestroy {
  @Input() invoiceId: string | null;
  @Input() accountingId: string | null;

  private destroy$ = new Subject<void>();

  agreement: AgreementDto | undefined;

  accounting$: Observable<AccountingDto>;
  units$: Observable<UnitDto[]>;

  invoiceForm: FormGroup;
  isSubmitted: boolean = false;

  get highTariff() {
    return this.agreement?.highTariff;
  }

  get lowTariff() {
    return this.agreement?.lowTariff;
  }

  constructor(
    private router: Router,
    private invoiceService: InvoiceService,
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private unitService: UnitService
  ) {}

  ngOnInit(): void {
    this.units$ = this.unitService.getUnits();

    this.initForm();

    if (this.invoiceId) {
      this.invoiceService
        .getInvoice(this.invoiceId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((invoice) => this.invoiceForm.patchValue(invoice));
    }

    if (this.accountingId) {
      this.accounting$ = this.accountingService
        .getAccounting(this.accountingId)
        .pipe(share(), takeUntil(this.destroy$));

      this.accounting$
        .pipe(
          switchMap((a: AccountingDto) => {
            if (a.agreementId) {
              return this.agreementService.getAgreement(a.agreementId);
            }
            return EMPTY;
          }),
          takeUntil(this.destroy$)
        )
        .subscribe((a: AgreementDto) => (this.agreement = a));
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.invoiceForm = new FormGroup({
      accountingId: new FormControl(this.accountingId, Validators.required),
      unitId: new FormControl(null, Validators.required),
      usageHighTariff: new FormControl(null, Validators.required),
      usageLowTariff: new FormControl(null, Validators.required),
      usageTotal: new FormControl(null, Validators.required),
      amountHighTariff: new FormControl(null, Validators.required),
      amountLowTariff: new FormControl(null, Validators.required),
      amountTotal: new FormControl(null, Validators.required),
      payed: new FormControl(null),
    });

    this.invoiceForm
      .get('usageHighTariff')
      ?.valueChanges.subscribe((highTariff: number) => {
        const sum = highTariff + this.invoiceForm.get('usageLowTariff')?.value;
        this.invoiceForm.get('usageTotal')?.setValue(sum);

        const amountHighTariff = highTariff * this.agreement?.highTariff!;
        this.invoiceForm
          .get('amountHighTariff')
          ?.setValue(amountHighTariff.toFixed(2));
      });

    this.invoiceForm
      .get('usageLowTariff')
      ?.valueChanges.subscribe((lowTariff: number) => {
        const sum = this.invoiceForm.get('usageHighTariff')?.value + lowTariff;
        this.invoiceForm.get('usageTotal')?.setValue(sum);

        const amountLowTariff = lowTariff * this.agreement?.lowTariff!;
        this.invoiceForm
          .get('amountLowTariff')
          ?.setValue(amountLowTariff.toFixed(2));
      });

    this.invoiceForm
      .get('amountHighTariff')
      ?.valueChanges.subscribe((highTariff) => {
        const lowTariff = +this.invoiceForm.get('amountLowTariff')?.value;
        const sum = +highTariff + lowTariff;
        this.invoiceForm.get('amountTotal')?.setValue(sum.toFixed(2));
      });

    this.invoiceForm
      .get('amountLowTariff')
      ?.valueChanges.subscribe((lowTariff) => {
        const highTariff = +this.invoiceForm.get('amountHighTariff')?.value;
        const sum = highTariff + +lowTariff;
        this.invoiceForm.get('amountTotal')?.setValue(sum.toFixed(2));
      });
  }

  submit() {
    this.isSubmitted = true;

    if (this.invoiceForm.valid) {
      const invoice = {
        ...this.invoiceForm.value,
      } as ModifiableInvoiceDto;

      if (this.invoiceId) {
        this.editInvoice(invoice);
      } else {
        this.addInvoice(invoice);
      }
    }
  }

  private addInvoice(invoice: ModifiableInvoiceDto) {
    this.invoiceService
      .createInvoice(invoice)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/accountings', this.accountingId]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editInvoice(invoice: ModifiableInvoiceDto) {
    this.invoiceService
      .changeInvoice(this.invoiceId!, invoice)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/accountings', this.accountingId]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.invoiceForm.reset();
  }
}
