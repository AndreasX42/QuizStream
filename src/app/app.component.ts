import { Component, input, signal } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { QuizzesComponent } from './quiz/quizzes/quizzes.component';
import { MainComponent } from './main/main.component';
import { QuizFormComponent } from "./quiz/new-quiz/new-quiz.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, QuizzesComponent, MainComponent, QuizFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  app_name = 'QuizStream!?';

  selectedButton = signal<string>('');

  onSelectButton(btnName: string) {
    console.log('selected button ' + btnName);
    this.selectedButton.set(btnName);
  }

  loggedIn = input(false);
  gettingStarted = input(false);
}
