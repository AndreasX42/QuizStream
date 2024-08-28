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
