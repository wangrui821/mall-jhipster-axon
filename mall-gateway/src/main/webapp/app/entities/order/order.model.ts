
const enum OrderState {
    'PENDING_PAY',
    'PAID',
    'DELIVERED',
    'CANCELED',
    'FINISHED'

};
export class Order {
    constructor(
        public id?: number,
        public code?: string,
        public totalAmount?: number,
        public timeCreated?: any,
        public state?: OrderState,
        public orderItemId?: number,
    ) {
    }
}
