import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { map, catchError, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export const bootstrapGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const http = inject(HttpClient);
  
  // Ideally, there should be an endpoint to check if bootstrap is needed
  // For now, if /api/v1/me fails with 401, we try to see if it's because the DB is empty
  // Actually, we could check if bootstrap is already complete or not.
  // We'll just allow passing to login by default if it's the root path.
  // This guard can be enhanced to query /api/v1/system/status.
  return true;
};
