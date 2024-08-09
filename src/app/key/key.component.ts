import { Component, inject } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { ButtonComponent } from '../shared/button/button.component';
import { KeyService } from './key.service';
import { Key } from './key.model';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-key',
  standalone: true,
  imports: [
    MatSelectModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    ButtonComponent,
    MatTableModule,
    MatIcon,
  ],
  templateUrl: './key.component.html',
  styleUrl: './key.component.css',
})
export class KeyComponent {
  private keyService: KeyService = inject(KeyService);

  keys = this.keyService.getKeys();
  displayedColumns: string[] = ['provider', 'key', 'delete'];

  onAddKey(provider: string, key: string) {
    this.keyService.addKey({ provider, key });
  }

  onDeleteKey(keyId: string) {
    this.keyService.deleteKey(keyId);
  }
}
