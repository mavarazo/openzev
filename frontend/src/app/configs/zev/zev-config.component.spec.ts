import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZevConfigComponent } from './zev-config.component';

describe('ZevComponent', () => {
  let component: ZevConfigComponent;
  let fixture: ComponentFixture<ZevConfigComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ZevConfigComponent],
    });
    fixture = TestBed.createComponent(ZevConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
