import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject, takeUntil } from 'rxjs';
import {
  ErrorDto,
  ModifiableOwnershipDto,
  OwnershipService,
  UnitDto,
  UnitService,
  UserDto,
  UserService,
} from '../../../generated-source/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { formatISO } from 'date-fns';

@Component({
  selector: 'app-add-edit-ownership',
  templateUrl: './add-edit-ownership.component.html',
  styleUrls: ['./add-edit-ownership.component.scss'],
})
export class AddEditOwnershipComponent implements OnInit, OnDestroy {
  @Input() unitId: string | null;
  @Input() ownershipId: string | null;

  private destroy$ = new Subject<void>();

  unit$: Observable<UnitDto>;
  users$: Observable<UserDto[]>;

  ownershipForm: FormGroup;
  isSubmitted: boolean = false;
  errors: string[] = [];

  constructor(
    private unitService: UnitService,
    private userService: UserService,
    private ownershipService: OwnershipService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    this.users$ = this.userService.getUsers();

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
    this.ownershipForm = this.fb.group({
      userId: [null, Validators.required],
      periodFrom: [null, Validators.required],
      periodUpto: [null],
    });
  }

  submit() {
    if (this.ownershipForm.valid) {
      const ownership = {
        userId: this.ownershipForm.get('userId')?.value,
        periodFrom: this.formatDateAsISO(
          this.ownershipForm.get('periodFrom')?.value
        ),
        periodUpto: this.formatDateAsISO(
          this.ownershipForm.get('periodUpto')?.value
        ),
      } as ModifiableOwnershipDto;

      if (this.ownershipId) {
        this.edit(ownership);
      } else {
        this.add(ownership);
      }
    }
  }

  private formatDateAsISO(value: string): string | null {
    return value
      ? formatISO(new Date(value), {
          representation: 'date',
        })
      : null;
  }

  private add(ownership: ModifiableOwnershipDto) {
    this.ownershipService
      .createOwnership(this.unitId!, ownership)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/units', this.unitId]);
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
          this.router.navigate(['/units', this.unitId]);
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
