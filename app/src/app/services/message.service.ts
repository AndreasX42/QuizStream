import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  static MSG_UNKNOWN_ERROR = 'Something went wrong, please try again later.';
  static MSG_REGISTER_SUCCESS = 'Registered successfully, you can log in now.';
  static MSG_LOGIN_ERROR_USERNAME_OR_PASSWORD =
    'Username or password incorrect.';
  static MSG_LOAD_PROFILE_DATA_ERROR =
    'Something went wrong loading profile data, please try again later.';
  static MSG_SESSION_EXPIRED_ERROR =
    'Your session has expired. Please log in again.';

  static MSG_LOGIN_FIST_WARNING =
    'You have to log in, in order to use this service.';

  private _error = signal<string | undefined>(undefined);
  private _success = signal<string | undefined>(undefined);
  private _warning = signal<string | undefined>(undefined);

  error = this._error.asReadonly();
  success = this._success.asReadonly();
  warning = this._warning.asReadonly();

  showError(message: string) {
    this._error.set(message);
  }

  clearError() {
    this._error.set(undefined);
  }

  showSuccess(message: string) {
    this._success.set(message);
  }

  clearSuccess() {
    this._success.set(undefined);
  }

  showWarning(message: string) {
    this._warning.set(message);
  }

  clearWarning() {
    this._warning.set(undefined);
  }
}
