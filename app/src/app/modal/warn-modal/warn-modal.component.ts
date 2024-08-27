import { Component, inject, input } from '@angular/core';
import { MessageService } from '../../services/message.service';
import { ModalComponent } from '../modal.component';
import {
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-warn-modal',
  standalone: true,
  imports: [
    ModalComponent,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogModule,
    MatButtonModule,
  ],
  templateUrl: './warn-modal.component.html',
  styleUrl: './warn-modal.component.scss',
})
export class WarnModalComponent {
  title = input<string>();
  message = input<string>();
  private messageService = inject(MessageService);

  onClearWarning() {
    this.messageService.clearWarning();
  }
}
