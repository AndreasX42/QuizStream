import { Component, inject, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Router, RouterLink } from '@angular/router';
import { ErrorManagerFactory } from '../../shared/error.manager.factory';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIcon,
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private router = inject(Router);

  loggedIn = signal(false);
  hide = signal(true);
  emailErrorMessage = signal<string | undefined>(undefined);
  pwdErrorMessage = signal<string | undefined>(undefined);

  form = new FormGroup({
    email: new FormControl('', {
      validators: [Validators.email, Validators.required],
    }),
    password: new FormControl('', {
      validators: [Validators.required, Validators.minLength(6)],
    }),
  });

  onSubmit() {
    if (this.form.invalid) {
      this.updateEmailErrorMessage();
      this.updatePwdErrorMessage();
      return;
    }

    const email = this.form.value.email;
    const password = this.form.value.password;

    this.loggedIn.set(true);

    console.log(email, password);

    this.router.navigate(['/'], {
      replaceUrl: true,
    });
  }

  onHide(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  updateEmailErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.get('email')!,
    this.emailErrorMessage.set,
    {
      required: 'Must not be blank',
      email: 'Must be valid email',
    }
  );
  updatePwdErrorMessage = ErrorManagerFactory.getFormErrorHandler(
    this.form.get('password')!,
    this.pwdErrorMessage.set,
    {
      required: 'Must not be blank',
      minlength: 'Must be at least 6 characters',
    }
  );
}
