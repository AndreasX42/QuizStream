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
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../shared/dialog/dialog.component';
import { CanDeactivateFn } from '@angular/router';
import { ErrorManagerFactory } from '../../shared/error.manager.factory';

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
  ],
  templateUrl: './new-quiz.component.html',
  styleUrl: './new-quiz.component.css',
})
export class NewQuizComponent {
  private quizService: QuizService = inject(QuizService);
  private dialog = inject(MatDialog);

  nameErrorMessage = signal('');
  linkErrorMessage = signal('');

  quizTypesList = Object.values(QuizType).filter(
    (value) => typeof value === 'string'
  );

  quizDifficultiesList = Object.values(QuizDifficulty).filter(
    (value) => typeof value === 'string'
  );

  youtubeRegExPattern =
    /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/(watch\?v=|embed\/|v\/|.+\?v=)?(\w{11})(\S+)?$/;

  form = new FormGroup({
    name: new FormControl('', { validators: [Validators.required] }),
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

  updateNameErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.controls.name,
    this.nameErrorMessage.set,
    { required: 'Must not be blank' }
  );

  updateLinkErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.controls.videoLink,
    this.linkErrorMessage.set,
    { required: 'Must not be blank', pattern: 'Must be a valid YouTube link' }
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

    const dialogRef = this.dialog.open(DialogComponent, {
      data: { message: 'Quiz was created successfully!' },
    });

    dialogRef.afterClosed().subscribe(() => {
      location.reload();
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
