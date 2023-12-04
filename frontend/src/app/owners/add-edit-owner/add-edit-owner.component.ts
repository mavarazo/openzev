import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  ModifiableOwnerDto,
  OwnerService,
} from '../../../generated-source/api';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-add-edit-owner',
  templateUrl: './add-edit-owner.component.html',
  styleUrls: ['./add-edit-owner.component.scss'],
})
export class AddEditOwnerComponent implements OnInit, OnDestroy {
  @Input() ownerId: string | null;

  private destroy$ = new Subject<void>();

  ownerForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(private ownerService: OwnerService, private router: Router) {}

  ngOnInit(): void {
    this.initForm();

    if (this.ownerId) {
      this.ownerService
        .getOwner(this.ownerId)
        .pipe(takeUntil(this.destroy$))
        .subscribe((owner) => this.ownerForm.patchValue(owner));
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.ownerForm = new FormBuilder().group({
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
    if (this.ownerForm.valid) {
      const owner = { ...this.ownerForm.value } as ModifiableOwnerDto;

      if (this.ownerId) {
        this.editOwner(owner);
      } else {
        this.addOwner(owner);
      }
    }
  }

  private addOwner(owner: ModifiableOwnerDto) {
    this.ownerService
      .createOwner(owner)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (id) => {
          this.reset();
          this.router.navigate(['/owners', id]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  private editOwner(owner: ModifiableOwnerDto) {
    this.ownerService
      .changeOwner(this.ownerId!, owner)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.reset();
          this.router.navigate(['/owners', this.ownerId]);
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.ownerForm.reset();
  }
}
