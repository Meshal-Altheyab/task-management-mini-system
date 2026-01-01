import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // إذا مسجل دخول -> كمّل
  if (auth.loggedIn()) {
    return true;
  }

  // غير كذا -> وده صفحة login
  return router.createUrlTree(['/login']);
};
