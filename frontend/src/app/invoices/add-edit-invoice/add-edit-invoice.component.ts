import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
import { EMPTY, first, Observable, switchMap } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-edit-invoice',
  templateUrl: './add-edit-invoice.component.html',
  styleUrls: ['./add-edit-invoice.component.scss'],
})
export class AddEditInvoiceComponent implements OnInit {
  id: string | null;
  accountingId: string | null;
  highTariff: number | undefined;
  lowTariff: number | undefined;

  accounting$: Observable<AccountingDto>;
  units$: Observable<UnitDto[]>;

  form: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private invoiceService: InvoiceService,
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private unitService: UnitService
  ) {}

  ngOnInit(): void {
    this.accountingId =
      this.activatedRoute.snapshot.queryParamMap.get('accountingId');
    this.units$ = this.unitService.getUnits();
    this.initForm();

    if (this.accountingId) {
      this.accounting$ = this.accountingService.getAccounting(
        this.accountingId
      );

      this.accounting$
        .pipe(
          switchMap((a: AccountingDto) => {
            if (a.agreementId) {
              return this.agreementService.getAgreement(a.agreementId);
            }
            return EMPTY;
          })
        )
        .pipe(first())
        .subscribe((a: AgreementDto) => {
          this.highTariff = a.highTariff;
          this.lowTariff = a.lowTariff;
        });
    }
  }

  private initForm() {
    this.form = new FormGroup({
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

    this.form
      .get('usageHighTariff')
      ?.valueChanges.subscribe((highTariff: number) => {
        const sum = highTariff + this.form.get('usageLowTariff')?.value;
        this.form.get('usageTotal')?.setValue(sum);

        const amountHighTariff = highTariff * this.highTariff!;
        this.form
          .get('amountHighTariff')
          ?.setValue(amountHighTariff.toFixed(2));
      });

    this.form
      .get('usageLowTariff')
      ?.valueChanges.subscribe((lowTariff: number) => {
        const sum = this.form.get('usageHighTariff')?.value + lowTariff;
        this.form.get('usageTotal')?.setValue(sum);

        const amountLowTariff = lowTariff * this.lowTariff!;
        this.form.get('amountLowTariff')?.setValue(amountLowTariff.toFixed(2));
      });

    this.form.get('amountHighTariff')?.valueChanges.subscribe((highTariff) => {
      const lowTariff = +this.form.get('amountLowTariff')?.value;
      const sum = +highTariff + lowTariff;
      this.form.get('amountTotal')?.setValue(sum.toFixed(2));
    });

    this.form.get('amountLowTariff')?.valueChanges.subscribe((lowTariff) => {
      const highTariff = +this.form.get('amountHighTariff')?.value;
      const sum = highTariff + +lowTariff;
      this.form.get('amountTotal')?.setValue(sum.toFixed(2));
    });
  }

  submit() {
    this.isSubmitted = true;

    if (this.form.valid) {
      const invoice = {
        ...this.form.value,
      } as ModifiableInvoiceDto;

      if (this.id) {
        this.editInvoice(invoice);
      } else {
        this.addInvoice(invoice);
      }
    }
  }

  private addInvoice(invoice: ModifiableInvoiceDto) {
    this.invoiceService
      .createInvoice(invoice)
      .pipe(first())
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
      .changeInvoice(this.id!, invoice)
      .pipe(first())
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
    this.form.reset();
  }
}
