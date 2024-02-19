import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RepresentativesRoutingModule } from './representatives-routing.module';
import { RepresentativesComponent } from './representatives.component';
import { AddEditRepresentativeComponent } from './add-edit-representative/add-edit-representative.component';
import { RepresentativeComponent } from './representative/representative.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [
    RepresentativesComponent,
    AddEditRepresentativeComponent,
    RepresentativeComponent,
  ],
  imports: [
    CommonModule,
    RepresentativesRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
  ],
})
export class RepresentativesModule {}
