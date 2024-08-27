import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [MatButtonModule, RouterLink],
  templateUrl: './start.page.component.html',
  styleUrl: './start.page.component.scss',
})
export class StartPageComponent {}
