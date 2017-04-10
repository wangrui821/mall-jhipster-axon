import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { OrderComponent } from './order.component';
import { OrderDetailComponent } from './order-detail.component';
import { OrderPopupComponent } from './order-dialog.component';
import { OrderDeletePopupComponent } from './order-delete-dialog.component';

import { Principal } from '../../shared';

@Injectable()
export class OrderResolvePagingParams implements Resolve<any> {

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

export const orderRoute: Routes = [
  {
    path: 'order',
    component: OrderComponent,
    resolve: {
      'pagingParams': OrderResolvePagingParams
    },
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.order.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'order/:id',
    component: OrderDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.order.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const orderPopupRoute: Routes = [
  {
    path: 'order-new',
    component: OrderPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.order.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'order/:id/edit',
    component: OrderPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.order.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'order/:id/delete',
    component: OrderDeletePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'mallGatewayApp.order.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
