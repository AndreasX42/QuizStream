import { inject, Injectable, signal } from '@angular/core';
import { Key, KeyProvider } from './../models/key.model';
import { Util } from '../shared/util';
import { MessageService } from './message.service';

@Injectable({
  providedIn: 'root',
})
export class KeyService {
  private messageService = inject(MessageService);
  private localStorageApiKeys = 'api-keys';

  private _keys = signal<Key[]>([]);
  keys = this._keys.asReadonly();

  constructor() {
    const keysString = localStorage.getItem(this.localStorageApiKeys);

    if (keysString) {
      this._keys.set(JSON.parse(keysString));
    }
  }

  getKeys() {
    return this.keys;
  }

  addKey(keyData: { provider: KeyProvider; key: string }) {
    if (this.keys().some((k) => k.provider === keyData.provider)) {
      this.messageService.showError(
        'An API key of this provider already exists.'
      );
      return;
    }

    const newKey: Key = {
      ...keyData,
      id: Util.getNextIncrement(this.keys()),
    };

    this._keys.update((oldKeys) => [...oldKeys, newKey]);
    this.savekeys();

    this.messageService.showSuccess('API key was added successfully!');
  }

  deleteKey(keyId: string) {
    this._keys.update((oldKeys) => oldKeys.filter((key) => key.id !== keyId));
    this.savekeys();
  }

  private savekeys() {
    localStorage.setItem(this.localStorageApiKeys, JSON.stringify(this.keys()));
  }
}
