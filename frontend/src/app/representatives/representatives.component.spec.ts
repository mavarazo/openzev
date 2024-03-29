import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepresentativesComponent } from './representatives.component';

describe('RepresentativesComponent', () => {
  let component: RepresentativesComponent;
  let fixture: ComponentFixture<RepresentativesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RepresentativesComponent]
    });
    fixture = TestBed.createComponent(RepresentativesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
