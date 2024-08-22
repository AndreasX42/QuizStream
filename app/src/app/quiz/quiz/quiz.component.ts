import { DatePipe, SlicePipe, TitleCasePipe } from '@angular/common';
import {
  Component,
  DestroyRef,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { getEnumDisplayName, Quiz } from '../../models/quiz.model';
import { QuizService } from '../../services/quiz.service';
import { MessageService } from '../../services/message.service';

@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [MatCardModule, DatePipe, TitleCasePipe, MatButtonModule, SlicePipe],
  templateUrl: './quiz.component.html',
  styleUrl: './quiz.component.css',
})
export class QuizComponent {
  private destroyRef = inject(DestroyRef);
  private quizService = inject(QuizService);
  private messageService = inject(MessageService);
  getEnumDisplayName = getEnumDisplayName;

  quiz = input.required<Quiz>();
  quizDeleted = output<void>();
  isExpanded = signal(false);

  onToggleExpansion() {
    this.isExpanded.update((wasExpanded) => !wasExpanded);
  }

  onDeleteQuiz(quiz: Quiz) {
    const confirmed = window.confirm(
      'Are you sure you want to delete this quiz? This action cannot be undone.'
    );

    if (!confirmed) {
      return;
    }

    const sub = this.quizService.deleteQuiz(quiz.quizId).subscribe({
      next: () => {
        this.quizDeleted.emit();
      },
      error: (err) =>
        this.messageService.showError(
          'Error deleting quiz "' + quiz.quizName + '".'
        ),
      complete: () =>
        this.messageService.showSuccess(
          'Quiz "' + quiz.quizName + '" deleted!'
        ),
    });

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }
}
