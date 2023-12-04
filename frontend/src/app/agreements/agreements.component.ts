import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AgreementDto, AgreementService } from '../../generated-source/api';

@Component({
  selector: 'app-agreements',
  templateUrl: './agreements.component.html',
  styleUrls: ['./agreements.component.scss'],
})
export class AgreementsComponent implements OnInit {
  agreements$: Observable<AgreementDto[]>;

  constructor(private agreementService: AgreementService) {}

  ngOnInit(): void {
    this.agreements$ = this.agreementService.getAgreements();
  }
}
