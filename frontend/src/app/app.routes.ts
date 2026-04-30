import { Routes } from '@angular/router';
import {guestGuard} from './core/guards/guest.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'auth', pathMatch: 'full' },

  {
    path: 'auth',
    loadComponent: () =>
      import('./pages/auth/auth').then(m => m.AuthComponent)
  },

  {
    path: 'company-select',
    loadComponent: () =>
      import('./pages/company-select/company-select').then(m => m.CompanySelectComponent)
  },

  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard').then(m => m.DashboardComponent)
  },

  {
    path: 'profile',
    loadComponent: () =>
      import('./pages/profile/profile').then(m => m.ProfileComponent)
  },

  {
    path: 'admin',
    loadComponent: () =>
      import('./pages/admin/admin').then(m => m.AdminComponent)
  },

  {
    path: 'auth',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./pages/auth/auth').then(m => m.AuthComponent)
  },

  { path: '**', redirectTo: 'auth' }
];
