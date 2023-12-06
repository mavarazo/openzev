import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject, takeUntil } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {
  AccountingService,
  AgreementDto,
  AgreementService,
  ModifiableAccountingDto,
} from '../../../generated-source/api';

@Component({
  selector: 'app-add-edit-accounting',
  templateUrl: './add-edit-accounting.component.html',
  styleUrls: ['./add-edit-accounting.component.scss'],
})
export class AddEditAccountingComponent implements OnInit, OnDestroy {
  @Input() accountingId: string | undefined;

  private destroy$ = new Subject<void>();

  agreements$: Observable<AgreementDto[]>;

  accountingForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    if (this.accountingId) {
      this.accountingService
        .getAccounting(this.accountingId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((accounting) => {
          this.accountingForm.patchValue(accounting);
        });
    }

    this.agreements$ = this.agreementService.getAgreements();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.accountingForm = new FormBuilder().group({
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
      } as ModifiableAccountingDto;

      if (this.accountingId) {
        this.editAccounting(this.accountingId, accounting);
      } else {
        this.addAccounting(accounting);
      }
    }
  }

  private addAccounting(accounting: ModifiableAccountingDto) {
    this.accountingService
      .createAccounting(accounting)
      .pipe(takeUntil(this.destroy$))
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
      .pipe(takeUntil(this.destroy$))
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
