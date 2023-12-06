import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ModifiableUnitDto, UnitService } from '../../../generated-source/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-add-edit-unit',
  templateUrl: './add-edit-unit.component.html',
  styleUrls: ['./add-edit-unit.component.scss'],
})
export class AddEditUnitComponent implements OnInit, OnDestroy {
  @Input() unitId: string | null;

  private destroy$ = new Subject<void>();

  unitForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(private unitService: UnitService, private router: Router) {}

  ngOnInit(): void {
    this.initForm();

    if (this.unitId) {
      this.unitService
        .getUnit(this.unitId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((unit) => {
          this.unitForm.patchValue(unit);
        });
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.unitForm = new FormBuilder().group({
      subject: [null, Validators.required],
      valueRatio: [null, Validators.pattern('^[0-9]*$')],
      mpan: [null],
    });
  }

  submit() {
    this.isSubmitted = true;
    if (this.unitForm.valid) {
      const unit = {
        ...this.unitForm.value,
      } as ModifiableUnitDto;

      if (this.unitId) {
        this.editUnit(unit);
      } else {
        this.addUnit(unit);
      }
    }
  }

  private addUnit(unit: ModifiableUnitDto) {
    this.unitService
      .createUnit(unit)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['units', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editUnit(unit: ModifiableUnitDto) {
    this.unitService
      .changeUnit(this.unitId!, unit)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['units', this.unitId]);
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
