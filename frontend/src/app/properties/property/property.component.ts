import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject, takeUntil } from 'rxjs';
import {
  PropertyDto,
  PropertyService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { Router } from '@angular/router';

@Component({
  selector: 'app-property',
  templateUrl: './property.component.html',
  styleUrls: ['./property.component.scss'],
})
export class PropertyComponent implements OnInit, OnDestroy {
  @Input() propertyId: string;

  private destroy$ = new Subject<void>();

  property$: Observable<PropertyDto>;
  units$: Observable<UnitDto[]>;

  constructor(
    private router: Router,
    private propertyService: PropertyService,
    private unitService: UnitService
  ) {}

  ngOnInit(): void {
    this.property$ = this.propertyService.getProperty(this.propertyId);
    this.units$ = this.unitService.getUnits(this.propertyId);
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  delete() {
    this.propertyService
      .deleteProperty(this.propertyId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        error: console.error,
        complete: () => {
          this.router.navigate(['/properties']);
        },
      });
  }
}
