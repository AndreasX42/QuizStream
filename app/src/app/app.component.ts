import { Component, inject, input, signal } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { QuizListComponent } from './quiz/quiz-list/quiz-list.component';
import { StartPageComponent } from './start/start.page.component';
import { NewQuizComponent } from './quiz/new-quiz/new-quiz.component';
import { KeyComponent } from './key/key.component';
import { LoginComponent } from './auth/login/login.component';
import { RouterOutlet } from '@angular/router';
import { ErrorModalComponent } from './modal/error-modal/error-modal.component';
import { ErrorService } from './services/error.service';
import { SuccessModalComponent } from './modal/success-modal/success-modal.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HeaderComponent,
    QuizListComponent,
    StartPageComponent,
    NewQuizComponent,
    KeyComponent,
    LoginComponent,
    RouterOutlet,
    ErrorModalComponent,
    SuccessModalComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  app_name = 'QuizStream';

  private errorService = inject(ErrorService);
  error = this.errorService.error;
  success = this.errorService.success;
}
