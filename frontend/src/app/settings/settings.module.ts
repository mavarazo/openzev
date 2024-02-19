import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SettingsRoutingModule } from './settings-routing.module';
import { SettingsComponent } from './settings.component';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { AddEditSettingsComponent } from './add-edit-settings/add-edit-settings.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [SettingsComponent, AddEditSettingsComponent],
  imports: [
    CommonModule,
    SettingsRoutingModule,
    NgbNavModule,
    ReactiveFormsModule,
  ],
})
export class SettingsModule {}
