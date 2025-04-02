import {
    ApplicationConfig,
    makeEnvironmentProviders,
    provideZoneChangeDetection,
} from '@angular/core'
import { provideRouter, withComponentInputBinding } from '@angular/router'

import { routes } from './app.routes'
import { provideStore } from '@ngrx/store'
import { BASE_PATH } from '../generated-source/api'
import { environment } from '../environments/environment'
import { provideHttpClient } from '@angular/common/http'

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes, withComponentInputBinding()),
        provideHttpClient(),
        makeEnvironmentProviders([
            {
                provide: BASE_PATH,
                useValue: environment.apiUrl,
            },
        ]),
        provideStore(),
    ],
}
