import { Component, computed, input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css',
})
export class ButtonComponent {
  selected = input('');
  name = input.required<string>();

  isActive = computed(() => this.name() === this.selected());
}
