import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-warn-modal',
  standalone: true,
  imports: [
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
  constructor(
    public dialogRef: MatDialogRef<WarnModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string }
  ) {}

  onClearWarning() {
    this.dialogRef.close();
  }
}
