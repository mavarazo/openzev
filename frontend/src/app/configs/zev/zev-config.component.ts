import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  ConfigService,
  ModifiableZevConfigDto,
} from '../../../generated-source/api';

@Component({
  selector: 'app-zev-config',
  templateUrl: './zev-config.component.html',
  styleUrls: ['./zev-config.component.scss'],
})
export class ZevConfigComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  zevConfigForm: FormGroup;
  isSubmitted: boolean = false;

  constructor(private configService: ConfigService) {}

  ngOnInit(): void {
    this.initForm();

    this.configService
      .getZevConfig()
      .pipe(takeUntil(this.destroy$))
      .subscribe((owner) => this.zevConfigForm.patchValue(owner));
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.zevConfigForm = new FormBuilder().group({
      name: ['', Validators.required],
      street: ['', Validators.required],
      houseNr: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required],
      propertyNr: [''],
    });
  }

  submit() {
    this.isSubmitted = true;
    if (this.zevConfigForm.valid) {
      const zevConfig = {
        ...this.zevConfigForm.value,
      } as ModifiableZevConfigDto;

      this.configService
        .saveZevConfig(zevConfig)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => (this.isSubmitted = false));
    }
  }
}
