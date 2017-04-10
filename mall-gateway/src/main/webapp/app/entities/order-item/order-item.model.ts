export class OrderItem {
    constructor(
        public id?: number,
        public productId?: number,
        public productCode?: string,
        public productName?: string,
        public price?: number,
        public quantity?: number,
        public orderId?: number,
    ) {
    }
}
