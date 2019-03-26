import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PredictTabComponent } from './predict-tab.component';

describe('PredictTabComponent', () => {
  let component: PredictTabComponent;
  let fixture: ComponentFixture<PredictTabComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PredictTabComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PredictTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
