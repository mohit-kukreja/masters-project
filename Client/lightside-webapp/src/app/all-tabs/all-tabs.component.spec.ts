import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllTabsComponent } from './all-tabs.component';

describe('AllTabsComponent', () => {
  let component: AllTabsComponent;
  let fixture: ComponentFixture<AllTabsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AllTabsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllTabsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
