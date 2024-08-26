import { environment } from './environment/environment';

export class Configs {
  static BASE_URL: string = environment.apiUrl;
  static QUIZZES_ENDPOINT: string = '/quizzes';
  static USERS_ENDPOINT: string = '/users';
  static QUIZ_DETAILS_ENDPOINT: string = '/details';
  static LOGIN_URL: string = this.USERS_ENDPOINT + '/login';
  static REGISTER_URL: string = this.USERS_ENDPOINT + '/register';
  static GET_BY_USERNAME: string = this.USERS_ENDPOINT + '/name';
  static GET_ALL_QUIZZES_BY_USER_ID: string =
    this.QUIZZES_ENDPOINT + this.USERS_ENDPOINT;
}
