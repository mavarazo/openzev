import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  ModifiableSettingsDto,
  SettingsDto,
  SettingService,
} from '../../../generated-source/api';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-edit-config',
  templateUrl: './add-edit-settings.component.html',
  styleUrls: ['./add-edit-settings.component.scss'],
})
export class AddEditSettingsComponent extends AbstractAddEditComponent<
  SettingsDto,
  ModifiableSettingsDto
> {
  override get isEdit(): boolean {
    return true;
  }

  constructor(private router: Router, private settingService: SettingService) {
    super();
  }

  override initForm() {
    return new FormGroup({
      name: new FormControl('', Validators.required),
      street: new FormControl('', Validators.required),
      houseNr: new FormControl('', Validators.required),
      postalCode: new FormControl('', Validators.required),
      city: new FormControl('', Validators.required),
      propertyNr: new FormControl(''),
    });
  }

  override fetchEntity(): Observable<SettingsDto> {
    return this.settingService.getSettings();
  }

  createEntity(entity: ModifiableSettingsDto): Observable<any> {
    throw new Error('Method not implemented.');
  }

  onSuccessfullCreate(result: any): void {
    throw new Error('Method not implemented.');
  }

  override changeEntity(entity: ModifiableSettingsDto): Observable<any> {
    return this.settingService.saveSettings(entity);
  }

  override onSuccessfullChange(result: any) {
    this.router.navigate(['/settings']);
  }
}
