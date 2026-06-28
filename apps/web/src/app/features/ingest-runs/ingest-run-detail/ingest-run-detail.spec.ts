import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IngestRunDetail } from './ingest-run-detail';

describe('IngestRunDetail', () => {
  let component: IngestRunDetail;
  let fixture: ComponentFixture<IngestRunDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IngestRunDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(IngestRunDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
