import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountingsComponent } from './accountings.component';

describe('AccountingsComponent', () => {
  let component: AccountingsComponent;
  let fixture: ComponentFixture<AccountingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccountingsComponent]
    });
    fixture = TestBed.createComponent(AccountingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
