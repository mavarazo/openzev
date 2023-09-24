import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  combineLatest,
  EMPTY,
  first,
  forkJoin,
  map,
  Observable,
  of,
  switchMap,
} from 'rxjs';
import {
  AccountingDto,
  AccountingService,
  AgreementDto,
  AgreementService,
  InvoiceDto,
  InvoiceService,
  OwnershipDto,
  OwnershipService,
  UnitDto,
  UnitService,
  UserDto,
  UserService,
} from '../../../generated-source/api';

export interface CustomOwnershipDto extends OwnershipDto {
  user?: UserDto;
}

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss'],
})
export class InvoiceComponent implements OnInit {
  id: string | null;
  highTariff: number | undefined;
  lowTariff: number | undefined;

  invoice$: Observable<InvoiceDto>;
  accounting$: Observable<AccountingDto>;
  unit$: Observable<UnitDto>;
  ownerships$: Observable<CustomOwnershipDto[]>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private invoiceService: InvoiceService,
    private accountingService: AccountingService,
    private unitService: UnitService,
    private userService: UserService,
    private ownershipService: OwnershipService,
    private agreementService: AgreementService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');

    if (this.id) {
      this.invoice$ = this.invoiceService.getInvoice(this.id);

      this.accounting$ = this.invoice$.pipe(
        switchMap((i) => this.accountingService.getAccounting(i.accountingId!))
      );

      this.unit$ = this.invoice$.pipe(
        switchMap((i) => this.unitService.getUnit(i.unitId!))
      );

      this.ownerships$ = combineLatest([this.accounting$, this.unit$]).pipe(
        switchMap(([a, u]) =>
          this.ownershipService
            .getOwnerships(u.id!, a.periodFrom, a.periodUpto)
            .pipe(
              switchMap((ownerships: OwnershipDto[]) =>
                forkJoin(
                  ownerships.map((ownership: OwnershipDto) =>
                    this.loadUserForOwnership(ownership)
                  )
                )
              )
            )
        )
      );

      this.accounting$
        .pipe(
          switchMap((a: AccountingDto) => {
            if (a.agreementId) {
              return this.agreementService.getAgreement(a.agreementId);
            }
            return EMPTY;
          })
        )
        .pipe(first())
        .subscribe((a: AgreementDto) => {
          this.highTariff = a.highTariff;
          this.lowTariff = a.lowTariff;
        });
    }
  }

  private loadUserForOwnership(
    ownership: OwnershipDto
  ): Observable<CustomOwnershipDto> {
    if (ownership.userId) {
      return this.userService
        .getUser(ownership.userId)
        .pipe(
          map((user) => ({ ...ownership, user: user } as CustomOwnershipDto))
        );
    }
    return of(ownership);
  }

  delete() {}
}
