import { ResolveFn } from '@angular/router';
import {
  InvoiceDto,
  InvoiceService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { inject } from '@angular/core';
import { defaultIfEmpty, forkJoin, map, of, switchMap } from 'rxjs';

export interface CustomInvoiceDto extends InvoiceDto {
  unit?: UnitDto;
}

export const invoicesResolver: ResolveFn<CustomInvoiceDto[]> = (
  route,
  state,
  invoiceService: InvoiceService = inject(InvoiceService),
  unitService: UnitService = inject(UnitService)
) => {
  const accountingId = route.paramMap.get('id')!;

  function loadUnitForInvoice(invoice: CustomInvoiceDto) {
    if (invoice.unitId) {
      return unitService
        .getUnit(invoice.unitId)
        .pipe(map((unit) => ({ ...invoice, unit: unit } as CustomInvoiceDto)));
    }
    return of({ ...invoice } as CustomInvoiceDto);
  }

  return invoiceService
    .getInvoices(accountingId)
    .pipe(
      switchMap((invoices: InvoiceDto[]) =>
        forkJoin(
          invoices.map((invoice: InvoiceDto) => loadUnitForInvoice(invoice))
        ).pipe(defaultIfEmpty([]))
      )
    );
};
