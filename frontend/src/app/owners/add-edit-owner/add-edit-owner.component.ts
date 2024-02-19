import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  ModifiableOwnerDto,
  OwnerDto,
  OwnerService,
} from '../../../generated-source/api';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';

@Component({
  selector: 'app-add-edit-owner',
  templateUrl: './add-edit-owner.component.html',
  styleUrls: ['./add-edit-owner.component.scss'],
})
export class AddEditOwnerComponent extends AbstractAddEditComponent<
  OwnerDto,
  ModifiableOwnerDto
> {
  @Input() ownerId: string | null;

  override get isEdit(): boolean {
    return !!this.ownerId;
  }

  constructor(private router: Router, private ownerService: OwnerService) {
    super();
  }

  fetchEntity(): Observable<OwnerDto> {
    return this.ownerService.getOwner(this.ownerId!);
  }

  override initForm() {
    return new FormGroup({
      firstName: new FormControl('', Validators.required),
      lastName: new FormControl('', Validators.required),
      email: new FormControl('', Validators.email),
      street: new FormControl('', Validators.required),
      houseNr: new FormControl('', Validators.required),
      postalCode: new FormControl('', Validators.required),
      city: new FormControl('', Validators.required),
      phoneNr: new FormControl(''),
      mobileNr: new FormControl(''),
      contractId: new FormControl(''),
    });
  }

  createEntity(entity: ModifiableOwnerDto): Observable<any> {
    return this.ownerService.createOwner(entity);
  }

  onSuccessfullCreate(result: any): void {
    this.router.navigate(['/owners', result]);
  }

  changeEntity(entity: ModifiableOwnerDto): Observable<any> {
    return this.ownerService.changeOwner(this.ownerId!, entity);
  }

  onSuccessfullChange(result: any): void {
    this.router.navigate(['/owners', result]);
  }
}
