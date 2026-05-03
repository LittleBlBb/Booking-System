import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) return true;

  return auth.getMe().pipe(
    map(me => {
      if (me.companyId) {
        return router.parseUrl('/dashboard');
      } else {
        return router.parseUrl('/company-select');
      }
    }),
    catchError(() => {
      auth.logout();
      return of(true);
    })
  );
};
