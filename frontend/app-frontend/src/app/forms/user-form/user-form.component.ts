import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {User} from '../../models/user/user';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user-service/user-service.service';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/authentication-service/authentication.service';
import {HttpErrorResponse} from '@angular/common/http';
import {NotificationsEnum} from '../../enum/notifications.enum';
import {NotificationsService} from '../../services/notifications-service/notifications.service';


@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit, OnDestroy {
  private user: User;
  private subscriptions: Subscription[] = [];
  constructor(private userService: UserService, private router: Router, private authenticationService: AuthenticationService,
              private notificationsService: NotificationsService) {
    this.user = new User();
  }

  userForms = new FormGroup({

    userName: new FormControl('', [
      Validators.required,
      Validators.pattern('[a-zA-Z ]*')]),

    userSurname: new FormControl('', [
      Validators.required,
      Validators.pattern('[a-zA-Z ]*')]),

    userEmail: new FormControl('', [
      Validators.required,
      Validators.pattern('[a-z0-9._%+-]+@comarch+\.(com|pl)$')]),

    userPassword: new FormControl('', [
      Validators.required]),

    confirmPassword: new FormControl('', [
      Validators.required,
    ]),
  });

  ngOnInit(): void {
    if (this.authenticationService.isLogged()) {
      this.router.navigateByUrl('');
    } else {
      this.router.navigateByUrl('/register');
    }
  }

  // tslint:disable-next-line:typedef
  checkPassword(){
    const password = this.userForms.get('userPassword').value;
    const confirmPassword = this.userForms.get('confirmPassword').value;
    return password === confirmPassword ? false : { notSame: true };
  }

  onRegister(): void {
    this.user.firstName = this.userForms.get('userName').value;
    this.user.lastName = this.userForms.get('userSurname').value;
    this.user.email = this.userForms.get('userEmail').value;
    this.user.password = this.userForms.get('userPassword').value;
    this.subscriptions.push(
      this.authenticationService.register(this.user).subscribe(
        (response: User) => {
          this.router.navigateByUrl('/');
          this.notificationsService.showMessage(NotificationsEnum.SUCCESS, "Konto zostało utworzone");
        },
        (errorResponse: HttpErrorResponse) => {
          this.notificationsService.showMessage(NotificationsEnum.ERROR, errorResponse.error.message);
        }
      )
    );

  }
  private sendErrorNotification(notificationType: NotificationsEnum, message: string): void {
    if (message) {
      this.notificationsService.showMessage(notificationType, message);
    } else {
      this.notificationsService.showMessage(notificationType, 'Bład. Sprobuj jeszcze raz.');
    }
  }

  // tslint:disable-next-line:typedef
  gotoHome() {
    return this.router.navigate(['/home']);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  get userName(): AbstractControl {return this.userForms.get('userName'); }

  get userSurname(): AbstractControl {return this.userForms.get('userSurname'); }

  get userEmail(): AbstractControl {return this.userForms.get('userEmail'); }

  get userPassword(): AbstractControl {return this.userForms.get('userPassword'); }

  get confirmPassword(): AbstractControl {return  this.userForms.get('confirmPassword'); }

}
