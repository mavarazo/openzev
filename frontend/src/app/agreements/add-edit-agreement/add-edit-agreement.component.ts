import { Component, OnDestroy, OnInit } from '@angular/core';
import { first, Subscription } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
  id: string | null;
  private subscription: Subscription;

  agreementForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private agreementService: AgreementService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.initForm();

    if (this.id) {
      this.subscription = this.agreementService
        .getAgreement(this.id)
        .pipe(first())
        .subscribe((agreement) => {
          this.agreementForm.patchValue(agreement);
        });
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
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

      if (this.id) {
        this.editAgreement(agreement);
      } else {
        this.addAgreement(agreement);
      }
    }
  }

  private addAgreement(agreement: ModifiableAgreementDto) {
    this.agreementService
      .createAgreement(agreement)
      .pipe(first())
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
      .changeAgreement(this.id!, agreement)
      .pipe(first())
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/agreements', this.id]);
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
