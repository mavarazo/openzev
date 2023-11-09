import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { BASE_PATH } from '../generated-source/api';
import { HttpClientModule } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import localeDeCh from '@angular/common/locales/de-CH';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BreadcrumbModule } from 'xng-breadcrumb';
import { CoreModule } from './core/core.module';

registerLocaleData(localeDeCh);

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule,
    AppRoutingModule,
    HttpClientModule,
    BreadcrumbModule,
    CoreModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'de-CH' },
    {
      provide: BASE_PATH,
      useValue: '/api',
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
