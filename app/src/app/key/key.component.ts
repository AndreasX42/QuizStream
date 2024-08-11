import { Component, ElementRef, inject, viewChild } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { KeyService } from './../services/key.service';
import { MatTableModule } from '@angular/material/table';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { KeyProvider } from './../models/key.model';
import { CommonModule, SlicePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../shared/dialog/dialog.component';

@Component({
  selector: 'app-key',
  standalone: true,
  imports: [
    CommonModule,
    MatSelectModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatTableModule,
    MatIcon,
    ReactiveFormsModule,
    SlicePipe,
  ],
  templateUrl: './key.component.html',
  styleUrl: './key.component.css',
})
export class KeyComponent {
  private keyService: KeyService = inject(KeyService);
  private dialog = inject(MatDialog);

  keys = this.keyService.getKeys();
  displayedColumns: string[] = ['provider', 'key', 'delete'];
  keyProvidersList = Object.values(KeyProvider).filter(
    (value) => typeof value === 'string'
  );

  form = new FormGroup({
    provider: new FormControl<KeyProvider>(KeyProvider.OpenAI, {
      validators: [Validators.required],
    }),
    key: new FormControl('', {
      validators: [Validators.required],
    }),
  });

  onSubmit() {
    if (this.form.invalid) {
      return;
    }

    const provider = this.form.value.provider!;
    const key = this.form.value.key!;
    this.keyService.addKey({ provider, key });

    const dialogRef = this.dialog.open(DialogComponent, {
      data: { message: 'API key was added successfully!' },
    });

    dialogRef.afterClosed().subscribe(() => {
      location.reload();
    });
  }

  onDeleteKey(keyId: string) {
    this.keyService.deleteKey(keyId);
  }
}
