import { TestBed } from '@angular/core/testing';

import { IngestRun } from './ingest-run';

describe('IngestRun', () => {
  let service: IngestRun;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IngestRun);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
