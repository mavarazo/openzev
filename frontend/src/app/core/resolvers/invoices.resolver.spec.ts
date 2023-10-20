import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { invoicesResolver } from './invoices.resolver';

describe('invoicesResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => invoicesResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
