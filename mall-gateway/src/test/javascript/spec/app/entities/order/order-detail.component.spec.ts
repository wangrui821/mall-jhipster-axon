import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { MallGatewayTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { OrderDetailComponent } from '../../../../../../main/webapp/app/entities/order/order-detail.component';
import { OrderService } from '../../../../../../main/webapp/app/entities/order/order.service';
import { Order } from '../../../../../../main/webapp/app/entities/order/order.model';

describe('Component Tests', () => {

    describe('Order Management Detail Component', () => {
        let comp: OrderDetailComponent;
        let fixture: ComponentFixture<OrderDetailComponent>;
        let service: OrderService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [MallGatewayTestModule],
                declarations: [OrderDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    OrderService,
                    EventManager
                ]
            }).overrideComponent(OrderDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(OrderDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OrderService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Order(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.order).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
