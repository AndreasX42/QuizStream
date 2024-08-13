import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DialogComponent } from '../../shared/dialog/dialog.component';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { ErrorManagerFactory } from '../../shared/error.manager.factory';

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
  private dialog = inject(MatDialog);
  private router = inject(Router);

  hide = signal(true);
  usernameErrorMessage = signal('');
  emailErrorMessage = signal('');
  pwdErrorMessage = signal('');
  confirmPwdErrorMessage = signal('');

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

  updateUsernameErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.get('username')!,
    this.usernameErrorMessage.set,
    {
      required: 'Must not be blank',
      minlength: 'Must be at least 3 charachters',
    }
  );

  updateEmailErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.get('email')!,
    this.emailErrorMessage.set,
    {
      required: 'Must not be blank',
      email: 'Must be valid email',
    }
  );

  updatePwdErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.controls.passwords.get('password')!,
    this.pwdErrorMessage.set,
    {
      required: 'Must not be blank',
      minlength: 'Must be at least 6 characters',
    }
  );

  updateConfirmPwdErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.controls.passwords.get('confirmPassword')!,
    this.confirmPwdErrorMessage.set,
    {
      required: 'Must not be blank',
      minlength: 'Must be at least 6 characters',
      valuesNotEqual: 'Passwords must match',
    }
  );

  onSubmit() {
    if (this.form.invalid) {
      this.updateUsernameErrorMessage();
      this.updateEmailErrorMessage();
      this.updatePwdErrorMessage();
      this.updateConfirmPwdErrorMessage();
      return;
    }

    const username = this.form.value.username!;
    const email = this.form.value.email;
    const password = this.form.value.passwords!.password!;

    console.log(username, email, password);

    const dialogRef = this.dialog.open(DialogComponent, {
      data: { message: 'Registered successfully! You can log in now.' },
    });

    this.router.navigate(['/login'], {
      replaceUrl: true,
    });
  }

  onHide(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
}
