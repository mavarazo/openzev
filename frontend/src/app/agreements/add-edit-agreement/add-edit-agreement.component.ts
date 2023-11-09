import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {
  AgreementService,
  ModifiableAgreementDto,
} from '../../../generated-source/api';

@Component({
  selector: 'app-add-edit-agreement',
  templateUrl: './add-edit-agreement.component.html',
  styleUrls: ['./add-edit-agreement.component.scss'],
})
export class AddEditAgreementComponent implements OnInit, OnDestroy {
  @Input() agreementId: string | null;

  private destroy$ = new Subject<void>();

  agreementForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private agreementService: AgreementService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    if (this.agreementId) {
      this.agreementService
        .getAgreement(this.agreementId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((agreement) => {
          this.agreementForm.patchValue(agreement);
        });
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.agreementForm = this.fb.group({
      periodFrom: [null, Validators.required],
      periodUpto: [null, Validators.required],
      highTariff: [
        null,
        [Validators.required, Validators.pattern(/^\d*[.,]?\d{0,2}$/)],
      ],
      lowTariff: [
        null,
        [Validators.required, Validators.pattern(/^\d*[.,]?\d{0,2}$/)],
      ],
      approved: [null],
    });
  }

  submit() {
    this.isSubmitted = true;
    if (this.agreementForm.valid) {
      const agreement = {
        ...this.agreementForm.value,
      } as ModifiableAgreementDto;

      if (this.agreementId) {
        this.editAgreement(agreement);
      } else {
        this.addAgreement(agreement);
      }
    }
  }

  private addAgreement(agreement: ModifiableAgreementDto) {
    this.agreementService
      .createAgreement(agreement)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/agreements', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editAgreement(agreement: ModifiableAgreementDto) {
    this.agreementService
      .changeAgreement(this.agreementId!, agreement)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/agreements', this.agreementId]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.agreementForm.reset();
  }
}
