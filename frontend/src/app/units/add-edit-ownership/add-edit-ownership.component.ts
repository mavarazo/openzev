import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import {
  ModifiableOwnershipDto,
  OwnerDto,
  OwnerService,
  OwnershipDto,
  OwnershipService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';

@Component({
  selector: 'app-add-edit-ownership',
  templateUrl: './add-edit-ownership.component.html',
  styleUrls: ['./add-edit-ownership.component.scss'],
})
export class AddEditOwnershipComponent extends AbstractAddEditComponent<
  OwnershipDto,
  ModifiableOwnershipDto
> {
  @Input() ownershipId: string | null;
  @Input() unitId: string;

  get isEdit(): boolean {
    return !!this.ownershipId;
  }

  owners$: Observable<OwnerDto[]>;
  unit$: Observable<UnitDto>;

  constructor(
    private unitService: UnitService,
    private ownerService: OwnerService,
    private ownershipService: OwnershipService,
    private router: Router
  ) {
    super();
  }

  fetchEntity(): Observable<OwnershipDto> {
    return this.ownershipService.getOwnership(this.ownershipId!);
  }

  override ngOnInit() {
    super.ngOnInit();

    this.owners$ = this.ownerService.getOwners();
    if (this.unitId) {
      this.unit$ = this.unitService.getUnit(this.unitId);
    }
  }

  override initForm() {
    return new FormGroup({
      ownerId: new FormControl(null, Validators.required),
      periodFrom: new FormControl(null, Validators.required),
    });
  }

  createEntity(entity: ModifiableOwnershipDto): Observable<any> {
    return this.ownershipService.createOwnership(this.unitId, entity);
  }

  onSuccessfullCreate(result: any): void {
    this.router.navigate(['units', this.unitId]);
  }

  changeEntity(entity: ModifiableOwnershipDto): Observable<any> {
    return this.ownershipService.changeOwnership(this.ownershipId!, entity);
  }

  onSuccessfullChange(result: any): void {
    this.router.navigate(['units', this.unitId]);
  }
}
