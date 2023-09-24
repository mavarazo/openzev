import { Component, OnInit } from '@angular/core';
import {
  defaultIfEmpty,
  first,
  forkJoin,
  map,
  Observable,
  switchMap,
} from 'rxjs';
import {
  OwnershipDto,
  OwnershipService,
  UnitDto,
  UnitService,
  UserDto,
  UserService,
} from '../../../generated-source/api';
import { ActivatedRoute, Router } from '@angular/router';

export interface CustomOwnershipDto extends OwnershipDto {
  user?: UserDto;
}

@Component({
  selector: 'app-unit',
  templateUrl: './unit.component.html',
  styleUrls: ['./unit.component.scss'],
})
export class UnitComponent implements OnInit {
  id: string | null;
  unit$: Observable<UnitDto>;
  ownerships$: Observable<CustomOwnershipDto[]>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private unitService: UnitService,
    private userService: UserService,
    private ownershipService: OwnershipService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');

    if (this.id) {
      this.unit$ = this.unitService.getUnit(this.id);
    }
    this.loadOwnership();
  }

  private loadOwnership(): void {
    if (this.id) {
      this.ownerships$ = this.ownershipService.getOwnerships(this.id).pipe(
        switchMap((ownerships: OwnershipDto[]) => {
          return forkJoin(
            ownerships
              .filter((ownership) => !!ownership.userId)
              .map((ownership: OwnershipDto) => {
                return this.userService.getUser(ownership.userId!).pipe(
                  map(
                    (user: UserDto) =>
                      ({
                        ...ownership,
                        user: user,
                      } as CustomOwnershipDto)
                  )
                );
              })
          ).pipe(defaultIfEmpty([]));
        })
      );
    }
  }

  delete() {
    if (this.id) {
      this.unitService
        .deleteUnit(this.id)
        .pipe(first())
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
        .pipe(first())
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/units', this.id]);
          },
        });
    }
  }
}
