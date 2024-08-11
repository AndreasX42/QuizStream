import { Component, ElementRef, inject, viewChild } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { KeyService } from './../services/key.service';
import { MatTableModule } from '@angular/material/table';
import { FormsModule } from '@angular/forms';
import { KeyProvider } from './../models/key.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-key',
  standalone: true,
  imports: [
    CommonModule,
    MatSelectModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    MatTableModule,
    MatIcon,
  ],
  templateUrl: './key.component.html',
  styleUrl: './key.component.css',
})
export class KeyComponent {
  private keyService: KeyService = inject(KeyService);
  private form = viewChild.required<ElementRef<HTMLFormElement>>('form');

  keys = this.keyService.getKeys();
  displayedColumns: string[] = ['provider', 'key', 'delete'];

  providers = Object.values(KeyProvider).filter(
    (value) => typeof value === 'string'
  );

  onAddKey(provider: KeyProvider, key: string) {
    this.keyService.addKey({ provider, key });
    this.form().nativeElement.reset();
  }

  onDeleteKey(keyId: string) {
    this.keyService.deleteKey(keyId);
  }
}
