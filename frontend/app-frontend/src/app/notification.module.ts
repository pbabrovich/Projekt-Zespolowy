import {NotifierModule, NotifierOptions} from 'angular-notifier';
import {NgModule} from '@angular/core';


const notifierDefaultOptions: NotifierOptions = {
  position: {
    horizontal: {
      position: 'right',
      distance: 80,
    },
    vertical: {
      position: 'top',
      distance: 90,
      gap: 10,
    },
  },
  theme: 'material',
  behaviour: {
    autoHide: 3000,
    onClick: false,
    onMouseover: 'pauseAutoHide',
    showDismissButton: true,
    stacking: 2,
  },
  animations: {
    enabled: true,
    show: {
      preset: 'slide',
      speed: 300,
      easing: 'ease',
    },
    hide: {
      preset: 'fade',
      speed: 300,
      easing: 'ease',
      offset: 50,
    },
    shift: {
      speed: 300,
      easing: 'ease',
    },
    overlap: 150,
  },
};

@NgModule({
  imports: [NotifierModule.withConfig(notifierDefaultOptions)],
  exports: [NotifierModule]
})
export class NotificationModule {}
