import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersRoutingModule } from './users-routing.module';
import { UsersComponent } from './users.component';
import {
  NbActionsModule,
  NbAlertModule,
  NbButtonGroupModule,
  NbButtonModule,
  NbCardModule,
  NbIconModule,
} from '@nebular/theme';
import { ReactiveFormsModule } from '@angular/forms';
import { UserComponent } from './user/user.component';
import { AddEditUserComponent } from './add-edit-user/add-edit-user.component';

@NgModule({
  declarations: [UsersComponent, UserComponent, AddEditUserComponent],
  imports: [
    CommonModule,
    UsersRoutingModule,
    ReactiveFormsModule,
    NbButtonModule,
    NbCardModule,
    NbAlertModule,
    NbIconModule,
    NbActionsModule,
    NbButtonGroupModule,
  ],
})
export class UsersModule {}
