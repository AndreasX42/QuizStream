import { HttpClient, HttpParams } from '@angular/common/http';
import { DestroyRef, inject, Injectable, signal } from '@angular/core';
import {
  catchError,
  interval,
  Observable,
  of,
  switchMap,
  takeWhile,
  timeout,
} from 'rxjs';
import { Configs } from '../shared/api.configs';
import {
  getEnumDisplayName,
  QuizRequest,
  RequestStatus,
} from '../models/quiz.model';
import { Page } from '../models/page.model';
import { AuthService } from './auth.service';
import { MessageService } from './message.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class QuizRequestService {
  private httpClient = inject(HttpClient);
  private authService = inject(AuthService);
  private messageService = inject(MessageService);
  private destroyRef = inject(DestroyRef);
  private router = inject(Router);

  isLoadingRequests = signal(false);
  requests = signal<QuizRequest[]>([]);

  // pagination and sorting signals
  totalItems = signal<number>(0);
  pageSize = signal<number>(5);
  sortBy = signal<string>('dateCreated,desc');
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  requestStatus = signal<RequestStatus>(RequestStatus.ALL);

  requestPagesArray(): number[] {
    return Array.from({ length: this.totalPages() }, (_, index) => index);
  }

  loadRequests(): void {
    this.isLoadingRequests.set(true);

    const sub = this.getQuizRequests(
      this.authService.user()!.id,
      this.currentPage(),
      this.pageSize(),
      this.sortBy(),
      this.requestStatus()
    ).subscribe({
      next: (page) => {
        this.totalItems.set(page.page.totalElements);
        this.totalPages.set(page.page.totalPages);
        this.isLoadingRequests.set(false);

        this.requests.set(page.content);
        if (
          this.requests().some((req) => req.status === RequestStatus.CREATING)
        ) {
          this.loadRequestsPolling();
        }
      },
      error: () => {
        this.isLoadingRequests.set(false);
        this.messageService.showErrorModal(
          MessageService.MSG_ERROR_LOADING_QUIZ_REQUESTS
        );
      },
    });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  loadRequestsPolling(): void {
    this.isLoadingRequests.set(true);

    const polling = interval(3000).pipe(
      switchMap(() =>
        this.getQuizRequests(
          this.authService.user()!.id,
          this.currentPage(),
          this.pageSize(),
          this.sortBy(),
          this.requestStatus()
        )
      ),
      takeWhile(
        (reqList) =>
          reqList.content.some((req) => req.status === RequestStatus.CREATING),
        true
      ),
      timeout(300000),
      catchError((err) => {
        console.error('Polling timed out after 5 minutes', err);
        return of({ content: [], page: { totalPages: 0 } });
      })
    );

    const sub = polling.subscribe({
      next: (page) => {
        this.totalPages.set(page.page.totalPages);
        this.requests.set(page.content);
        this.isLoadingRequests.set(false);
      },
      error: () => {
        this.isLoadingRequests.set(false);
        this.messageService.showErrorModal(
          MessageService.MSG_ERROR_LOADING_QUIZ_REQUESTS
        );
      },
      complete: () => {
        this.router.navigate([], {
          fragment: 'requests',
          replaceUrl: true,
        });
      },
    });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  getQuizRequests(
    userId: string,
    page: number,
    size: number,
    sort: string,
    status: RequestStatus
  ): Observable<Page<QuizRequest>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (status !== RequestStatus.ALL) {
      params = params.set('status', status.toString());
    }

    return this.httpClient.get<Page<QuizRequest>>(
      `${Configs.BASE_URL}${Configs.QUIZ_REQUESTS}${Configs.USERS_ENDPOINT}/${userId}`,
      {
        params,
      }
    );
  }

  deleteQuizRequest(userId: number, quizName: string): Observable<void> {
    const body = {
      body: { userId: userId, quizName: quizName },
    };

    return this.httpClient.request<void>(
      'DELETE',
      `${Configs.BASE_URL}${Configs.QUIZ_REQUESTS}`,
      body
    );
  }
}
