import { DatePipe, SlicePipe, TitleCasePipe } from '@angular/common';
import {
  Component,
  ElementRef,
  inject,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Quiz } from '../../models/quiz.model';
import { QuizService } from '../../services/quiz.service';

@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [MatCardModule, DatePipe, TitleCasePipe, MatButtonModule, SlicePipe],
  templateUrl: './quiz.component.html',
  styleUrl: './quiz.component.css',
})
export class QuizComponent {
  quiz = input.required<Quiz>();
  private quizService: QuizService = inject(QuizService);
  isExpanded = signal(false);

  onToggleExpansion() {
    this.isExpanded.update((wasExpanded) => !wasExpanded);
  }

  onDeleteQuiz(quizId: string) {
    this.quizService.deleteQuiz(quizId);
  }
}
