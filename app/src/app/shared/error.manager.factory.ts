import { AbstractControl } from '@angular/forms';

export class ErrorManagerFactory {
  static getFormErrorHandler(
    control: AbstractControl,
    signalSetter: (message: string) => void,
    errorMessagesMap: { [key: string]: string }
  ) {
    return () => {
      for (let errorName in errorMessagesMap) {
        if (control.hasError(errorName)) {
          signalSetter(errorMessagesMap[errorName]);
          return;
        }
      }
      signalSetter('');
    };
  }
}
