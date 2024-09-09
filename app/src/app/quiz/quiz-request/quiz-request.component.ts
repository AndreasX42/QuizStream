import { Component, DestroyRef, effect, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { QuizRequestService } from '../../services/quiz.requests.service';
import { AuthService } from '../../services/auth.service';
import { MessageService } from '../../services/message.service';
import { ActivatedRoute, Router } from '@angular/router';
import { getEnumDisplayName, RequestStatus } from '../../models/quiz.model';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'app-quiz-requests',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatProgressSpinner,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    DatePipe,
    TitleCasePipe,
    MatProgressBarModule,
  ],
  templateUrl: './quiz-request.component.html',
  styleUrls: ['./quiz-request.component.scss'],
})
export class QuizRequestComponent implements OnInit {
  private quizRequestService = inject(QuizRequestService);
  private authService = inject(AuthService);
  private destroyRef = inject(DestroyRef);
  private messageService = inject(MessageService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  getEnumDisplayName = getEnumDisplayName;

  user = this.authService.user;

  requests = this.quizRequestService.requests.asReadonly();
  requestPageSize = this.quizRequestService.requestPageSize;
  requestSortBy = this.quizRequestService.requestSortBy;
  requestCurrentPage = this.quizRequestService.requestCurrentPage;
  requestTotalPages = this.quizRequestService.requestTotalPages;
  requestStatus = this.quizRequestService.requestStatus;
  isLoadingRequests = this.quizRequestService.isLoadingRequests;
  requestPagesArray = this.quizRequestService.requestPagesArray;

  requestStatusList = Object.values(RequestStatus).filter(
    (value) => typeof value === 'string'
  );

  displayedRequestColumns: string[] = [
    'quizName',
    'status',
    'dateCreated',
    'dateFinished',
    'errorMessage',
    'delete',
  ];

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const createdRequest = params['createdRequest'];
      if (createdRequest === 'true') {
        this.quizRequestService.loadRequestsPolling();

        this.router.navigate([], {
          fragment: 'requests',
          replaceUrl: true,
        });
      }
    });

    if (this.requests().length === 0) {
      this.quizRequestService.loadRequests();
    }
  }

  reloadRequests() {
    this.quizRequestService.loadRequests();
  }

  onRequestPageChange(page: number): void {
    this.requestCurrentPage.set(page);
    this.quizRequestService.loadRequests();
  }

  onDeleteQuizRequest(userId: number, quizName: string): void {
    const sub = this.quizRequestService
      .deleteQuizRequest(userId, quizName)
      .subscribe({
        error: (err) => {
          this.messageService.showErrorModal('Error deleting quiz request.');
        },
        complete: () => {
          this.messageService.showSuccessModal('Quiz request deleted.');
          this.quizRequestService.loadRequests();
        },
      });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  getStatusColor(status: string): 'primary' | 'accent' | 'warn' {
    switch (status) {
      case 'FINISHED':
        return 'accent';
      case 'FAILED':
        return 'warn';
      case 'CREATING':
        return 'primary';
      default:
        return 'primary';
    }
  }
}
