import { Component, Input } from '@angular/core';
import { OwnerDto, OwnerService } from '../../../generated-source/api';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AbstractDetailComponent } from '../../shared/components/abstract-detail.component';

@Component({
  selector: 'app-owner',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.scss'],
})
export class OwnerComponent extends AbstractDetailComponent<OwnerDto> {
  @Input() ownerId: string;

  constructor(private router: Router, private ownerService: OwnerService) {
    super();
  }

  fetchEntity(): Observable<OwnerDto> {
    return this.ownerService.getOwner(this.ownerId);
  }

  deleteEntity(): Observable<any> {
    return this.ownerService.deleteOwner(this.ownerId);
  }

  onSuccessfullDelete(): void {
    this.router.navigate(['/owners']);
  }
}
