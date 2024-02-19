import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import {
  RepresentativeDto,
  RepresentativeService,
} from '../../generated-source/api';

@Component({
  selector: 'app-representatives',
  templateUrl: './representatives.component.html',
  styleUrls: ['./representatives.component.scss'],
})
export class RepresentativesComponent {
  representatives$: Observable<RepresentativeDto[]>;

  constructor(private representativeService: RepresentativeService) {}

  ngOnInit(): void {
    this.representatives$ = this.representativeService.getRepresentatives();
  }
}
