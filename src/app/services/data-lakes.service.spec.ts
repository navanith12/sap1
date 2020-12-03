import { TestBed } from '@angular/core/testing';

import { DataLakesService } from './data-lakes.service';

describe('DataLakesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DataLakesService = TestBed.get(DataLakesService);
    expect(service).toBeTruthy();
  });
});
