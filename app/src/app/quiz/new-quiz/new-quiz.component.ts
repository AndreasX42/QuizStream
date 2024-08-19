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
import { QuizDifficulty, QuizType } from '../../models/quiz.model';
import { QuizService } from '../../services/quiz.service';
import { CommonModule } from '@angular/common';
import { CanDeactivateFn, Router } from '@angular/router';
import { ErrorManagerFactory } from '../../shared/error.manager.factory';
import { MatIcon } from '@angular/material/icon';
import { MessageService } from '../../services/message.service';

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
    MatIcon,
  ],
  templateUrl: './new-quiz.component.html',
  styleUrl: './new-quiz.component.css',
})
export class NewQuizComponent {
  private router = inject(Router);
  private quizService = inject(QuizService);
  private messageService = inject(MessageService);

  nameErrorMessage = signal<string | undefined>(undefined);
  linkErrorMessage = signal<string | undefined>(undefined);

  quizTypesList = Object.values(QuizType).filter(
    (value) => typeof value === 'string'
  );

  quizDifficultiesList = Object.values(QuizDifficulty).filter(
    (value) => typeof value === 'string'
  );

  youtubeRegExPattern =
    /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/(watch\?v=|embed\/|v\/|.+\?v=)?(\w{11})(\S+)?$/;

  form = new FormGroup({
    name: new FormControl('', {
      validators: [Validators.required, Validators.minLength(3)],
    }),
    videoLink: new FormControl('', {
      validators: [
        Validators.required,
        Validators.pattern(this.youtubeRegExPattern),
      ],
    }),
    type: new FormControl<QuizType>(QuizType.MULTIPLE_CHOICE, {
      validators: [Validators.required],
    }),
    difficulty: new FormControl<QuizDifficulty>(QuizDifficulty.EASY, {
      validators: [Validators.required],
    }),
  });

  updateNameErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.controls.name,
    this.nameErrorMessage.set,
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
      this.updateNameErrorMessage();
      this.updateLinkErrorMessage();
      return;
    }

    const name = this.form.value.name!;
    const videoLink = this.form.value.videoLink!;
    const type = this.form.value.type!;
    const difficulty = this.form.value.difficulty!;
    this.quizService.addQuiz({ name, videoLink, type, difficulty });

    this.messageService.showSuccess('Quiz was created successfully!');

    this.router.navigate(['/quizzes'], {
      replaceUrl: true,
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
