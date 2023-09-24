import { Component, OnInit } from '@angular/core';
import { first, Observable } from 'rxjs';
import { AgreementDto, AgreementService } from '../../../generated-source/api';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-agreement',
  templateUrl: './agreement.component.html',
  styleUrls: ['./agreement.component.scss'],
})
export class AgreementComponent implements OnInit {
  id: string | null;
  agreement$: Observable<AgreementDto>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private agreementService: AgreementService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');

    if (this.id) {
      this.agreement$ = this.agreementService.getAgreement(this.id);
    }
  }

  delete() {
    if (this.id) {
      this.agreementService
        .deleteAgreement(this.id)
        .pipe(first())
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/agreements']);
          },
        });
    }
  }
}
