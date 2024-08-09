import { Component, input } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { QuizFormComponent } from './quiz/new-quiz/new-quiz.component';
import { QuizzesComponent } from './quiz/quizzes/quizzes.component';
import { MainComponent } from './main/main.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HeaderComponent,
    QuizFormComponent,
    QuizzesComponent,
    MainComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  app_name = 'QuizStream!?';

  loggedIn = input(false);
  gettingStarted = input(false);
}
