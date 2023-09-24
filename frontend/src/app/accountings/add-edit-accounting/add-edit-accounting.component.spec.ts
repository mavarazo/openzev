import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditAccountingComponent } from './add-edit-accounting.component';

describe('AddEditAccountingComponent', () => {
  let component: AddEditAccountingComponent;
  let fixture: ComponentFixture<AddEditAccountingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddEditAccountingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditAccountingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
