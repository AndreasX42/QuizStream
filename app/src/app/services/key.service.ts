import { DestroyRef, inject, Injectable, signal } from '@angular/core';
import { Key, KeyProvider } from './../models/key.model';
import { Util } from '../shared/util';
import { MessageService } from './message.service';
import { Configs } from '../shared/api.configs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class KeyService {
  private messageService = inject(MessageService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private localStorageApiKeys = 'api-keys';

  private _keys = signal<Key[]>([]);
  keys = this._keys.asReadonly();

  constructor() {
    const keyDftName = 'sk-...' + Configs.API_KEY.slice(125);
    const keysString = localStorage.getItem(this.localStorageApiKeys);

    if (keysString) {
      this._keys.set(JSON.parse(keysString));
    }

    if (
      !keysString ||
      (Configs.API_KEY !== '' &&
        this._keys().length === 1 &&
        this._keys()[0].key === keyDftName) ||
      this._keys().length === 0
    ) {
      this._keys.set([
        { id: '0', provider: KeyProvider.OpenAI, key: Configs.API_KEY },
      ]);

      localStorage.setItem(
        this.localStorageApiKeys,
        JSON.stringify([
          {
            id: 0,
            provider: KeyProvider.OpenAI,
            key: keyDftName,
          },
        ])
      );
    }
  }

  getKeys() {
    return this.keys;
  }

  addKey(keyData: { provider: KeyProvider; key: string }) {
    if (this.keys().some((k) => k.provider === keyData.provider)) {
      this.messageService.showErrorModal(
        MessageService.MSG_ERROR_API_KEY_PROVIDER_ALREADY_EXISTS
      );

      return;
    }

    const newKey: Key = {
      ...keyData,
      id: Util.getNextIncrement(this.keys()),
    };

    this._keys.update((oldKeys) => [...oldKeys, newKey]);
    this.savekeys();

    const dialogRef = this.messageService.showSuccessModal(
      MessageService.MSG_SUCCESS_ADDED_API_KEY
    );

    const sub = dialogRef.afterClosed().subscribe(() => {
      this.router
        .navigate(['/keys'], {
          replaceUrl: true,
        })
        .then(() => {
          window.location.reload();
        });
    });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  deleteKey(keyId: string) {
    this._keys.update((oldKeys) => oldKeys.filter((key) => key.id !== keyId));
    this.savekeys();
  }

  private savekeys() {
    localStorage.setItem(this.localStorageApiKeys, JSON.stringify(this.keys()));
  }
}
