import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
  defaultIfEmpty,
  forkJoin,
  map,
  Observable,
  Subject,
  switchMap,
  takeUntil,
} from 'rxjs';
import {
  OwnerDto,
  OwnerService,
  OwnershipDto,
  OwnershipService,
  PropertyDto,
  PropertyService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { Router } from '@angular/router';

export interface CustomOwnershipDto extends OwnershipDto {
  owner?: OwnerDto;
}

@Component({
  selector: 'app-unit',
  templateUrl: './unit.component.html',
  styleUrls: ['./unit.component.scss'],
})
export class UnitComponent implements OnInit, OnDestroy {
  @Input() unitId: string;
  @Input() propertyId: string;

  private destroy$ = new Subject<void>();

  unit$: Observable<UnitDto>;
  property$: Observable<PropertyDto>;
  ownerships$: Observable<CustomOwnershipDto[]>;

  constructor(
    private router: Router,
    private unitService: UnitService,
    private propertyService: PropertyService,
    private ownerService: OwnerService,
    private ownershipService: OwnershipService
  ) {}

  ngOnInit(): void {
    this.unit$ = this.unitService.getUnit(this.unitId);
    this.property$ = this.propertyService.getProperty(this.propertyId);

    this.loadOwnership();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadOwnership(): void {
    if (this.unitId) {
      this.ownerships$ = this.ownershipService.getOwnerships(this.unitId).pipe(
        switchMap((ownerships: OwnershipDto[]) => {
          return forkJoin(
            ownerships
              .filter((ownership) => !!ownership.ownerId)
              .map((ownership: OwnershipDto) => {
                return this.ownerService.getOwner(ownership.ownerId!).pipe(
                  map(
                    (owner: OwnerDto) =>
                      ({
                        ...ownership,
                        owner: owner,
                      } as CustomOwnershipDto)
                  )
                );
              })
          ).pipe(defaultIfEmpty([]));
        }),
        takeUntil(this.destroy$)
      );
    }
  }

  delete() {
    if (this.unitId) {
      this.unitService
        .deleteUnit(this.unitId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/units']);
          },
        });
    }
  }

  deleteOwnership(ownership: CustomOwnershipDto) {
    if (ownership?.id) {
      this.ownershipService
        .deleteOwnership(ownership.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          error: console.error,
          complete: () => {
            this.loadOwnership();
          },
        });
    }
  }
}
