import { Component } from '@angular/core';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss',
})
export class AboutComponent {
  buildNumber = '119'; // Dynamic data
  commitSha = '20411bb0'; // Dynamic data
  buildTime = '2024-08-09 12:46:36'; // Dynamic data

  buildUrl = 'https://circleci.com/gh/AndreasX42/QuizStream/119'; // Replace with your CircleCI build URL
  commitUrl = 'https://github.com/AndreasX42/QuizStream/commit/20411bb0'; // Replace with your commit URL
}
