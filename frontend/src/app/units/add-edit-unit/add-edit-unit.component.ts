import { Component, Input } from '@angular/core';
import {
  ModifiableUnitDto,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AbstractAddEditComponent } from '../../shared/components/abstract-add-edit.component';

@Component({
  selector: 'app-add-edit-unit',
  templateUrl: './add-edit-unit.component.html',
  styleUrls: ['./add-edit-unit.component.scss'],
})
export class AddEditUnitComponent extends AbstractAddEditComponent<
  UnitDto,
  ModifiableUnitDto
> {
  @Input() unitId: string | null;

  get isEdit(): boolean {
    return !!this.unitId;
  }

  constructor(private router: Router, private unitService: UnitService) {
    super();
  }

  fetchEntity(): Observable<UnitDto> {
    return this.unitService.getUnit(this.unitId!);
  }

  override initForm() {
    return new FormGroup({
      subject: new FormControl(null, Validators.required),
      valueRatio: new FormControl(null, Validators.pattern('^[0-9]*$')),
      mpan: new FormControl(null),
    });
  }

  createEntity(entity: ModifiableUnitDto): Observable<any> {
    return this.unitService.createUnit(entity);
  }

  onSuccessfullCreate(result: any): void {
    this.router.navigate(['/units', result]);
  }

  changeEntity(entity: ModifiableUnitDto): Observable<any> {
    return this.unitService.changeUnit(this.unitId!, entity);
  }

  onSuccessfullChange(result: any): void {
    this.router.navigate(['/invoices', result]);
  }
}
