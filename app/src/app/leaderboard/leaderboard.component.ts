import { Component, inject } from '@angular/core';
import { LeaderboardService } from '../services/leaderboard.service';
import { LeaderboardEntry } from '../models/leaderboard.model';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [MatProgressSpinner, MatTableModule],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.scss',
})
export class LeaderboardComponent {
  private leaderboardService = inject(LeaderboardService);

  isLoading = this.leaderboardService.isLoadingLeaderboard.asReadonly();
  leaderboardEntries = this.leaderboardService.leaderboardData.asReadonly();

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

    // if (this.leaderboardService.leaderboardData().length === 0) {
    //   this.quizRequestService.loadRequests();
    // }
  }
}
