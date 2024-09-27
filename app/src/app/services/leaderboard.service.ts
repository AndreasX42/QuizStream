import { DestroyRef, inject, Injectable, signal } from '@angular/core';
import { MessageService } from './message.service';
import { Observable } from 'rxjs';
import { LeaderboardEntry } from '../models/leaderboard.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Configs } from '../shared/api.configs';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root',
})
export class LeaderboardService {
  private messageService = inject(MessageService);
  private httpClient = inject(HttpClient);
  private destroyRef = inject(DestroyRef);

  leaderboardData = signal<LeaderboardEntry[]>([]);
  isLoadingLeaderboard = signal(false);

  // pagination and sorting signals
  totalItems = signal<number>(0);
  pageSize = signal<number>(10);
  sortBy = signal<string>('score');
  sortDir = signal<string>('desc');
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);

  loadLeaderboard(): void {
    this.isLoadingLeaderboard.set(true);

    const sub = this.getLeaderboardData(
      this.currentPage(),
      this.pageSize(),
      this.sortBy(),
      this.sortDir()
    ).subscribe({
      next: (data) => {
        this.totalItems.set(data.page.totalElements);
        this.totalPages.set(data.page.totalPages);
        this.leaderboardData.set(data.content);
        this.isLoadingLeaderboard.set(false);
      },
      error: () => {
        this.isLoadingLeaderboard.set(false);
        this.messageService.showErrorModal(
          MessageService.MSG_ERROR_LOADING_LEADERBOARD_DATA
        );
      },
    });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  getLeaderboardData(
    page: number = 0,
    size: number = 10,
    sort: string = 'score',
    dir: string = 'desc'
  ): Observable<Page<LeaderboardEntry>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', `${sort},${dir}`);

    return this.httpClient.get<Page<LeaderboardEntry>>(
      `${Configs.BASE_URL}${Configs.LEADERBOARD}`,
      {
        params,
      }
    );
  }
}
