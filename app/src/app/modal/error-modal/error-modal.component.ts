import { Component, inject, input } from '@angular/core';
import { ModalComponent } from '../modal.component';

import {
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MessageService } from '../../services/message.service';

@Component({
  selector: 'app-error-modal',
  standalone: true,
  imports: [
    ModalComponent,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogModule,
    MatButtonModule,
  ],
  templateUrl: './error-modal.component.html',
  styleUrl: './error-modal.component.scss',
})
export class ErrorModalComponent {
  title = input<string>();
  message = input<string>();
  private messageService = inject(MessageService);

  onClearError() {
    this.messageService.clearError();
  }
}
