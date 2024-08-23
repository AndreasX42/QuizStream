import { Component, inject, signal } from '@angular/core';

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
import { ErrorManagerFactory } from '../shared/error.manager.factory';
import { Router } from '@angular/router';
import { MessageService } from '../services/message.service';

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
  private router = inject(Router);
  private keyService = inject(KeyService);
  private messageService = inject(MessageService);

  keyErrorMessage = signal<string | undefined>(undefined);

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

  updateKeyErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.controls.key,
    this.keyErrorMessage.set,
    { required: ErrorManagerFactory.MSG_IS_REQUIRED }
  );

  onSubmit() {
    if (this.form.invalid) {
      // update error message for key field
      this.updateKeyErrorMessage();
      return;
    }

    const provider = this.form.value.provider!;
    const key = this.form.value.key!;
    this.keyService.addKey({ provider, key });

    this.router.navigate(['/keys'], {
      replaceUrl: true,
    });
  }

  onDeleteKey(keyId: string) {
    this.keyService.deleteKey(keyId);
  }
}
