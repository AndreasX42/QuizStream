import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { Configs } from '../shared/api.configs';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss',
})
export class AboutComponent {
  baseUrl = Configs.BASE_URL;
}
