import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AppInitService {
  private authService = inject(AuthService);

  initApp(): void {
    const token: string | null = localStorage.getItem(
      this.authService.localStorageTokenKey
    );
    const userString: string | null = localStorage.getItem(
      this.authService.localStorageUserKey
    );

    if (!token || !userString || this.authService.isTokenExpired()) {
      this.authService.logout();
    } else {
      this.authService._isLoggedIn.set(true);
      this.authService._userToken.set(token);
      this.authService._user.set(JSON.parse(userString));
    }
  }
}
