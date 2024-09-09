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
import { Page } from './page.model';
import { AuthService } from './auth.service';
import { MessageService } from './message.service';

@Injectable({
  providedIn: 'root',
})
export class QuizRequestService {
  private httpClient = inject(HttpClient);
  private authService = inject(AuthService);
  private messageService = inject(MessageService);
  private destroyRef = inject(DestroyRef);

  isLoadingRequests = signal(false);
  requests = signal<QuizRequest[]>([]);

  // pagination and sorting signals
  requestPageSize = signal<string>('5');
  requestSortBy = signal<string>('dateCreated,desc');
  requestCurrentPage = signal<number>(0);
  requestTotalPages = signal<number>(0);
  requestStatus = signal<RequestStatus>(RequestStatus.ALL);

  requestPagesArray(): number[] {
    return Array.from(
      { length: this.requestTotalPages() },
      (_, index) => index
    );
  }

  loadRequests(): void {
    this.isLoadingRequests.set(true);

    const sub = this.getQuizRequests(
      this.authService.user()!.id,
      this.requestCurrentPage(),
      this.requestPageSize(),
      this.requestSortBy(),
      this.requestStatus()
    ).subscribe({
      next: (page) => {
        this.requestTotalPages.set(page.page.totalPages);
        this.requests.set(page.content);
        this.isLoadingRequests.set(false);

        // continue fetching requests if some are still in creating state
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
          this.requestCurrentPage(),
          this.requestPageSize(),
          this.requestSortBy(),
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
        this.requestTotalPages.set(page.page.totalPages);
        this.requests.set(page.content);
        this.isLoadingRequests.set(false);
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

  getQuizRequests(
    userId: number,
    page: number,
    size: string,
    sort: string,
    status: RequestStatus
  ): Observable<Page<QuizRequest>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size)
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
