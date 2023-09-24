import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  ModifiableUserDto,
  UserDto,
  UserService,
} from '../../../generated-source/api';
import { ActivatedRoute, Router } from '@angular/router';
import { first } from 'rxjs';

@Component({
  selector: 'app-add-edit-user',
  templateUrl: './add-edit-user.component.html',
  styleUrls: ['./add-edit-user.component.scss'],
})
export class AddEditUserComponent implements OnInit {
  private id: string | null;
  private user: UserDto | null;

  userForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.initForm();

    if (this.id) {
      this.userService
        .getUser(this.id)
        .pipe(first())
        .subscribe((user) => {
          this.user = user;
          this.userForm.patchValue(user);
        });
    }
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

      if (this.id) {
        this.editUser(user);
      } else {
        this.addUser(user);
      }
    }
  }

  private addUser(user: ModifiableUserDto) {
    this.userService
      .createUser(user)
      .pipe(first())
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
      .changeUser(this.id!, user)
      .pipe(first())
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/users', this.id]);
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
