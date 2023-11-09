import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ModifiableUserDto, UserService } from '../../../generated-source/api';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-add-edit-user',
  templateUrl: './add-edit-user.component.html',
  styleUrls: ['./add-edit-user.component.scss'],
})
export class AddEditUserComponent implements OnInit, OnDestroy {
  @Input() userId: string | null;

  private destroy$ = new Subject<void>();

  userForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.initForm();

    if (this.userId) {
      this.userService
        .getUser(this.userId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((user) => this.userForm.patchValue(user));
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.userForm = this.fb.group({
      contractId: [''],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', Validators.email],
      street: ['', Validators.required],
      houseNr: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required],
      phoneNr: [''],
      mobileNr: [''],
    });
  }

  submit() {
    this.isSubmitted = true;
    if (this.userForm.valid) {
      const user = { ...this.userForm.value } as ModifiableUserDto;

      if (this.userId) {
        this.editUser(user);
      } else {
        this.addUser(user);
      }
    }
  }

  private addUser(user: ModifiableUserDto) {
    this.userService
      .createUser(user)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/users', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editUser(user: ModifiableUserDto) {
    this.userService
      .changeUser(this.userId!, user)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/users', this.userId]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.userForm.reset();
  }
}
