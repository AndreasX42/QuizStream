import { DestroyRef, inject, Injectable, OnInit, signal } from '@angular/core';
import {
  Quiz,
  QuizCreateRequestDto,
  QuizDifficulty,
  QuizType,
} from './../models/quiz.model';
import { AuthService } from './auth.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from './page.model';
import { Configs } from '../shared/api.configs';
import { KeyService } from './key.service';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private keyService = inject(KeyService);
  private authService = inject(AuthService);
  private httpClient = inject(HttpClient);
  private destroyRef = inject(DestroyRef);
  private quizzes = signal<Quiz[]>([]);

  getQuizzes() {
    return this.quizzes.asReadonly();
  }

  getAllQuizzes(
    userId: number,
    page: number,
    size: string,
    sort: string
  ): Observable<Page<Quiz>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size)
      .set('sort', sort);

    return this.httpClient.get<Page<Quiz>>(
      `${Configs.BASE_URL}${Configs.GET_ALL_QUIZZES_BY_USER_ID}/${userId}`,
      {
        params,
      }
    );
  }

  addQuiz(quizData: {
    quizName: string;
    videoUrl: string;
    type: QuizType;
    difficulty: QuizDifficulty;
  }) {
    // Convert keys map to an object
    const apiKeyObject = this.keyService.keys().reduce((obj, current) => {
      const currentProviderName = current.provider.toUpperCase() + '_API_KEY';
      obj[currentProviderName] = current.key;
      return obj;
    }, {} as { [key: string]: string });

    const createQuizDto: QuizCreateRequestDto = {
      ...quizData,
      userId: this.authService.user()!.id,
      apiKeys: apiKeyObject,
    };

    const sub = this.createQuizRequest(createQuizDto).subscribe();

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  createQuizRequest(requestDto: QuizCreateRequestDto): Observable<any> {
    return this.httpClient.post<any>(
      `${Configs.BASE_URL}${Configs.QUIZZES_ENDPOINT}/new`,
      {
        userId: requestDto.userId,
        quizName: requestDto.quizName,
        videoUrl: requestDto.videoUrl,
        apiKeys: requestDto.apiKeys,
        type: requestDto.type,
        difficulty: requestDto.difficulty,
      },
      {
        observe: 'body',
      }
    );
  }

  deleteQuiz(quizId: string): Observable<void> {
    return this.httpClient.delete<void>(
      `${Configs.BASE_URL}${Configs.QUIZZES_ENDPOINT}/${quizId}${
        Configs.USERS_ENDPOINT
      }/${this.authService.user()!.id}`
    );
  }
}
