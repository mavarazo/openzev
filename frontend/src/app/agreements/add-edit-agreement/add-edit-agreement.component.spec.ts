import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditAgreementComponent } from './add-edit-agreement.component';

describe('AddEditAgreementComponent', () => {
  let component: AddEditAgreementComponent;
  let fixture: ComponentFixture<AddEditAgreementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddEditAgreementComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditAgreementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
