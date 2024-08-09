import { Component, input, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ButtonComponent } from '../shared/button/button.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatToolbarModule, MatIconModule, ButtonComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  title = input<string>();
  selectedButton = signal('');

  onSelect(btnName: string) {
    this.selectedButton.set(btnName);
  }
}
