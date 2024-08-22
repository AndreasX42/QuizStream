import { Component, inject } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { QuizListComponent } from './quiz/quiz-list/quiz-list.component';
import { StartPageComponent } from './start/start.page.component';
import { NewQuizComponent } from './quiz/new-quiz/new-quiz.component';
import { KeyComponent } from './key/key.component';
import { LoginComponent } from './auth/login/login.component';
import { RouterOutlet } from '@angular/router';
import { ErrorModalComponent } from './modal/error-modal/error-modal.component';
import { SuccessModalComponent } from './modal/success-modal/success-modal.component';
import { MessageService } from './services/message.service';
import { WarnModalComponent } from './modal/warn-modal/warn-modal.component';

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
    WarnModalComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  app_name = 'QuizStream';

  private messageService = inject(MessageService);
  error = this.messageService.error;
  success = this.messageService.success;
  warning = this.messageService.warning;
}
