import {
  Component,
  ElementRef,
  inject,
  ViewChild,
  viewChild,
} from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ButtonComponent } from '../../shared/button/button.component';
import { FormsModule, NgForm } from '@angular/forms';
import { QuizDifficulty, QuizType } from '../../models/quiz.model';
import { QuizService } from '../../services/quiz.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-quiz',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    ButtonComponent,
  ],
  templateUrl: './new-quiz.component.html',
  styleUrl: './new-quiz.component.css',
})
export class QuizFormComponent {
  private form = viewChild.required<ElementRef<HTMLFormElement>>('form');
  private quizService: QuizService = inject(QuizService);

  types = Object.values(QuizType).filter((value) => typeof value === 'string');

  difficulties = Object.values(QuizDifficulty).filter(
    (value) => typeof value === 'string'
  );

  onSubmit(videoLink: string, type: QuizType, difficulty: QuizDifficulty) {
    this.quizService.addQuiz({ videoLink, type, difficulty });
    this.form().nativeElement.reset();
  }
}
