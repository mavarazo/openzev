import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { PropertyDto, PropertyService } from '../../generated-source/api';

@Component({
  selector: 'app-properties',
  templateUrl: './properties.component.html',
  styleUrls: ['./properties.component.scss'],
})
export class PropertiesComponent implements OnInit {
  properties$: Observable<PropertyDto[]>;

  constructor(private propertyService: PropertyService) {}

  ngOnInit(): void {
    this.properties$ = this.propertyService.getProperties();
  }
}
