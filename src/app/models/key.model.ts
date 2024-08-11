export interface Key {
  id: string;
  provider: KeyProvider;
  key: string;
}

export enum KeyProvider {
  OpenAI = 'OpenAI',
}
