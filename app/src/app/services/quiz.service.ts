import { inject, Injectable, OnInit, signal } from '@angular/core';
import { Quiz, QuizDifficulty, QuizType } from './../models/quiz.model';
import { Util } from '../shared/util';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private initQuizList: Quiz[] = [
    {
      id: '1',
      userId: 1,
      name: 'My YouTube Video 1',
      videoLink: 'www.yt.com',
      dateCreated: '10/12/2023',
      difficulty: QuizDifficulty.EASY,
      type: QuizType.MULTIPLE_CHOICE,
    },
    {
      id: '2',
      userId: 1,
      name: 'My YouTube Video 2',
      videoLink: 'www.yt.com',
      dateCreated: '12/12/2023',
      difficulty: QuizDifficulty.MEDIUM,
      type: QuizType.MULTIPLE_CHOICE,
    },
  ];

  private authService = inject(AuthService);
  private quizzes = signal<Quiz[]>(this.initQuizList);

  getQuizzes() {
    return this.quizzes.asReadonly();
  }

  addQuiz(quizData: {
    name: string;
    videoLink: string;
    type: QuizType;
    difficulty: QuizDifficulty;
  }) {
    const newQuiz: Quiz = {
      ...quizData,
      id: Util.getNextIncrement(this.quizzes()),
      userId: this.authService.user()!.id,
      dateCreated: Date.now().toString(),
    };

    this.quizzes.update((oldQuizzes) => [...oldQuizzes, newQuiz]);
  }

  deleteQuiz(quizId: string) {
    this.quizzes.update((oldQuizzes) =>
      oldQuizzes.filter((quiz) => quiz.id !== quizId)
    );
  }
}
