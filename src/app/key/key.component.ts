import { Component, ElementRef, inject, viewChild } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { KeyService } from './key.service';
import { MatTableModule } from '@angular/material/table';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-key',
  standalone: true,
  imports: [
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

  onAddKey(provider: string, key: string) {
    this.keyService.addKey({ provider, key });
    this.form().nativeElement.reset();
  }

  onDeleteKey(keyId: string) {
    this.keyService.deleteKey(keyId);
  }
}
