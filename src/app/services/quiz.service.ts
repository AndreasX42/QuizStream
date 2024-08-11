import { Injectable, OnInit, signal } from '@angular/core';
import { Quiz, QuizDifficulty, QuizType } from './../models/quiz.model';

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
      difficulty: QuizDifficulty.EASY,
      type: QuizType.MULTIPLE_CHOICE,
    },
    {
      id: '2',
      userId: '1',
      name: 'My YouTube Video 2',
      videoLink: 'www.yt.com',
      date: '12/12/2023',
      difficulty: QuizDifficulty.MEDIUM,
      type: QuizType.MULTIPLE_CHOICE,
    },
  ];

  private quizzes = signal<Quiz[]>(this.initQuizList);

  getQuizzes() {
    return this.quizzes.asReadonly();
  }

  addQuiz(quizData: {
    videoLink: string;
    type: QuizType;
    difficulty: QuizDifficulty;
  }) {
    const newQuiz: Quiz = {
      ...quizData,
      id:
        this.quizzes().length > 0
          ? (Math.max(...this.quizzes().map((quiz) => +quiz.id)) + 1).toString()
          : '0',
      userId: '0',
      name: 'Test video',
      date: Date.now().toString(),
    };

    this.quizzes.update((oldQuizzes) => [...oldQuizzes, newQuiz]);
  }

  deleteQuiz(quizId: string) {
    this.quizzes.update((oldQuizzes) =>
      oldQuizzes.filter((quiz) => quiz.id !== quizId)
    );
  }
}
