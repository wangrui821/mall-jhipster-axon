import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { MallGatewayTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { OrderItemDetailComponent } from '../../../../../../main/webapp/app/entities/order-item/order-item-detail.component';
import { OrderItemService } from '../../../../../../main/webapp/app/entities/order-item/order-item.service';
import { OrderItem } from '../../../../../../main/webapp/app/entities/order-item/order-item.model';

describe('Component Tests', () => {

    describe('OrderItem Management Detail Component', () => {
        let comp: OrderItemDetailComponent;
        let fixture: ComponentFixture<OrderItemDetailComponent>;
        let service: OrderItemService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [MallGatewayTestModule],
                declarations: [OrderItemDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    OrderItemService,
                    EventManager
                ]
            }).overrideComponent(OrderItemDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(OrderItemDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OrderItemService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new OrderItem(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.orderItem).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
