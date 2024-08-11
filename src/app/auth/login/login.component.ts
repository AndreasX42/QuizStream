import { Component, inject, signal } from '@angular/core';
import { RegisterComponent } from '../register/register.component';
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
import { DialogComponent } from '../../shared/dialog/dialog.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RegisterComponent,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIcon,
    ReactiveFormsModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private dialog = inject(MatDialog);

  register = signal(false);

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
      return;
    }

    const email = this.form.value.email;
    const password = this.form.value.password;

    const dialogRef = this.dialog.open(DialogComponent, {
      data: { message: 'Logged in successfully!' },
    });

    dialogRef.afterClosed().subscribe(() => {
      location.reload();
    });
  }

  onRegister() {
    this.register.set(true);
  }
}
