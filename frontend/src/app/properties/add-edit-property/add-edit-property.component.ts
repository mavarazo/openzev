import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {
  ModifiablePropertyDto,
  PropertyService,
} from '../../../generated-source/api';

@Component({
  selector: 'app-add-edit-property',
  templateUrl: './add-edit-property.component.html',
  styleUrls: ['./add-edit-property.component.scss'],
})
export class AddEditPropertyComponent implements OnInit, OnDestroy {
  @Input() propertyId: string | null;

  private destroy$ = new Subject<void>();

  propertyForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private router: Router,
    private propertyService: PropertyService
  ) {}

  ngOnInit(): void {
    this.initForm();

    if (this.propertyId) {
      this.propertyService
        .getProperty(this.propertyId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((property) => this.propertyForm.patchValue(property));
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.propertyForm = new FormBuilder().group({
      street: ['', Validators.required],
      houseNr: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required],
    });
  }

  submit() {
    this.isSubmitted = true;
    if (this.propertyForm.valid) {
      const property = { ...this.propertyForm.value } as ModifiablePropertyDto;

      if (this.propertyId) {
        this.editProperty(property);
      } else {
        this.addProperty(property);
      }
    }
  }

  private addProperty(property: ModifiablePropertyDto) {
    this.propertyService
      .createProperty(property)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/properties', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editProperty(property: ModifiablePropertyDto) {
    this.propertyService
      .changeProperty(this.propertyId!, property)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/properties', this.propertyId]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.propertyForm.reset();
  }
}
