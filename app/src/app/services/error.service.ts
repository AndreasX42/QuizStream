import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ErrorService {
  private _error = signal('');
  private _success = signal('');

  error = this._error.asReadonly();
  success = this._success.asReadonly();

  showError(message: string) {
    this._error.set(message);
  }

  clearError() {
    this._error.set('');
  }

  showSuccess(message: string) {
    this._success.set(message);
  }

  clearSuccess() {
    this._success.set('');
  }
}
