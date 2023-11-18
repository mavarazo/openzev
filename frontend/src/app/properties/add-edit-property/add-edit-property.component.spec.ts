import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditPropertyComponent } from './add-edit-property.component';

describe('AddEditPropertyComponent', () => {
  let component: AddEditPropertyComponent;
  let fixture: ComponentFixture<AddEditPropertyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddEditPropertyComponent]
    });
    fixture = TestBed.createComponent(AddEditPropertyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
