import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { UnitDto, UnitService } from '../../generated-source/api';

@Component({
  selector: 'app-units',
  templateUrl: './units.component.html',
  styleUrls: ['./units.component.scss'],
})
export class UnitsComponent implements OnInit {
  units$: Observable<UnitDto[]>;

  constructor(private unitService: UnitService) {}

  ngOnInit(): void {
    this.units$ = this.unitService.getUnits();
  }
}
