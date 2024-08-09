import { Component } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ButtonComponent } from '../../shared/button/button.component';

@Component({
  selector: 'app-new-quiz',
  standalone: true,
  imports: [
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    ButtonComponent,
  ],
  templateUrl: './new-quiz.component.html',
  styleUrl: './new-quiz.component.css',
})
export class QuizFormComponent {}
