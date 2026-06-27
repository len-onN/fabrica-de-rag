import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { map, catchError, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.me().pipe(
    map(user => {
      if (user) {
        return true;
      }
      return router.createUrlTree(['/auth/login']);
    }),
    catchError(() => of(router.createUrlTree(['/auth/login'])))
  );
};
