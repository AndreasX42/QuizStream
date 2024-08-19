import { Component, inject, input } from '@angular/core';
import { ModalComponent } from '../modal.component';
import {
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ErrorService } from '../../services/error.service';

@Component({
  selector: 'app-success-modal',
  standalone: true,
  imports: [
    ModalComponent,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogModule,
    MatButtonModule,
  ],
  templateUrl: './success-modal.component.html',
  styleUrl: './success-modal.component.css',
})
export class SuccessModalComponent {
  title = input<string>();
  message = input<string>();
  private errorService = inject(ErrorService);

  onClearSuccess() {
    this.errorService.clearSuccess();
  }
}
