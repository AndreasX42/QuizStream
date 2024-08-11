import { Component, ElementRef, inject } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ButtonComponent } from '../../shared/button/button.component';
import {
  FormControl,
  FormGroup,
  FormsModule,
  NgForm,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { QuizDifficulty, QuizType } from '../../models/quiz.model';
import { QuizService } from '../../services/quiz.service';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../shared/dialog/dialog.component';

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
    ReactiveFormsModule,
  ],
  templateUrl: './new-quiz.component.html',
  styleUrl: './new-quiz.component.css',
})
export class QuizFormComponent {
  private quizService: QuizService = inject(QuizService);
  private dialog = inject(MatDialog);

  quizTypesList = Object.values(QuizType).filter(
    (value) => typeof value === 'string'
  );

  quizDifficultiesList = Object.values(QuizDifficulty).filter(
    (value) => typeof value === 'string'
  );

  form = new FormGroup({
    name: new FormControl('', { validators: [Validators.required] }),
    videoLink: new FormControl('', {
      validators: [Validators.required],
    }),
    type: new FormControl<QuizType>(QuizType.MULTIPLE_CHOICE, {
      validators: [Validators.required],
    }),
    difficulty: new FormControl<QuizDifficulty>(QuizDifficulty.EASY, {
      validators: [Validators.required],
    }),
  });

  onSubmit() {
    if (this.form.invalid) {
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
