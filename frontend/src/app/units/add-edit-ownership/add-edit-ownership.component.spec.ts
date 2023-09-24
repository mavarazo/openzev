import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditOwnershipComponent } from './add-edit-ownership.component';

describe('AddEditOwnershipComponent', () => {
  let component: AddEditOwnershipComponent;
  let fixture: ComponentFixture<AddEditOwnershipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddEditOwnershipComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditOwnershipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
