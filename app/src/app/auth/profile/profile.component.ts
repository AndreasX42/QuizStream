import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { MatButton } from '@angular/material/button';
import { interval } from 'rxjs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { ThemeService } from '../../services/theme.service';
import { MessageService } from '../../services/message.service';
import { TitleCasePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MatButton, MatSlideToggleModule, TitleCasePipe, RouterLink],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private themeService = inject(ThemeService);
  private messageService = inject(MessageService);
  private destroyRef = inject(DestroyRef);
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

  deleteAccount(): void {
    const dialogRef = this.messageService.showConfirmModal(
      MessageService.MSG_WARNING_DELETE_USER_ACCOUNT
    );

    dialogRef.afterClosed().subscribe((confirm) => {
      if (!confirm) {
        return;
      }

      const sub = this.authService.deleteUserAccount().subscribe({
        complete: () => {
          this.authService.logout();
          this.messageService.showSuccessModal(
            MessageService.MSG_SUCCESS_DELETE_USER_ACCOUNT
          );
        },
      });

      this.destroyRef.onDestroy(() => {
        sub.unsubscribe();
      });
    });
  }

  initializeSessionTimer(): void {
    const token = this.authService.getJwtToken();
    if (token) {
      const decodedToken = this.authService.decodeToken();

      const expiryTime = decodedToken.exp * 1000;

      const sub = interval(1000).subscribe(() => {
        const currentTime = Date.now();
        const timeLeft = expiryTime - currentTime;

        if (timeLeft > 0) {
          this.sessionTimeLeft.set(this.formatTimeLeft(timeLeft));
        } else {
          this.sessionTimeLeft.set('Session expired');
          this.authService.logout();
        }
      });

      this.destroyRef.onDestroy(() => {
        sub.unsubscribe();
      });
    }
  }

  formatTimeLeft(timeLeft: number): string {
    const hours = Math.floor((timeLeft / 1000 / 60 / 60) % 24);
    const minutes = Math.floor((timeLeft / 1000 / 60) % 60);
    const seconds = Math.floor((timeLeft / 1000) % 60);

    return `${hours}h ${minutes}m ${seconds}s`;
  }
}
