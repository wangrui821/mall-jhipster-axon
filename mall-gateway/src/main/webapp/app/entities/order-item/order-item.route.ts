import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { OrderItemComponent } from './order-item.component';
import { OrderItemDetailComponent } from './order-item-detail.component';
import { OrderItemPopupComponent } from './order-item-dialog.component';
import { OrderItemDeletePopupComponent } from './order-item-delete-dialog.component';

import { Principal } from '../../shared';

@Injectable()
export class OrderItemResolvePagingParams implements Resolve<any> {

  constructor(private paginationUtil: PaginationUtil) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
      let page = route.queryParams['page'] ? route.queryParams['page'] : '1';
      let sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
      return {
          page: this.paginationUtil.parsePage(page),
          predicate: this.paginationUtil.parsePredicate(sort),
          ascending: this.paginationUtil.parseAscending(sort)
    };
  }
}

export const orderItemRoute: Routes = [
  {
    path: 'order-item',
    component: OrderItemComponent,
    resolve: {
      'pagingParams': OrderItemResolvePagingParams
    },
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.orderItem.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'order-item/:id',
    component: OrderItemDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.orderItem.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const orderItemPopupRoute: Routes = [
  {
    path: 'order-item-new',
    component: OrderItemPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.orderItem.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'order-item/:id/edit',
    component: OrderItemPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.orderItem.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'order-item/:id/delete',
    component: OrderItemDeletePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.orderItem.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
