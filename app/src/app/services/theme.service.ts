import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private localStorageThemeKey = 'theme';
  isDarkMode = signal(false);

  constructor() {
    const savedTheme = localStorage.getItem(this.localStorageThemeKey);
    if (savedTheme === 'dark') {
      this.enableDarkMode();
    } else {
      this.disableDarkMode();
    }
  }

  enableDarkMode() {
    document.documentElement.className = 'darkMode';
    localStorage.setItem(this.localStorageThemeKey, 'dark');
    this.isDarkMode.set(true);
  }

  disableDarkMode() {
    document.documentElement.className = 'lightMode';
    localStorage.setItem(this.localStorageThemeKey, 'light');
    this.isDarkMode.set(false);
  }

  private applySystemDefaultTheme(): void {
    const prefersDarkScheme = window.matchMedia(
      '(prefers-color-scheme: dark)'
    ).matches;

    if (prefersDarkScheme) {
      this.enableDarkMode();
    } else {
      this.disableDarkMode();
    }
  }

  toggleThemeByName(theme: string) {
    if (theme === 'system-default') {
      this.applySystemDefaultTheme();
    } else if (theme === 'light') {
      this.disableDarkMode();
    } else {
      this.enableDarkMode();
    }
  }

  toggleTheme() {
    if (this.isDarkMode()) {
      this.disableDarkMode();
    } else {
      this.enableDarkMode();
    }
  }

  isDarkModeEnabled() {
    return this.isDarkMode();
  }
}
