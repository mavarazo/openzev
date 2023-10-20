import { ResolveFn } from '@angular/router';
import { EMPTY, Observable } from 'rxjs';
import { AccountingService } from '../../../generated-source/api';
import { inject } from '@angular/core';

export const accountingResolver: ResolveFn<any> = (
  route,
  state,
  accountingService: AccountingService = inject(AccountingService)
): Observable<any> => {
  const id = route.paramMap.get('accountingId');
  return id ? accountingService.getAccounting(id) : EMPTY;
};
