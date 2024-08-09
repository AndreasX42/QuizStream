import { Injectable, signal } from '@angular/core';
import { Key } from './key.model';

@Injectable({
  providedIn: 'root',
})
export class KeyService {
  private initKeyList: Key[] = [
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
  ];

  private keys = signal<Key[]>(this.initKeyList);

  getKeys() {
    return this.keys.asReadonly();
  }

  addKey(keyData: { provider: string; key: string }) {
    const newKey: Key = {
      ...keyData,
      id: Math.random().toString(),
    };

    this.keys.update((oldKeys) => [...oldKeys, newKey]);
  }

  deleteKey(keyId: string) {
    this.keys.update((oldKeys) => oldKeys.filter((key) => key.id !== keyId));
  }
}
