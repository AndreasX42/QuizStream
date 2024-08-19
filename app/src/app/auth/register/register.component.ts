import { Component, DestroyRef, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { ErrorManagerFactory } from '../../shared/error.manager.factory';
import { AuthService } from '../../services/auth.service';

function equalValues(controlName1: string, controlName2: string) {
  return (control: AbstractControl) => {
    const val1 = control.get(controlName1)?.value;
    const val2 = control.get(controlName2)?.value;

    if (val1 === val2) {
      return null;
    }

    const errorMessage = { valuesNotEqual: true };
    if (controlName2 === 'confirmPassword') {
      control.get(controlName2)?.setErrors(errorMessage);
    }

    return { valuesNotEqual: true };
  };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatIcon,
    RouterLink,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);

  hide = signal(true);
  usernameErrorMessage = signal<string | undefined>(undefined);
  emailErrorMessage = signal<string | undefined>(undefined);
  pwdErrorMessage = signal<string | undefined>(undefined);
  confirmPwdErrorMessage = signal<string | undefined>(undefined);

  form = new FormGroup({
    username: new FormControl('', {
      validators: [Validators.required, Validators.minLength(3)],
    }),
    email: new FormControl('', {
      validators: [Validators.email, Validators.required],
    }),
    passwords: new FormGroup(
      {
        password: new FormControl('', {
          validators: [Validators.required, Validators.minLength(6)],
        }),
        confirmPassword: new FormControl('', {
          validators: [Validators.required, Validators.minLength(6)],
        }),
      },
      {
        validators: [equalValues('password', 'confirmPassword')],
      }
    ),
  });

  updateUsernameErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.get('username')!,
    this.usernameErrorMessage.set,
    {
      required: ErrorManagerFactory.MSG_IS_REQUIRED,
      minlength: ErrorManagerFactory.MSG_AT_LEAST_3_CHARS,
    }
  );

  updateEmailErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.get('email')!,
    this.emailErrorMessage.set,
    {
      required: ErrorManagerFactory.MSG_IS_REQUIRED,
      email: ErrorManagerFactory.MSG_VALID_EMAIL,
    }
  );

  updatePwdErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.controls.passwords.get('password')!,
    this.pwdErrorMessage.set,
    {
      required: ErrorManagerFactory.MSG_IS_REQUIRED,
      minlength: ErrorManagerFactory.MSG_AT_LEAST_6_CHARS,
    }
  );

  updateConfirmPwdErrorMessage = ErrorManagerFactory.getFormErrorManager(
    this.form.controls.passwords.get('confirmPassword')!,
    this.confirmPwdErrorMessage.set,
    {
      required: ErrorManagerFactory.MSG_IS_REQUIRED,
      minlength: ErrorManagerFactory.MSG_AT_LEAST_6_CHARS,
      valuesNotEqual: 'Passwords must match',
    }
  );

  onSubmit() {
    if (this.form.invalid) {
      // update error messages for all form fields
      this.updateUsernameErrorMessage();
      this.updateEmailErrorMessage();
      this.updatePwdErrorMessage();
      this.updateConfirmPwdErrorMessage();
      return;
    }

    const username = this.form.value.username!;
    const email = this.form.value.email!;
    const password = this.form.value.passwords!.password!;

    this.register(username, email, password);
  }

  register(username: string, email: string, password: string) {
    const sub = this.authService
      .register(username, email, password)
      .subscribe();

    this.destroyRef.onDestroy(() => {
      sub.unsubscribe();
    });
  }

  onHide(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
}
