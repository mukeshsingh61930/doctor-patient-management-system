import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService, UserRole } from './auth.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | import('@angular/router').UrlTree => {
  const router = inject(Router);
  const authService = inject(AuthService);

  if (!authService.isLoggedIn()) {
    return router.parseUrl('/login');
  }

  const allowedRoles = route.data?.['roles'] as UserRole[] | undefined;
  if (!allowedRoles?.length || authService.hasAnyRole(allowedRoles)) {
    return true;
  }

  return router.parseUrl('/dashboard');
};
