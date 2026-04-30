import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Если пользователь уже залогинен, перенаправляем на /dashboard
  return auth.isLoggedIn() ? router.parseUrl('/dashboard') : true;
};
