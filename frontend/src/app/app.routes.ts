import { Routes } from '@angular/router';
import { guestGuard } from './core/guards/guest.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'auth', pathMatch: 'full' },

  {
    path: 'auth',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./pages/auth/auth').then(m => m.AuthComponent)
  },

  {
    path: 'company-select',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/company-select/company-select').then(m => m.CompanySelectComponent)
  },

  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/dashboard/dashboard').then(m => m.DashboardComponent)
  },

  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/profile/profile').then(m => m.ProfileComponent)
  },

  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/admin/admin').then(m => m.AdminComponent)
  },

  { path: '**', redirectTo: 'auth' }
];
