import { DestroyRef, inject, Injectable, signal } from '@angular/core';
import {
  Quiz,
  QuizCreateRequestDto,
  QuizDetails,
  QuizDifficulty,
  QuizLanguage,
  QuizType,
  QuizUpdateRequestDto,
} from './../models/quiz.model';
import { AuthService } from './auth.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from './page.model';
import { Configs } from '../shared/api.configs';
import { KeyService } from './key.service';
import { MessageService } from './message.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private keyService = inject(KeyService);
  private authService = inject(AuthService);
  private httpClient = inject(HttpClient);
  private destroyRef = inject(DestroyRef);
  private messageService = inject(MessageService);
  private router = inject(Router);
  private quizzes = signal<Quiz[]>([]);

  isCreating = signal(false);

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
    language: QuizLanguage;
    type: QuizType;
    difficulty: QuizDifficulty;
  }) {
    // Convert keys map to an object
    const apiKeyObject = this.keyService.keys().reduce((obj, current) => {
      // Prepare backend API call arguments
      const currentProviderName = current.provider.toUpperCase() + '_API_KEY';
      obj[currentProviderName] = current.key;
      return obj;
    }, {} as { [key: string]: string });

    const createQuizDto: QuizCreateRequestDto = {
      ...quizData,
      userId: this.authService.user()!.id,
      apiKeys: apiKeyObject,
    };

    this.isCreating.set(true);
    const sub = this.createQuizRequest(createQuizDto).subscribe({
      next: () => {
        this.isCreating.set(false);
      },
      error: (error) => {
        console.log(error);
        this.isCreating.set(false);
        const errorMessage = Array.isArray(error.error?.messages)
          ? error.error.messages.join(' ')
          : error.error?.message || '';

        if (errorMessage?.includes('Invalid API key provided.')) {
          this.messageService.showErrorModal(
            MessageService.MSG_ERROR_API_KEY_INVALID
          );
        } else if (errorMessage?.includes('already exists for user')) {
          this.messageService.showErrorModal(
            MessageService.MSG_ERROR_QUIZ_NAME_ALREADY_EXISTS
          );
        } else if (
          errorMessage?.includes('Error fetching video transcript for ')
        ) {
          this.messageService.showErrorModal(
            MessageService.MSG_ERROR_FETCHING_VIDEO_TRANSCRIPT
          );
        } else {
          this.messageService.showErrorModal(
            MessageService.MSG_ERROR_CREATING_QUIZ
          );
        }
      },

      complete: () => {
        this.messageService.showSuccessModal(
          MessageService.MSG_SUCCESS_CREATED_QUIZ
        );
        this.router.navigate(['/quizzes'], {
          replaceUrl: true,
        });
      },
    });

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
        language: requestDto.language,
        type: requestDto.type,
        difficulty: requestDto.difficulty,
      },
      {
        observe: 'body',
      }
    );
  }

  updateQuiz(requestDto: QuizUpdateRequestDto): Observable<any> {
    return this.httpClient.put<any>(
      `${Configs.BASE_URL}${Configs.QUIZZES_ENDPOINT}/update`,
      {
        userId: requestDto.userId,
        quizId: requestDto.quizId,
        numCorrect: requestDto.numCorrect,
        quizName: '',
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

  getQuizDetails(quizId: string): Observable<any> {
    return this.httpClient.get<QuizDetails>(
      `${Configs.BASE_URL}${Configs.QUIZZES_ENDPOINT}/${quizId}${
        Configs.USERS_ENDPOINT
      }/${this.authService.user()!.id}${Configs.QUIZ_DETAILS_ENDPOINT}`
    );
  }
}
