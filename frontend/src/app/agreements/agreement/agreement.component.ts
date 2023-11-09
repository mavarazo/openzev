import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject, takeUntil } from 'rxjs';
import { AgreementDto, AgreementService } from '../../../generated-source/api';
import { Router } from '@angular/router';

@Component({
  selector: 'app-agreement',
  templateUrl: './agreement.component.html',
  styleUrls: ['./agreement.component.scss'],
})
export class AgreementComponent implements OnInit, OnDestroy {
  @Input() agreementId: string;

  private destroy$ = new Subject<void>();

  agreement$: Observable<AgreementDto>;

  constructor(
    private router: Router,
    private agreementService: AgreementService
  ) {}

  ngOnInit(): void {
    this.agreement$ = this.agreementService.getAgreement(this.agreementId);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  delete() {
    this.agreementService
      .deleteAgreement(this.agreementId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        error: console.error,
        complete: () => {
          this.router.navigate(['/agreements']);
        },
      });
  }
}
