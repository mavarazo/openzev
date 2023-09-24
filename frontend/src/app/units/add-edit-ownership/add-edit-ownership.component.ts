import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { first, Observable, Subscription } from 'rxjs';
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
  private unitId: string | null;
  private ownershipId: string | null;
  private subscription: Subscription;

  unit$: Observable<UnitDto>;
  users$: Observable<UserDto[]>;

  ownershipForm: FormGroup;
  isSubmitted: boolean = false;
  errors: string[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private unitService: UnitService,
    private userService: UserService,
    private ownershipService: OwnershipService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.unitId = this.activatedRoute.snapshot.paramMap.get('id');
    this.ownershipId = this.activatedRoute.snapshot.paramMap.get('ownershipId');
    this.initForm();

    this.users$ = this.userService.getUsers();

    if (this.unitId) {
      this.unit$ = this.unitService.getUnit(this.unitId);
    }
    if (this.ownershipId) {
      this.subscription = this.ownershipService
        .getOwnership(this.ownershipId)
        .pipe(first())
        .subscribe((ownership) => {
          this.ownershipForm.patchValue(ownership);
        });
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
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
        userId: this.ownershipForm.get('user')?.value,
        periodFrom: this.ownershipForm.get('periodFrom')?.value
          ? formatISO(new Date(this.ownershipForm.get('periodFrom')?.value), {
              representation: 'date',
            })
          : null,
        periodUpto: this.ownershipForm.get('periodUpto')?.value
          ? formatISO(new Date(this.ownershipForm.get('periodUpto')?.value), {
              representation: 'date',
            })
          : null,
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
      .createOwnership(this.unitId!, ownership)
      .pipe(first())
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
      .pipe(first())
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
