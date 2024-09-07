import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [MatButtonModule, RouterLink],
  templateUrl: './start.page.component.html',
  styleUrl: './start.page.component.scss',
})
export class StartPageComponent {
  private authService = inject(AuthService);

  isLoggedIn = this.authService.isLoggedIn;
}
