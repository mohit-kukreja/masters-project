import { TestBed } from '@angular/core/testing';

import { BuildTabService } from './build-tab.service';

describe('BuildTabService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BuildTabService = TestBed.get(BuildTabService);
    expect(service).toBeTruthy();
  });
});
