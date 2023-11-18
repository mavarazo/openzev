import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PropertiesRoutingModule } from './properties-routing.module';
import { PropertiesComponent } from './properties.component';
import { AddEditPropertyComponent } from './add-edit-property/add-edit-property.component';
import { ReactiveFormsModule } from '@angular/forms';
import { PropertyComponent } from './property/property.component';
import { UnitsModule } from '../units/units.module';

@NgModule({
  declarations: [
    PropertiesComponent,
    AddEditPropertyComponent,
    PropertyComponent,
  ],
  imports: [
    CommonModule,
    PropertiesRoutingModule,
    ReactiveFormsModule,
    UnitsModule,
  ],
})
export class PropertiesModule {}
