import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepresentativeComponent } from './representative.component';

describe('RepresentativComponent', () => {
  let component: RepresentativeComponent;
  let fixture: ComponentFixture<RepresentativeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RepresentativeComponent],
    });
    fixture = TestBed.createComponent(RepresentativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
