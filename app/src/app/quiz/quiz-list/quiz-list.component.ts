import {
  Component,
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
import { MatButtonModule } from '@angular/material/button';
import { QuizService } from '../../services/quiz.service';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { QuizComponent } from '../quiz/quiz.component';
import { Quiz } from '../../models/quiz.model';
import { AuthService } from '../../services/auth.service';
import { MatOption, MatSelect } from '@angular/material/select';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MessageService } from '../../services/message.service';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { QuizRequestComponent } from '../quiz-request/quiz-request.component';
import { QuizRequestService } from '../../services/quiz.requests.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

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
    MatProgressSpinner,
    MatTabsModule,
    MatTableModule,
    RouterLink,
    MatChipsModule,
    MatIconModule,
    QuizRequestComponent,
    MatPaginatorModule,
  ],
  templateUrl: './quiz-list.component.html',
  styleUrl: './quiz-list.component.scss',
})
export class QuizListComponent implements OnInit {
  private quizService = inject(QuizService);
  private authService = inject(AuthService);
  private destroyRef = inject(DestroyRef);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private messageService = inject(MessageService);
  private quizRequestService = inject(QuizRequestService);

  user = this.authService.user;

  isLoadingQuizzes = signal(false);
  expandedQuizId = signal('');
  selectedTabIndex = signal(0);

  sortBy = signal<string>('dateCreated,desc');
  pageSize = signal<number>(3);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  totalItems = signal<number>(0);

  quizzes = signal<Quiz[]>([]);

  constructor() {
    const loadQuizzesEffect = effect(
      () => {
        this.loadQuizzes();
      },
      {
        allowSignalWrites: true,
      }
    );

    this.destroyRef.onDestroy(() => {
      loadQuizzesEffect.destroy();
    });
  }

  ngOnInit(): void {
    const ref = this.route.fragment.subscribe((fragment) => {
      if (fragment === 'requests') {
        this.selectedTabIndex.set(1);
      } else {
        this.selectedTabIndex.set(0);
      }
    });

    this.destroyRef.onDestroy(() => {
      ref.unsubscribe();
    });
  }

  onTabChange(event: MatTabChangeEvent): void {
    let fragment = '';
    if (event.index === 0) {
      fragment = 'list';
      this.loadQuizzes();
    } else {
      fragment = 'requests';
      this.quizRequestService.loadRequests();
    }

    this.router.navigate([], {
      fragment: fragment,
      replaceUrl: true,
    });
  }

  loadQuizzes(): void {
    this.isLoadingQuizzes.set(true);
    const sub = this.quizService
      .getAllQuizzes(
        this.user()!.id,
        this.currentPage(),
        this.pageSize(),
        this.sortBy()
      )
      .subscribe({
        next: (page) => {
          this.totalItems.set(page.page.totalElements);
          this.totalPages.set(page.page.totalPages);
          this.isLoadingQuizzes.set(false);

          this.quizzes.set(page.content);
        },
        error: () => {
          this.isLoadingQuizzes.set(false);
          this.messageService.showErrorModal(
            MessageService.MSG_ERROR_LOADING_QUIZ_LIST
          );
        },
      });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  onDeleteQuiz() {
    this.loadQuizzes();
  }

  onPaginatorChange(event: PageEvent) {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  toggleExpansion(quizId: string): void {
    this.expandedQuizId.set(this.expandedQuizId() === quizId ? '' : quizId);
  }

  deleteQuiz(quizId: string) {
    this.quizService.deleteQuiz(quizId);
  }
}
