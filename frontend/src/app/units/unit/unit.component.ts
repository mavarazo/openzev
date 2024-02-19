import { Component, Input } from '@angular/core';
import {
  defaultIfEmpty,
  forkJoin,
  map,
  Observable,
  switchMap,
  takeUntil,
} from 'rxjs';
import {
  OwnerDto,
  OwnerService,
  OwnershipDto,
  OwnershipService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { Router } from '@angular/router';
import { AbstractDetailComponent } from '../../shared/components/abstract-detail.component';

export interface CustomOwnershipDto extends OwnershipDto {
  owner?: OwnerDto;
}

@Component({
  selector: 'app-unit',
  templateUrl: './unit.component.html',
  styleUrls: ['./unit.component.scss'],
})
export class UnitComponent extends AbstractDetailComponent<UnitDto> {
  @Input() unitId: string;

  ownerships$: Observable<CustomOwnershipDto[]>;

  constructor(
    private router: Router,
    private unitService: UnitService,
    private ownerService: OwnerService,
    private ownershipService: OwnershipService
  ) {
    super();
  }

  fetchEntity(): Observable<UnitDto> {
    return this.unitService.getUnit(this.unitId);
  }

  deleteEntity(): Observable<any> {
    return this.unitService.deleteUnit(this.unitId);
  }

  onSuccessfullDelete(): void {
    this.router.navigate(['/units']);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.loadOwnership();
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
