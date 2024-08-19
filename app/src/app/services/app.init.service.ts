import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { ErrorService } from './error.service';

@Injectable({
  providedIn: 'root',
})
export class AppInitService {
  private authService = inject(AuthService);
  private errorService = inject(ErrorService);

  initApp(): void {
    const token = localStorage.getItem(this.authService.localStorageTokenKey);
    if (token && this.authService.isTokenExpired(token)) {
      this.authService.logout();

      this.errorService.showError(
        'Your session has expired. Please log in again.'
      );
    }
  }
}
