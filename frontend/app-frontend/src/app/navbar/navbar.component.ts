import { Component, OnInit } from '@angular/core';
import { NavigationStart, Router, RouterEvent } from '@angular/router';
import { AuthenticationService } from '../services/authentication-service/authentication.service';
import {NotificationsEnum} from '../enum/notifications.enum';
import {NotificationsService} from '../services/notifications-service/notifications.service';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {ModalService} from "../modals/_modal";

@Component({
  selector: 'navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  oldPassword: string;
  newPassword: string;
  newPasswordCopy: string;

  constructor(private authenticationService : AuthenticationService,
    private router : Router, private notificationsService: NotificationsService,
              private modalService: ModalService) {
  }

  ngOnInit(): void {
  }

  public classes = {
    specialItem : true,
    item : true
  };



  public logout(): void{
    this.authenticationService.logOut().subscribe(
      res => {
        console.log('Logging out');
      },
      err => {
        console.log(err);
      },
      () => {
        this.gotoHome();
      });


  }

  public isAdmin(){
      let role = this.authenticationService.getUser().role;
      return role == "ROLE_ADMIN";
  }

  public isLoggedIn(){
   return this.authenticationService.isLogged();
  }

  public getName(){
    return this.authenticationService.getUser().firstName;
  }

  gotoHome(): void {
    this.authenticationService.clearCache();
    console.log("Going to home");
    this.router.navigate(['/home']);
    this.notificationsService.showMessage(NotificationsEnum.DEFAULT, "Logged out");
  }

  changePassword(id: string): void{
    console.log('oldPassword: ' + this.oldPassword);
    console.log('real oldPassword: ' + this.authenticationService.getUser().password);
    console.log('newPassword: ' + this.newPassword);
    console.log('newPasswordCopy: ' + this.newPasswordCopy);
    /*if (this.oldPassword === this.authenticationService.getUser().password && this.newPassword === this.newPasswordCopy){
      this.authenticationService.changePassword(this.authenticationService.getUser(), this.newPassword);
      this.closeModal(id);
      this.notificationsService.showMessage(NotificationsEnum.DEFAULT, "Zmieniono hasło");
    }
    else{
      this.notificationsService.showMessage(NotificationsEnum.DEFAULT, "Prowadzono złe stare hasło, lub hasła się nie zgadzają");
    }

     */
  }

  closeModal(id: string) {
    this.modalService.close(id);
  }
  openModal(id: string) {
    this.modalService.open(id);
  }
}
