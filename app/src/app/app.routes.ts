import { CanMatchFn, RedirectCommand, Router, Routes } from '@angular/router';

import { NotFoundComponent } from './shared/not-found/not-found.component';

import { StartPageComponent } from './start/start.page.component';
import { QuizListComponent } from './quiz/quiz-list/quiz-list.component';
import {
  canLeaveEditPage,
  NewQuizComponent,
} from './quiz/new-quiz/new-quiz.component';
import { KeyComponent } from './key/key.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ProfileComponent } from './auth/profile/profile.component';
import { AuthGuard } from './shared/guards/auth.guard';
import { AboutComponent } from './about/about.component';
import { SolveQuizComponent } from './quiz/solve-quiz/solve-quiz.component';

export const routes: Routes = [
  {
    path: '',
    component: StartPageComponent,
  },
  {
    path: 'getting-started',
    component: StartPageComponent,
  },
  {
    path: 'about',
    component: AboutComponent,
  },
  {
    path: 'quizzes',
    component: QuizListComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'quizzes/new',
    component: NewQuizComponent,
    canActivate: [AuthGuard],
    canDeactivate: [canLeaveEditPage],
  },
  {
    path: 'quizzes/run/:quizId',
    component: SolveQuizComponent,
    canActivate: [AuthGuard],
    // canDeactivate: [canLeaveEditPage],
  },
  {
    path: 'keys',
    component: KeyComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
    canDeactivate: [canLeaveEditPage],
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard],
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
