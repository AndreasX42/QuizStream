import { inject, Injectable, signal } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ErrorModalComponent } from '../modal/error-modal/error-modal.component';
import { SuccessModalComponent } from '../modal/success-modal/success-modal.component';
import { WarnModalComponent } from '../modal/warn-modal/warn-modal.component';
import { ConfirmModalComponent } from '../modal/confirm-modal/confirm-modal.component';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  // List of messages
  // Success messages
  static MSG_SUCCESS_REGISTER = 'Registered successfully, you can log in now.';
  static MSG_SUCCESS_CREATED_QUIZ = 'Quiz request created successfully.';
  static MSG_SUCCESS_ADDED_API_KEY = 'API key was added successfully.';
  static MSG_SUCCESS_DELETE_USER_ACCOUNT = 'Account was deleted successfully.';

  // Error messages
  static MSG_ERROR_UNKOWN = 'Something went wrong, please try again later.';
  static MSG_ERROR_LOGIN_USERNAME_OR_PASSWORD_INCORRECT =
    'Username or password incorrect.';
  static MSG_ERROR_LOAD_PROFILE_DATA =
    'Something went wrong loading profile data, please try again later.';
  static MSG_ERROR_DELETE_USER_ACCOUNT =
    'Something went wrong deleting account, please try again later.';
  static MSG_ERROR_SESSION_EXPIRED =
    'Your session has expired. Please log in again.';
  static MSG_ERROR_QUIZ_UPDATE_FAILED =
    'Your quiz statistics could not be updated.';
  static MSG_ERROR_LOADING_QUIZ_LIST =
    'Unexpected error loading quizzes, try again later.';
  static MSG_ERROR_LOADING_QUIZ_REQUESTS =
    'Unexpected error loading quiz requests, try again later.';
  static MSG_ERROR_LOADING_QUIZ =
    'Something went wrong loading quiz details, try again later.';
  static MSG_ERROR_API_KEY_PROVIDER_ALREADY_EXISTS =
    'An API key of this provider already exists.';
  static MSG_ERROR_API_KEY_INVALID = 'The provided API key is invalid.';
  static MSG_ERROR_QUIZ_NAME_ALREADY_EXISTS =
    'You already created a quiz with this name, choose another one.';
  static MSG_ERROR_FETCHING_VIDEO_TRANSCRIPT =
    'Something went wrong fetching the video transcript, try another video.';
  static MSG_ERROR_CREATING_QUIZ_REQUEST =
    'Something went wrong creating the quiz request.';

  // Warning messages
  static MSG_WARNING_LOGIN_FIST =
    'You have to log in, in order to use this service.';
  static MSG_WARNING_DELETE_USER_ACCOUNT =
    'Deleting user account cannot be undone. Are you sure?';

  // Dialog handling with MatDialog
  private readonly dialog = inject(MatDialog);

  showErrorModal(message: string): MatDialogRef<ErrorModalComponent, void> {
    return this.dialog.open(ErrorModalComponent, {
      data: { title: 'An error occurred!', message: message },
    });
  }

  showSuccessModal(message: string): MatDialogRef<SuccessModalComponent, void> {
    return this.dialog.open(SuccessModalComponent, {
      data: { title: 'Action successful!', message: message },
    });
  }

  showWarningModal(message: string): MatDialogRef<WarnModalComponent, void> {
    return this.dialog.open(WarnModalComponent, {
      data: { title: 'Warning!', message: message },
    });
  }

  showConfirmModal(message: string): MatDialogRef<ConfirmModalComponent, void> {
    return this.dialog.open(ConfirmModalComponent, {
      data: { title: 'Warning!', message: message },
    });
  }
}
