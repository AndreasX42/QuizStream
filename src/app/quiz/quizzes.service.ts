import { Injectable, OnInit, signal } from '@angular/core';
import { Quiz } from './quiz.model';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private initQuizList: Quiz[] = [
    {
      id: '1',
      userId: '1',
      name: 'My YouTube Video 1',
      videoLink: 'www.yt.com',
      date: '10/12/2023',
      difficulty: 'EASY',
      type: 'MULTIPLE_CHOICE',
    },
    {
      id: '2',
      userId: '1',
      name: 'My YouTube Video 2',
      videoLink: 'www.yt.com',
      date: '12/12/2023',
      difficulty: 'MEDIUM',
      type: 'MULTIPLE_CHOICE',
    },
  ];

  private quizzes = signal<Quiz[]>(this.initQuizList);

  getQuizzes() {
    return this.quizzes.asReadonly();
  }

  deleteQuiz(quizId: string) {
    this.quizzes.update((oldQuizzes) =>
      oldQuizzes.filter((quiz) => quiz.id !== quizId)
    );
  }
}
