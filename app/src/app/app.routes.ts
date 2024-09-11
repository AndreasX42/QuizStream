import { Routes } from '@angular/router';
import { StartPageComponent } from './start/start.page.component';
import { AuthGuard } from './shared/guards/auth.guard';
import { canLeaveEditPage } from './shared/guards/leave.guard';

export const routes: Routes = [
  {
    path: '',
    component: StartPageComponent,
  },
  {
    path: 'about',
    loadComponent: () =>
      import('./about/about.component').then((mod) => mod.AboutComponent),
  },
  {
    path: 'quizzes',
    loadComponent: () =>
      import('./quiz/quiz-list/quiz-list.component').then(
        (mod) => mod.QuizListComponent
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'quizzes/new',
    loadComponent: () =>
      import('./quiz/new-quiz/new-quiz.component').then(
        (mod) => mod.NewQuizComponent
      ),
    canActivate: [AuthGuard],
    canDeactivate: [canLeaveEditPage],
  },
  {
    path: 'quizzes/run/:quizId',
    loadComponent: () =>
      import('./quiz/solve-quiz/solve-quiz.component').then(
        (mod) => mod.SolveQuizComponent
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'keys',
    loadComponent: () =>
      import('./key/key.component').then((mod) => mod.KeyComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component').then((mod) => mod.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register/register.component').then(
        (mod) => mod.RegisterComponent
      ),
    canDeactivate: [canLeaveEditPage],
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./auth/profile/profile.component').then(
        (mod) => mod.ProfileComponent
      ),
    canActivate: [AuthGuard],
  },
  {
    path: '**',
    loadComponent: () =>
      import('./shared/not-found/not-found.component').then(
        (mod) => mod.NotFoundComponent
      ),
  },
];
