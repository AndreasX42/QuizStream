import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { MatButton } from '@angular/material/button';
import { interval } from 'rxjs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MatButton, MatSlideToggleModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private themeService = inject(ThemeService);
  selectedDarkMode = this.themeService.isDarkMode;

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  sessionTimeLeft = signal<string | undefined>(undefined);
  user = this.authService.user;

  ngOnInit(): void {
    this.initializeSessionTimer();
  }

  logout(): void {
    this.authService.logout();
  }

  initializeSessionTimer(): void {
    const token = this.authService.getJwtToken();
    if (token) {
      const decodedToken = this.authService.decodeToken();
      const expiryTime = decodedToken.exp * 1000;

      interval(1000).subscribe(() => {
        const currentTime = Date.now();
        const timeLeft = expiryTime - currentTime;

        if (timeLeft <= 0) {
          this.sessionTimeLeft.set('Session expired');
          this.authService.logout();
        } else {
          this.sessionTimeLeft.set(this.formatTimeLeft(timeLeft));
        }
      });
    }
  }

  formatTimeLeft(timeLeft: number): string {
    const minutes = Math.floor((timeLeft / 1000 / 60) % 60);
    const seconds = Math.floor((timeLeft / 1000) % 60);
    return `${minutes}m ${seconds}s`;
  }
}
