import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ConfigsRoutingModule } from './configs-routing.module';
import { ConfigsComponent } from './configs.component';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { ZevConfigComponent } from './zev/zev-config.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ZevRepresentativeConfigComponent } from './zev-representative-config/zev-representative-config.component';

@NgModule({
  declarations: [ConfigsComponent, ZevConfigComponent, ZevRepresentativeConfigComponent],
  imports: [
    CommonModule,
    ConfigsRoutingModule,
    NgbNavModule,
    ReactiveFormsModule,
  ],
})
export class ConfigsModule {}
