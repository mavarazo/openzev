import { Component } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  ConfigService,
  ModifiableZevRepresentativeConfigDto,
} from '../../../generated-source/api';

@Component({
  selector: 'app-zev-representative-config',
  templateUrl: './zev-representative-config.component.html',
  styleUrls: ['./zev-representative-config.component.scss'],
})
export class ZevRepresentativeConfigComponent {
  private destroy$ = new Subject<void>();

  zevRepresentativeConfigForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(private configService: ConfigService) {}

  ngOnInit(): void {
    this.initForm();

    this.configService
      .getZevRepresentativeConfig()
      .pipe(takeUntil(this.destroy$))
      .subscribe((owner) => this.zevRepresentativeConfigForm.patchValue(owner));
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.zevRepresentativeConfigForm = new FormBuilder().group({
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
    if (this.zevRepresentativeConfigForm.valid) {
      const zevRepresentativeConfigDto = {
        ...this.zevRepresentativeConfigForm.value,
      } as ModifiableZevRepresentativeConfigDto;

      this.configService
        .saveZevRepresentativeConfig(zevRepresentativeConfigDto)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => (this.isSubmitted = false));
    }
  }
}
