import { ResolveFn } from '@angular/router';
import {
  AccountingDto,
  AccountingService,
} from '../../../generated-source/api';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';

export const accountingsResolver: ResolveFn<AccountingDto[]> = (
  route,
  state,
  accountingService: AccountingService = inject(AccountingService)
): Observable<AccountingDto[]> => {
  return accountingService.getAccountings();
};
