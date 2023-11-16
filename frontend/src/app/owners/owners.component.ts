import { Component, OnInit } from '@angular/core';
import { OwnerDto, OwnerService } from '../../generated-source/api';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-owners',
  templateUrl: './owners.component.html',
  styleUrls: ['./owners.component.scss'],
})
export class OwnersComponent implements OnInit {
  owners$: Observable<OwnerDto[]>;

  constructor(private ownerService: OwnerService) {}

  ngOnInit(): void {
    this.owners$ = this.ownerService.getOwners();
  }
}
