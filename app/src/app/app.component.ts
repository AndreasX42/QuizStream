import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { RouterOutlet } from '@angular/router';
import { ErrorModalComponent } from './modal/error-modal/error-modal.component';
import { SuccessModalComponent } from './modal/success-modal/success-modal.component';
import { WarnModalComponent } from './modal/warn-modal/warn-modal.component';
import { ThemeService } from './services/theme.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HeaderComponent,
    RouterOutlet,
    ErrorModalComponent,
    SuccessModalComponent,
    WarnModalComponent,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  app_name = 'QuizStream';

  private themeService = inject(ThemeService);

  // ngOnInit(): void {
  //   this.themeService.toggleThemeByName('system-default');
  // }
}
