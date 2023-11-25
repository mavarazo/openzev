import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject, takeUntil } from 'rxjs';
import {
  ErrorDto,
  ModifiableOwnershipDto,
  OwnerDto,
  OwnerService,
  OwnershipService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-edit-ownership',
  templateUrl: './add-edit-ownership.component.html',
  styleUrls: ['./add-edit-ownership.component.scss'],
})
export class AddEditOwnershipComponent implements OnInit, OnDestroy {
  @Input() ownershipId: string | null;
  @Input() unitId: string;
  @Input() propertyId: string;

  private destroy$ = new Subject<void>();

  unit$: Observable<UnitDto>;
  owners$: Observable<OwnerDto[]>;

  ownershipForm: FormGroup;
  isSubmitted: boolean = false;
  errors: string[] = [];

  constructor(
    private unitService: UnitService,
    private ownerService: OwnerService,
    private ownershipService: OwnershipService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    this.owners$ = this.ownerService.getOwners(this.propertyId);

    if (this.unitId) {
      this.unit$ = this.unitService.getUnit(this.unitId);
    }
    if (this.ownershipId) {
      this.ownershipService
        .getOwnership(this.ownershipId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((ownership) => {
          this.ownershipForm.patchValue(ownership);
        });
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.ownershipForm = new FormBuilder().group({
      ownerId: [null, Validators.required],
      periodFrom: [null, Validators.required],
    });
  }

  submit() {
    if (this.ownershipForm.valid) {
      const ownership = {
        ...this.ownershipForm.value,
      } as ModifiableOwnershipDto;

      if (this.ownershipId) {
        this.edit(ownership);
      } else {
        this.add(ownership);
      }
    }
  }

  private add(ownership: ModifiableOwnershipDto) {
    this.ownershipService
      .createOwnership(this.unitId, ownership)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate([
            'properties',
            this.propertyId,
            'units',
            this.unitId,
          ]);
        },
        error: (error) => {
          console.error(error);
          this.showValidationError(error.error);
        },
      });
  }

  private edit(ownership: ModifiableOwnershipDto) {
    this.ownershipService
      .changeOwnership(this.ownershipId!, ownership)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate([
            'properties',
            this.propertyId,
            'units',
            this.unitId,
          ]);
        },
        error: (error) => {
          console.error(error);
          this.showValidationError(error.error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.ownershipForm.reset();
  }

  showValidationError(error: ErrorDto): void {
    if (error && error.code) {
      this.errors.push(error.code);
    }
  }
}
