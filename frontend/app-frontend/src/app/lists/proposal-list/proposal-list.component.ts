import { Component, OnInit } from '@angular/core';
import { Proposal } from '../../models/proposal/proposal';
import {ProposalService} from '../../services/proposal-service/proposal-service.service';
import {AuthenticationService} from '../../services/authentication-service/authentication.service';
import {PageEvent} from '@angular/material/paginator';
import { ModalService } from 'src/app/modals/_modal';
import {User} from "../../models/user/user";
import {CustomHttpRespone} from '../../models/custom-http-responce';
import {NotificationsEnum} from '../../enum/notifications.enum';
import {HttpErrorResponse} from '@angular/common/http';
import {NotificationsService} from '../../services/notifications-service/notifications.service';
import {Subscription} from 'rxjs';
import {Router} from "@angular/router";

@Component({
  selector: 'app-proposal-list',
  templateUrl: './proposal-list.component.html',
  styleUrls: ['./proposal-list.component.css']
})
export class ProposalListComponent implements OnInit {
  proposalMapping = new Map();
  proposalVoted = new Map();
  totalElements = 0;
  proposals: Proposal[] = [];
  loading: boolean;
  assignedUser = true;
  users: User[] = [];
  private subscriptions: Subscription[] = [];
  idToDelete: string;
  constructor(private proposalService: ProposalService,
              private authenticationService: AuthenticationService,
              private modalService: ModalService,
              private notifications: NotificationsService,
              private router: Router
              ) { }

  ngOnInit(): void {
    this.getProposals({page: '0', size: '10'});
    this.proposals;
  }

  onAssignClicked(id: string): void{
    this.proposalService.updateAssign( this.authenticationService.getUser().id , Number.parseFloat(id)).subscribe(data => {
      window.location.reload();
    });
  }

  onInterestedClicked(id: string): void{
    this.proposalService.updateVotes( this.authenticationService.getUser().id , Number.parseFloat(id)).subscribe(data => {
      window.location.reload();
    });
  }
  onShowAssignedUsers(id: string): void{
    this.proposalService.showAssignedUsers(Number.parseInt(id)).subscribe(data => {
      this.users = data;
    });
  }
  public isAdmin(){
    const role = this.authenticationService.getUser().role;
    return role == 'ROLE_ADMIN';
  }

  public isLoggedIn(): boolean{
    return this.authenticationService.isLogged();
  }
  private getProposals(request) {
    this.loading = true;
    this.proposalService.listProposals(request)
      .subscribe(data => {
        this.proposals = data['content'];
        this.totalElements = data['totalElements'];
        this.loading = false;
        const UserID = this.authenticationService.getUser().id;
        this.proposals.forEach(proposal => {
          this.proposalService.checkVote(proposal.id, UserID).subscribe(answer2 => {
            this.proposalVoted.set(proposal, answer2);
          });
          this.proposalService.checkAssign(proposal.id, UserID).subscribe( answer => {
            this.proposalMapping.set(proposal, answer);
          });
        });
        console.log(this.proposalVoted);
      }, error => {
        this.loading = false;
      });
  }
  nextPage(event: PageEvent) {
    const request = {};
    request['page'] = event.pageIndex.toString();
    request['size'] = event.pageSize.toString();
    this.getProposals(request);
  }

  closeModal(id: string) {
    this.modalService.close(id);
  }
  openModal(id: string) {
    this.modalService.open(id);
  }
  private sendNotification(notificationType: NotificationsEnum, message: string): void {
    if (message) {
      this.notifications.showMessage(notificationType, message);
    } else {
      this.notifications.showMessage(notificationType, 'An error occurred. Please try again.');
    }
  }

  delete(id: string){
    // this.trainingService.delete(id).subscribe();
    // window.location.reload();
    this.subscriptions.push(
      this.proposalService.delete(id).subscribe(
        (response: CustomHttpRespone) => {
          this.sendNotification(NotificationsEnum.SUCCESS, response.message);
          window.location.reload();
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationsEnum.ERROR, error.error.message);
        }
      )
    );
  }

  createTraining(id: string){
    this.router.navigateByUrl('createTrainingFromProposal/' + id);
  }
}
