import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  ModifiableRepresentativeDto,
  RepresentativeDto,
  RepresentativeService,
} from '../../../generated-source/api';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-edit-representative',
  templateUrl: './add-edit-representative.component.html',
  styleUrls: ['./add-edit-representative.component.scss'],
})
export class AddEditRepresentativeComponent extends AbstractAddEditComponent<
  RepresentativeDto,
  ModifiableRepresentativeDto
> {
  @Input() representativeId: string | null;

  override get isEdit(): boolean {
    return !!this.representativeId;
  }

  constructor(
    private representativeService: RepresentativeService,
    private router: Router
  ) {
    super();
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
      active: new FormControl(''),
    });
  }

  override fetchEntity(): Observable<RepresentativeDto> {
    return this.representativeService.getRepresentative(this.representativeId!);
  }

  override createEntity(entity: ModifiableRepresentativeDto): Observable<any> {
    return this.representativeService.createRepresentative(entity);
  }

  override onSuccessfullCreate(result: any) {
    this.router.navigate(['/representatives/', result]);
  }

  override changeEntity(entity: ModifiableRepresentativeDto): Observable<any> {
    return this.representativeService.changeRepresentative(
      this.representativeId!,
      entity
    );
  }

  override onSuccessfullChange(result: any) {
    this.router.navigate(['/representatives/', result]);
  }
}
