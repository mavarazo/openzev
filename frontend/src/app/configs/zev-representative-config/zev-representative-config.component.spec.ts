import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZevRepresentativeConfigComponent } from './zev-representative-config.component';

describe('ZevRepresentativeConfigComponent', () => {
  let component: ZevRepresentativeConfigComponent;
  let fixture: ComponentFixture<ZevRepresentativeConfigComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ZevRepresentativeConfigComponent]
    });
    fixture = TestBed.createComponent(ZevRepresentativeConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
