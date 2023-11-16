import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { OwnerDto, OwnerService } from '../../../generated-source/api';
import { Router } from '@angular/router';
import { Observable, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-owner',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.scss'],
})
export class OwnerComponent implements OnInit, OnDestroy {
  @Input() ownerId: string;

  private destroy$ = new Subject<void>();

  owner$: Observable<OwnerDto>;

  constructor(private router: Router, private ownerService: OwnerService) {}

  ngOnInit(): void {
    this.owner$ = this.ownerService.getOwner(this.ownerId);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  delete() {
    if (this.ownerId) {
      this.ownerService
        .deleteOwner(this.ownerId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/owners']);
          },
        });
    }
  }
}
