import { Component, Input, OnInit } from '@angular/core';
import { first, Observable } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {
  AccountingDto,
  AccountingService,
  AgreementDto,
  AgreementService,
  ModifiableAccountingDto,
} from '../../../generated-source/api';
import { BreadcrumbService } from 'xng-breadcrumb';
import { DatePipe } from '@angular/common';
import { formatISO, parseISO } from 'date-fns';

@Component({
  selector: 'app-add-edit-accounting',
  templateUrl: './add-edit-accounting.component.html',
  styleUrls: ['./add-edit-accounting.component.scss'],
})
export class AddEditAccountingComponent implements OnInit {
  @Input() accounting?: AccountingDto;

  agreements$: Observable<AgreementDto[]>;

  accountingForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private datePipe: DatePipe,
    private breadcrumbService: BreadcrumbService,
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    if (this.accounting) {
      this.breadcrumbService.set(
        '@accountingSlug',
        `${this.datePipe.transform(
          this.accounting.periodFrom
        )} - ${this.datePipe.transform(this.accounting.periodUpto)}`
      );

      this.accountingForm.patchValue({
        ...this.accounting,
        periodFrom: parseISO(this.accounting.periodFrom!),
        periodUpto: parseISO(this.accounting.periodUpto!),
      });
    }

    this.agreements$ = this.agreementService.getAgreements();
  }

  private initForm() {
    this.accountingForm = this.fb.group({
      periodFrom: [null, Validators.required],
      periodUpto: [null, Validators.required],
      subject: [null, Validators.required],
      amountHighTariff: [
        null,
        [Validators.required, Validators.pattern(/^\d*[.,]?\d{0,2}$/)],
      ],
      amountLowTariff: [
        null,
        [Validators.required, Validators.pattern(/^\d*[.,]?\d{0,2}$/)],
      ],
      amountTotal: [
        null,
        [Validators.required, Validators.pattern(/^\d*[.,]?\d{0,2}$/)],
      ],
      agreementId: [null],
    });

    this.accountingForm
      .get('amountHighTariff')
      ?.valueChanges.subscribe((highTariff: number) => {
        const sum =
          highTariff + this.accountingForm.get('amountLowTariff')?.value;
        this.accountingForm.get('amountTotal')?.setValue(sum.toFixed(2));
      });

    this.accountingForm
      .get('amountLowTariff')
      ?.valueChanges.subscribe((lowTariff: number) => {
        const sum =
          this.accountingForm.get('amountHighTariff')?.value + lowTariff;
        this.accountingForm.get('amountTotal')?.setValue(sum.toFixed(2));
      });
  }

  submit() {
    this.isSubmitted = true;
    if (this.accountingForm.valid) {
      const accounting = {
        ...this.accountingForm.value,
        periodFrom: formatISO(this.accountingForm.get('periodFrom')?.value, {
          representation: 'date',
        }),
        periodUpto: formatISO(this.accountingForm.get('periodUpto')?.value, {
          representation: 'date',
        }),
      } as ModifiableAccountingDto;

      if (this.accounting?.id) {
        this.editAccounting(this.accounting?.id, accounting);
      } else {
        this.addAccounting(accounting);
      }
    }
  }

  private addAccounting(accounting: ModifiableAccountingDto) {
    this.accountingService
      .createAccounting(accounting)
      .pipe(first())
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/accountings', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editAccounting(id: string, accounting: ModifiableAccountingDto) {
    console.log(accounting);

    this.accountingService
      .changeAccounting(id, accounting)
      .pipe(first())
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/accountings', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.accountingForm.reset();
  }
}
