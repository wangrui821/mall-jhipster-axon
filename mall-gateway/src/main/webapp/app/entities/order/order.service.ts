import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, BaseRequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Order } from './order.model';
import { DateUtils } from 'ng-jhipster';
@Injectable()
export class OrderService {

    private resourceUrl = 'mallserviceorder/api/orders';

    constructor(private http: Http, private dateUtils: DateUtils) { }

    create(order: Order): Observable<Order> {
        let copy: Order = Object.assign({}, order);
        copy.timeCreated = this.dateUtils.toDate(order.timeCreated);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(order: Order): Observable<Order> {
        let copy: Order = Object.assign({}, order);

        copy.timeCreated = this.dateUtils.toDate(order.timeCreated);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Order> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            let jsonResponse = res.json();
            jsonResponse.timeCreated = this.dateUtils
                .convertDateTimeFromServer(jsonResponse.timeCreated);
            return jsonResponse;
        });
    }

    query(req?: any): Observable<Response> {
        let options = this.createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: any) => this.convertResponse(res))
        ;
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }


    private convertResponse(res: any): any {
        let jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            jsonResponse[i].timeCreated = this.dateUtils
                .convertDateTimeFromServer(jsonResponse[i].timeCreated);
        }
        res._body = jsonResponse;
        return res;
    }

    private createRequestOption(req?: any): BaseRequestOptions {
        let options: BaseRequestOptions = new BaseRequestOptions();
        if (req) {
            let params: URLSearchParams = new URLSearchParams();
            params.set('page', req.page);
            params.set('size', req.size);
            if (req.sort) {
                params.paramsMap.set('sort', req.sort);
            }
            params.set('query', req.query);

            options.search = params;
        }
        return options;
    }
}
