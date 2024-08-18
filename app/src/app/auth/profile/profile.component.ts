import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MatButton],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = signal<string | undefined>(undefined);
  email = signal<string | undefined>(undefined);
  userId = signal<number | undefined>(undefined);

  ngOnInit(): void {
    // Fetch user details from the AuthService
    const user = { username: 'admin', email: '123', id: 0 };
    if (user) {
      this.username.set(user.username);
      this.email.set(user.email);
      this.userId.set(user.id);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']); // Redirect to the login page after logout
  }
}
