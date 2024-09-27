import { Component, inject } from '@angular/core';
import { LeaderboardService } from '../services/leaderboard.service';
import { LeaderboardEntry } from '../models/leaderboard.model';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [
    MatProgressSpinner,
    MatTableModule,
    MatSelectModule,
    MatPaginatorModule,
  ],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.scss',
})
export class LeaderboardComponent {
  private leaderboardService = inject(LeaderboardService);

  isLoading = this.leaderboardService.isLoadingLeaderboard.asReadonly();
  leaderboardEntries = this.leaderboardService.leaderboardData.asReadonly();

  pageSize = this.leaderboardService.pageSize;
  totalItems = this.leaderboardService.totalItems;
  sortBy = this.leaderboardService.sortBy;
  sortDir = this.leaderboardService.sortDir;
  currentPage = this.leaderboardService.currentPage;
  totalPages = this.leaderboardService.totalPages;

  displayedRequestColumns: string[] = [
    'username',
    'numberQuizzes',
    'numberAttempts',
    'numberQuestions',
    'numberCorrectAnswers',
    'score',
  ];

  ngOnInit(): void {
    this.leaderboardService.loadLeaderboard();
  }

  reloadData(): void {
    this.leaderboardService.loadLeaderboard();
  }

  onPaginatorChange(event: PageEvent) {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.leaderboardService.loadLeaderboard();
  }
}
