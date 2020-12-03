import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JobsChainComponent } from './jobs-chain.component';

describe('JobsChainComponent', () => {
  let component: JobsChainComponent;
  let fixture: ComponentFixture<JobsChainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JobsChainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JobsChainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
