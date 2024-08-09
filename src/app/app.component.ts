import { Component, input, signal } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { QuizListComponent } from './quiz/quiz-list/quiz-list.component';
import { MainComponent } from './main/main.component';
import { QuizFormComponent } from './quiz/new-quiz/new-quiz.component';
import { KeyComponent } from "./key/key.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HeaderComponent,
    QuizListComponent,
    MainComponent,
    QuizFormComponent,
    KeyComponent
],
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
