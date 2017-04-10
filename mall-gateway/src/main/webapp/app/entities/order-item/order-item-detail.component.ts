import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager , JhiLanguageService  } from 'ng-jhipster';

import { OrderItem } from './order-item.model';
import { OrderItemService } from './order-item.service';

@Component({
    selector: 'jhi-order-item-detail',
    templateUrl: './order-item-detail.component.html'
})
export class OrderItemDetailComponent implements OnInit, OnDestroy {

    orderItem: OrderItem;
    private subscription: any;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: EventManager,
        private jhiLanguageService: JhiLanguageService,
        private orderItemService: OrderItemService,
        private route: ActivatedRoute
    ) {
        this.jhiLanguageService.setLocations(['orderItem']);
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe(params => {
            this.load(params['id']);
        });
        this.registerChangeInOrderItems();
    }

    load (id) {
        this.orderItemService.find(id).subscribe(orderItem => {
            this.orderItem = orderItem;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInOrderItems() {
        this.eventSubscriber = this.eventManager.subscribe('orderItemListModification', response => this.load(this.orderItem.id));
    }

}
