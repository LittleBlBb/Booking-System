import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) return router.parseUrl('/auth');

  return auth.getMe().pipe(
    map(me => {
      const target = route.routeConfig?.path;

      if (!me.company && target !== 'company-select') {
        return router.parseUrl('/company-select');
      }

      if (me.company && target === 'company-select') {
        return router.parseUrl('/dashboard');
      }

      return true;
    }),
    catchError(() => {
      auth.logout();
      return of(router.parseUrl('/auth'));
    })
  );
};
