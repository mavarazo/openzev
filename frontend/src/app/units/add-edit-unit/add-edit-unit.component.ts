import { Component, OnDestroy, OnInit } from '@angular/core';
import { ModifiableUnitDto, UnitService } from '../../../generated-source/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { first, Subscription } from 'rxjs';

@Component({
  selector: 'app-add-edit-unit',
  templateUrl: './add-edit-unit.component.html',
  styleUrls: ['./add-edit-unit.component.scss'],
})
export class AddEditUnitComponent implements OnInit, OnDestroy {
  id: string | null;
  private subscription: Subscription;

  unitForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private unitService: UnitService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.initForm();

    if (this.id) {
      this.subscription = this.unitService
        .getUnit(this.id)
        .pipe(first())
        .subscribe((unit) => {
          this.unitForm.patchValue(unit);
        });
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private initForm() {
    this.unitForm = this.fb.group({
      subject: [null, Validators.required],
      valueRatio: [null, Validators.pattern('^[0-9]*$')],
      mpan: [null],
    });
  }

  submit() {
    this.isSubmitted = true;
    if (this.unitForm.valid) {
      const unit = { ...this.unitForm.value } as ModifiableUnitDto;

      if (this.id) {
        this.editUnit(unit);
      } else {
        this.addUnit(unit);
      }
    }
  }

  private addUnit(unit: ModifiableUnitDto) {
    this.unitService
      .createUnit(unit)
      .pipe(first())
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/units', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editUnit(unit: ModifiableUnitDto) {
    this.unitService
      .changeUnit(this.id!, unit)
      .pipe(first())
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/units', this.id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.unitForm.reset();
  }
}
