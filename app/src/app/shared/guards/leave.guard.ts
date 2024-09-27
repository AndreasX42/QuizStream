import { CanDeactivateFn } from '@angular/router';

export const canLeaveEditPage: CanDeactivateFn<any> = (component) => {
  if (component.form.touched && component.form.invalid) {
    return window.confirm('Do you really want to leave?');
  }
  return true;
};

export const canLeaveSolvePage: CanDeactivateFn<any> = (component) => {
  return window.confirm(
    'Do you really want to leave? Any progress will not be saved.'
  );
};
