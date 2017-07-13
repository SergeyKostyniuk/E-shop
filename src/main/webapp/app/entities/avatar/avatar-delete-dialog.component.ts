import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';

import { Avatar } from './avatar.model';
import { AvatarPopupService } from './avatar-popup.service';
import { AvatarService } from './avatar.service';

@Component({
    selector: 'jhi-avatar-delete-dialog',
    templateUrl: './avatar-delete-dialog.component.html'
})
export class AvatarDeleteDialogComponent {

    avatar: Avatar;

    constructor(
        private avatarService: AvatarService,
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.avatarService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'avatarListModification',
                content: 'Deleted an avatar'
            });
            this.activeModal.dismiss(true);
        });
        this.alertService.success(`A Avatar is deleted with identifier ${id}`, null, null);
    }
}

@Component({
    selector: 'jhi-avatar-delete-popup',
    template: ''
})
export class AvatarDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private avatarPopupService: AvatarPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.avatarPopupService
                .open(AvatarDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
