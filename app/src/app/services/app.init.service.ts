import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AppInitService {
  private authService = inject(AuthService);

  initApp(): void {
    const token = localStorage.getItem(this.authService.localStorageTokenKey);
    if (token && this.authService.isTokenExpired(token)) {
      this.authService.logout();
      alert('Your session has expired. Please log in again.');
    }
  }
}
