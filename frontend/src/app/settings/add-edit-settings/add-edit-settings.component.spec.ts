import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditSettingsComponent } from './add-edit-settings.component';

describe('ZevComponent', () => {
  let component: AddEditSettingsComponent;
  let fixture: ComponentFixture<AddEditSettingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddEditSettingsComponent],
    });
    fixture = TestBed.createComponent(AddEditSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
