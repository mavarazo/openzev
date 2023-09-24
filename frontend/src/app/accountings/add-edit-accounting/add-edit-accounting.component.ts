import { Component, OnDestroy, OnInit } from '@angular/core';
import { first, Observable, Subscription } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AccountingService,
  AgreementDto,
  AgreementService,
  ModifiableAccountingDto,
  ModifiableUnitDto,
} from '../../../generated-source/api';

@Component({
  selector: 'app-add-edit-accounting',
  templateUrl: './add-edit-accounting.component.html',
  styleUrls: ['./add-edit-accounting.component.scss'],
})
export class AddEditAccountingComponent implements OnInit, OnDestroy {
  id: string | null;
  private subscription: Subscription;

  agreements$: Observable<AgreementDto[]>;

  accountingForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.initForm();

    this.agreements$ = this.agreementService.getAgreements();

    if (this.id) {
      this.subscription = this.accountingService
        .getAccounting(this.id)
        .pipe(first())
        .subscribe((accounting) => {
          this.accountingForm.patchValue(accounting);
        });
    }
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
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
      } as ModifiableAccountingDto;

      if (this.id) {
        this.editAccounting(accounting);
      } else {
        this.addAccounting(accounting);
      }
    }
  }

  private addAccounting(accounting: ModifiableUnitDto) {
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

  private editAccounting(accounting: ModifiableUnitDto) {
    this.accountingService
      .changeAccounting(this.id!, accounting)
      .pipe(first())
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/accountings', this.id]);
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
