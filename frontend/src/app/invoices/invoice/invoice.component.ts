import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
  EMPTY,
  forkJoin,
  map,
  Observable,
  of,
  share,
  Subject,
  switchMap,
  takeUntil,
} from 'rxjs';
import {
  AccountingDto,
  AccountingService,
  AgreementDto,
  AgreementService,
  InvoiceDto,
  InvoiceService,
  OwnerDto,
  OwnerService,
  OwnershipDto,
  OwnershipService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';

export interface CustomOwnershipDto extends OwnershipDto {
  owner?: OwnerDto;
}

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss'],
})
export class InvoiceComponent implements OnInit, OnDestroy {
  @Input() invoiceId: string;

  private destroy$ = new Subject<void>();

  highTariff: number | undefined;
  lowTariff: number | undefined;

  invoice$: Observable<InvoiceDto>;
  accounting$: Observable<AccountingDto>;
  unit$: Observable<UnitDto>;
  ownerships$: Observable<CustomOwnershipDto[]>;

  constructor(
    private invoiceService: InvoiceService,
    private accountingService: AccountingService,
    private unitService: UnitService,
    private ownerService: OwnerService,
    private ownershipService: OwnershipService,
    private agreementService: AgreementService
  ) {}

  ngOnInit(): void {
    this.invoice$ = this.invoiceService
      .getInvoice(this.invoiceId)
      .pipe(share(), takeUntil(this.destroy$));

    this.accounting$ = this.invoice$.pipe(
      switchMap((i) => this.accountingService.getAccounting(i.accountingId!)),
      takeUntil(this.destroy$)
    );

    this.unit$ = this.invoice$.pipe(
      switchMap((i) => this.unitService.getUnit(i.unitId!)),
      takeUntil(this.destroy$)
    );

    this.unit$.pipe(
      switchMap((u) =>
        this.ownershipService
          .getOwnerships(u.id!)
          .pipe(
            switchMap((ownerships) =>
              forkJoin(
                ownerships.map((ownership) =>
                  this.loadOwnerForOwnership(ownership)
                )
              )
            )
          )
      ),
      takeUntil(this.destroy$)
    );

    this.accounting$
      .pipe(
        switchMap((a: AccountingDto) => {
          if (a.agreementId) {
            return this.agreementService.getAgreement(a.agreementId);
          }
          return EMPTY;
        }),
        takeUntil(this.destroy$)
      )
      .subscribe((a: AgreementDto) => {
        this.highTariff = a.highTariff;
        this.lowTariff = a.lowTariff;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadOwnerForOwnership(
    ownership: OwnershipDto
  ): Observable<CustomOwnershipDto> {
    if (ownership.ownerId) {
      return this.ownerService.getOwner(ownership.ownerId).pipe(
        map((owner) => ({ ...ownership, owner: owner } as CustomOwnershipDto)),
        takeUntil(this.destroy$)
      );
    }
    return of(ownership);
  }

  delete() {}
}
