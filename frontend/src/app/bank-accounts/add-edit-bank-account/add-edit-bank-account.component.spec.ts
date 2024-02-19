import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditBankAccountComponent } from './add-edit-bank-account.component';

describe('AddEditBankAccountComponent', () => {
  let component: AddEditBankAccountComponent;
  let fixture: ComponentFixture<AddEditBankAccountComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddEditBankAccountComponent]
    });
    fixture = TestBed.createComponent(AddEditBankAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
