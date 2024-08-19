import { Component, inject, signal } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';

import { QuizService } from '../../services/quiz.service';
import { DatePipe, TitleCasePipe } from '@angular/common';
import { QuizComponent } from '../quiz/quiz.component';
import { Quiz } from '../../models/quiz.model';
import { ResolveFn } from '@angular/router';

@Component({
  selector: 'app-quiz-list',
  standalone: true,
  imports: [
    MatCardModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    DatePipe,
    TitleCasePipe,
    MatButtonModule,
    QuizComponent,
  ],
  templateUrl: './quiz-list.component.html',
  styleUrl: './quiz-list.component.css',
})
export class QuizListComponent {
  private quizService = inject(QuizService);
  quizzes = this.quizService.getQuizzes();

  expandedQuizId = signal('');

  toggleExpansion(quizId: string): void {
    this.expandedQuizId.set(this.expandedQuizId() === quizId ? '' : quizId);
  }

  deleteQuiz(quizId: string) {
    this.quizService.deleteQuiz(quizId);
  }
}

export const resolveQuizzes: ResolveFn<Quiz[]> = (
  activatedRouteSnapshot,
  routerState
) => {
  const order = activatedRouteSnapshot.queryParams['order'];
  const quizService = inject(QuizService);
  const quizzes = quizService.getQuizzes()();

  if (order && order === 'asc') {
    quizzes.sort((a, b) => (a.id > b.id ? 1 : -1));
  } else {
    quizzes.sort((a, b) => (a.id > b.id ? -1 : 1));
  }

  return quizzes.length ? quizzes : [];
};
