import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Configs } from '../shared/api.configs';
import { User } from '../models/user.model';
import { MessageService } from './message.service';
import { LoginResponse } from '../models/login.response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  _isLoggedIn = signal(false);
  _user = signal<User | undefined>(undefined);
  _userToken = signal<string | undefined>(undefined);

  private httpClient = inject(HttpClient);
  private router = inject(Router);
  private messageService = inject(MessageService);

  localStorageTokenKey = 'jwt-token';
  localStorageUserKey = 'qs-user';

  isLoggedIn = this._isLoggedIn.asReadonly();
  userToken = this._userToken.asReadonly();
  user = this._user.asReadonly();

  getJwtToken() {
    return localStorage.getItem(this.localStorageTokenKey);
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
          observe: 'body',
        }
      )
      .pipe(
        tap((body: LoginResponse) => {
          if (body && body.jwtToken) {
            const token: string = body.jwtToken;
            const userId: number = body.userId;
            const userName: string = body.userName;
            const email: string = body.email;

            // Set authentication state
            this._isLoggedIn.set(true);
            this._userToken.set(token);
            this._user.set({ id: userId, username: username, email: email });

            // Store the token and user data in localStorage
            localStorage.setItem(this.localStorageTokenKey, token);
            localStorage.setItem(
              this.localStorageUserKey,
              JSON.stringify(this.user())
            );
          } else {
            throw new Error(
              'Login failed: JWT token not found in the response.'
            );
          }
        }),
        catchError((error) => {
          let errorMessage = MessageService.MSG_UNKNOWN_ERROR;
          if (
            typeof error.error === 'string' &&
            error.error.includes('Incorrect')
          ) {
            errorMessage = MessageService.MSG_LOGIN_ERROR_USERNAME_OR_PASSWORD;
          }

          this.messageService.showError(errorMessage);
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.httpClient
      .post<any>(
        `${Configs.BASE_URL}${Configs.REGISTER_URL}`,
        {
          username: username,
          email: email,
          password: password,
        },
        {
          observe: 'response',
        }
      )
      .pipe(
        tap({
          next: (response) => {
            if (response.status !== 201) {
              throw new Error('Error in registration');
            }
          },
          complete: () => {
            this.router.navigate(['/login'], {
              replaceUrl: true,
            });

            this.messageService.showSuccess(
              MessageService.MSG_REGISTER_SUCCESS
            );
          },
        }),
        catchError((error) => {
          let errorMessage = MessageService.MSG_UNKNOWN_ERROR;
          if (
            error.error &&
            error.error.messages &&
            Array.isArray(error.error.messages)
          ) {
            errorMessage = error.error.messages.join('\n');
          }

          this.messageService.showError(errorMessage);
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getUserByUserName(username: string): Observable<any> {
    return this.httpClient
      .get<any>(`${Configs.BASE_URL}${Configs.GET_BY_USERNAME}/${username}`, {
        observe: 'response',
      })
      .pipe(
        tap({
          next: (response) => {
            if (response.status !== 200) {
              throw new Error('Error getting user data');
            }
            const user = response.body;
            this._user.set({
              id: user.id,
              username: user.username,
              email: user.email,
            });
          },
        }),
        catchError((error) => {
          this.messageService.showError(
            MessageService.MSG_LOAD_PROFILE_DATA_ERROR
          );
          this.router.navigate(['/']);
          return throwError(() => new Error(error.message));
        })
      );
  }

  deleteAuthDetails() {
    this._isLoggedIn.set(false);
    this._userToken.set(undefined);
    this._user.set(undefined);
    localStorage.removeItem(this.localStorageTokenKey);
    localStorage.removeItem(this.localStorageUserKey);
  }

  logout(): void {
    if (this.isTokenExpired()) {
      this.messageService.showError(MessageService.MSG_SESSION_EXPIRED_ERROR);
    }

    this.deleteAuthDetails();
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  // Helper method to decode the JWT
  decodeToken() {
    const token = localStorage.getItem(this.localStorageTokenKey);

    if (!token) {
      return null;
    }

    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (error) {
      return null;
    }
  }

  // Helper method to check if the token is expired
  isTokenExpired(): boolean {
    const token = localStorage.getItem(this.localStorageTokenKey);

    if (!token) {
      return false;
    }

    const decodedToken = this.decodeToken();
    if (!decodedToken) {
      return true;
    }

    const expiryTime = decodedToken.exp * 1000;
    return expiryTime < Date.now();
  }
}
