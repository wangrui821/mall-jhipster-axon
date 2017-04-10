import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { MallGatewayProductModule } from './product/product.module';
import { MallGatewayOrderModule } from './order/order.module';
import { MallGatewayOrderItemModule } from './order-item/order-item.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        MallGatewayProductModule,
        MallGatewayOrderModule,
        MallGatewayOrderItemModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class MallGatewayEntityModule {}
