import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Configs } from '../shared/configs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _isLoggedIn = signal(false);
  private _userName = signal<string | undefined>(undefined);
  private _userToken = signal<string | undefined>(undefined);
  private httpClient = inject(HttpClient);
  private router = inject(Router);

  localStorageTokenKey = 'jwt-token';
  localStorageUsernameKey = 'qs-username';

  isLoggedIn = this._isLoggedIn.asReadonly();
  userToken = this._userToken.asReadonly();
  userName = this._userName.asReadonly();

  constructor() {
    const token = localStorage.getItem(this.localStorageTokenKey);

    if (token) {
      this._isLoggedIn.set(true);
      this._userToken.set(token);
    }
  }

  login(username: string, password: string): Observable<any> {
    return this.httpClient
      .post<any>(
        `${Configs.BASE_URL}${Configs.LOGIN_URL}`,
        {
          username: username,
          password: password,
        },
        {
          observe: 'response',
        }
      )
      .pipe(
        tap((response: HttpResponse<any>) => {
          const authHeader = response.headers.get('Authorization');
          if (authHeader && authHeader.startsWith('Bearer ')) {
            const token = authHeader.split(' ')[1];
            this._isLoggedIn.set(true);
            this._userToken.set(token);
            localStorage.setItem(this.localStorageTokenKey, token);
            localStorage.setItem(this.localStorageUsernameKey, username);
          } else {
            console.error('JWT token not found or in the wrong format');
            throw new Error('Login failed: JWT token not found.');
          }
        }),
        catchError((error) => {
          console.error('Login error:', error);
          return throwError(() => new Error('Login failed: ' + error.message));
        })
      );
  }

  logout(): void {
    this._isLoggedIn.set(false);
    this._userToken.set(undefined);
    this._userName.set(undefined);
    localStorage.removeItem(this.localStorageTokenKey);
    this.router.navigate(['/login']);
  }

  // Helper method to decode the JWT
  private decodeToken(token: string) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (error) {
      return null;
    }
  }

  // Helper method to check if the token is expired
  isTokenExpired(token: string): boolean {
    const decodedToken = this.decodeToken(token);
    if (!decodedToken) {
      return true;
    }

    const expiryTime = decodedToken.exp * 1000;
    return expiryTime < Date.now();
  }
}
