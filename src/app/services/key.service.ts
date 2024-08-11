import { Injectable, signal } from '@angular/core';
import { Key, KeyProvider } from './../models/key.model';

@Injectable({
  providedIn: 'root',
})
export class KeyService {
  private STORAGE_KEY = 'api-keys';

  private keys = signal([
    {
      id: '1',
      provider: 'OpenAI',
      key: '123123123',
    },
    {
      id: '2',
      provider: 'OpenAI',
      key: '987987987',
    },
  ]);

  constructor() {
    const keys = sessionStorage.getItem(this.STORAGE_KEY);

    if (keys) {
      this.keys.set(JSON.parse(keys));
    }
  }

  getKeys() {
    return this.keys.asReadonly();
  }

  addKey(keyData: { provider: KeyProvider; key: string }) {
    const newKey: Key = {
      ...keyData,
      id:
        this.keys().length > 0
          ? (Math.max(...this.keys().map((key) => +key.id)) + 1).toString()
          : '0',
    };

    this.keys.update((oldKeys) => [...oldKeys, newKey]);
    this.savekeys();
  }

  deleteKey(keyId: string) {
    this.keys.update((oldKeys) => oldKeys.filter((key) => key.id !== keyId));
    this.savekeys();
  }

  private savekeys() {
    sessionStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.keys()));
  }
}
