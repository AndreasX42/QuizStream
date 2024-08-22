import {
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { MatButton, MatButtonModule } from '@angular/material/button';

import { QuizService } from '../../services/quiz.service';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { QuizComponent } from '../quiz/quiz.component';
import { Quiz } from '../../models/quiz.model';
import { AuthService } from '../../services/auth.service';
import { MatOption, MatSelect } from '@angular/material/select';

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
    MatSelect,
    MatOption,
    QuizComponent,
    MatButtonModule,
    CommonModule,
  ],
  templateUrl: './quiz-list.component.html',
  styleUrl: './quiz-list.component.css',
})
export class QuizListComponent {
  private quizService = inject(QuizService);
  private authService = inject(AuthService);
  private destroyRef = inject(DestroyRef);

  user = this.authService.user;

  expandedQuizId = signal('');

  pageSize = signal<string>('3');
  sortBy = signal<string>('dateCreated,desc');
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);

  pagesArray(): number[] {
    return Array.from({ length: this.totalPages() }, (_, index) => index);
  }

  quizzes = signal<Quiz[]>([]);

  constructor() {
    const ref = effect(() => {
      this.loadQuizzes();
    });

    this.destroyRef.onDestroy(() => {
      ref.destroy();
    });
  }

  loadQuizzes(): void {
    const sub = this.quizService
      .getAllQuizzes(
        this.user()!.id,
        this.currentPage(),
        this.pageSize(),
        this.sortBy()
      )
      .subscribe({
        next: (page) => {
          this.totalPages.set(page.totalPages);
          this.quizzes.set(page.content);
        },
        error: (err) => console.error('Error fetching quizzes', err),
      });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  onDeleteQuiz() {
    this.loadQuizzes();
  }

  onSortChange(sortBy: string): void {
    if (!sortBy) {
      return;
    }

    this.sortBy.set(sortBy);
  }

  onPageSizeChange(pageSize: string): void {
    this.pageSize.set(pageSize);
    this.currentPage.set(0);
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  toggleExpansion(quizId: string): void {
    this.expandedQuizId.set(this.expandedQuizId() === quizId ? '' : quizId);
  }

  deleteQuiz(quizId: string) {
    this.quizService.deleteQuiz(quizId);
  }
}
