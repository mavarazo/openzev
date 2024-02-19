import { Component, Input } from '@angular/core';
import { AbstractDetailComponent } from '../../shared/components/abstract-detail.component';
import {
  RepresentativeDto,
  RepresentativeService,
} from '../../../generated-source/api';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-representativ',
  templateUrl: './representative.component.html',
  styleUrls: ['./representative.component.scss'],
})
export class RepresentativeComponent extends AbstractDetailComponent<RepresentativeDto> {
  @Input() representativeId: string;

  constructor(
    private router: Router,
    private representativeService: RepresentativeService
  ) {
    super();
  }

  fetchEntity(): Observable<RepresentativeDto> {
    return this.representativeService.getRepresentative(this.representativeId);
  }

  deleteEntity(): Observable<any> {
    return this.representativeService.deleteRepresentative(
      this.representativeId
    );
  }

  onSuccessfullDelete(): void {
    this.router.navigate(['/representatives']);
  }
}
