import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditRepresentativeComponent } from './add-edit-representative.component';

describe('AddEditRepresentativeComponent', () => {
  let component: AddEditRepresentativeComponent;
  let fixture: ComponentFixture<AddEditRepresentativeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddEditRepresentativeComponent]
    });
    fixture = TestBed.createComponent(AddEditRepresentativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
