import { CanMatchFn, RedirectCommand, Router, Routes } from '@angular/router';

import { inject } from '@angular/core';

import { NotFoundComponent } from './not-found/not-found.component';

import { StartPageComponent } from './start/start.page.component';
import {
  QuizListComponent,
  resolveQuizzes,
} from './quiz/quiz-list/quiz-list.component';
import {
  canLeaveEditPage,
  NewQuizComponent,
} from './quiz/new-quiz/new-quiz.component';
import { KeyComponent } from './key/key.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ProfileComponent } from './auth/profile/profile.component';

const dummyCanMatch: CanMatchFn = (route, segments) => {
  const router = inject(Router);
  const shouldGetAccess = Math.random();
  if (shouldGetAccess < 0.5) {
    return true;
  }
  return new RedirectCommand(router.parseUrl('/unauthorized'));
};

export const routes: Routes = [
  {
    path: '',
    component: StartPageComponent,
  },
  {
    path: 'quizzes',
    component: QuizListComponent,
  },
  {
    path: 'quizzes/new',
    component: NewQuizComponent,
    // canDeactivate: [canLeaveEditPage],
  },
  {
    path: 'keys',
    component: KeyComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'profile',
    component: ProfileComponent,
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
