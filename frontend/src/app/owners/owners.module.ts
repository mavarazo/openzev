import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OwnersRoutingModule } from './owners-routing.module';
import { OwnersComponent } from './owners.component';
import { ReactiveFormsModule } from '@angular/forms';
import { OwnerComponent } from './owner/owner.component';
import { AddEditOwnerComponent } from './add-edit-owner/add-edit-owner.component';

@NgModule({
  declarations: [OwnersComponent, OwnerComponent, AddEditOwnerComponent],
  imports: [CommonModule, OwnersRoutingModule, ReactiveFormsModule],
})
export class OwnersModule {}
