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

  private _keys = signal<Key[]>([
    {
      id: '1',
      provider: KeyProvider.OpenAI,
      key: '123123123',
    },
    {
      id: '2',
      provider: KeyProvider.OpenAI,
      key: '987987987',
    },
  ]);

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
        'There is already a key for this provider in the list.'
      );
      return;
    }

    const newKey: Key = {
      ...keyData,
      id: Util.getNextIncrement(this.keys()),
    };

    this._keys.update((oldKeys) => [...oldKeys, newKey]);
    this.savekeys();
  }

  deleteKey(keyId: string) {
    this._keys.update((oldKeys) => oldKeys.filter((key) => key.id !== keyId));
    this.savekeys();
  }

  private savekeys() {
    localStorage.setItem(this.localStorageApiKeys, JSON.stringify(this.keys()));
  }
}
