import { Component, inject, signal } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  QuizDifficulty,
  QuizLanguage,
  QuizType,
  getEnumDisplayName,
} from '../../models/quiz.model';
import { QuizService } from '../../services/quiz.service';
import { CommonModule } from '@angular/common';
import { CanDeactivateFn, Router } from '@angular/router';
import { ErrorManagerFactory } from '../../shared/error.manager.factory';
import { MatIcon } from '@angular/material/icon';
import { MessageService } from '../../services/message.service';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-new-quiz',
  standalone: true,
  imports: [
    CommonModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatProgressSpinner,
    MatIcon,
  ],
  templateUrl: './new-quiz.component.html',
  styleUrl: './new-quiz.component.css',
})
export class NewQuizComponent {
  private router = inject(Router);
  private quizService = inject(QuizService);
  private messageService = inject(MessageService);
  getEnumDisplayName = getEnumDisplayName;

  isCreating = this.quizService.isCreating.asReadonly();

  quizNameErrorMessage = signal<string | undefined>(undefined);
  linkErrorMessage = signal<string | undefined>(undefined);

  quizLanguages = Object.values(QuizLanguage).filter(
    (value) => typeof value === 'string'
  );

  quizTypesList = Object.values(QuizType).filter(
    (value) => typeof value === 'string'
  );

  quizDifficultiesList = Object.values(QuizDifficulty).filter(
    (value) => typeof value === 'string'
  );

  youtubeRegExPattern =
    /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/(watch\?v=|embed\/|v\/|.+\?v=)?(\w{11})(\S+)?$/;

  form = new FormGroup({
    quizName: new FormControl('', {
      validators: [Validators.required, Validators.minLength(3)],
    }),
    videoLink: new FormControl('', {
      validators: [
        Validators.required,
        Validators.pattern(this.youtubeRegExPattern),
      ],
    }),
    language: new FormControl<QuizLanguage>(QuizLanguage.EN, {
      validators: [Validators.required],
    }),
    type: new FormControl<QuizType>(QuizType.MULTIPLE_CHOICE, {
      validators: [Validators.required],
    }),
    difficulty: new FormControl<QuizDifficulty>(QuizDifficulty.EASY, {
      validators: [Validators.required],
    }),
  });

  updateQuizNameErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.controls.quizName,
    this.quizNameErrorMessage.set,
    {
      required: ErrorManagerFactory.MSG_IS_REQUIRED,
      minlength: ErrorManagerFactory.MSG_AT_LEAST_3_CHARS,
    }
  );

  updateLinkErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.controls.videoLink,
    this.linkErrorMessage.set,
    {
      required: ErrorManagerFactory.MSG_IS_REQUIRED,
      pattern: ErrorManagerFactory.MSG_VALID_YOUTUBE_LINK,
    }
  );

  onSubmit() {
    if (this.form.invalid) {
      this.updateQuizNameErrorMessage();
      this.updateLinkErrorMessage();
      return;
    }

    const quizName = this.form.value.quizName!;
    const videoUrl = this.form.value.videoLink!;
    const language = this.form.value.language!;
    const type = this.form.value.type!;
    const difficulty = this.form.value.difficulty!;

    this.quizService.addQuiz({
      quizName,
      videoUrl,
      language,
      type,
      difficulty,
    });
  }
}

export const canLeaveEditPage: CanDeactivateFn<NewQuizComponent> = (
  component
) => {
  if (component.form.touched && component.form.invalid) {
    return window.confirm('Do you really want to leave?');
  }
  return true;
};
