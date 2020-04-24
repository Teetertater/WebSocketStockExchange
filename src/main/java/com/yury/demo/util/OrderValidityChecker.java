package com.yury.demo.util;

import com.yury.demo.book.AcknowledgementMessage;
import com.yury.demo.book.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderValidityChecker {

    public OrderValidityChecker() { }
    /**
     * This is where valid orders are defined. An incoming order is checked for validity
     * @param order: order to be checked
     * @return AcknowledgementMessage (Whether the order is going to be accepted, and if not, the error message)
     */
    public AcknowledgementMessage validOrder (Order order) {

        if (order.getOrderQty() == null
                || order.getPrice() == null
                || order.getOrdType() == null
                || order.getSide() == null
                || order.getClOrdID() == null
                || order.getChecksum() == null
                || order.getTimestamp() == null) {
            return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR,
                    "Fields must not be null");
        }

        //Only one order type for now, extendible
        switch (order.getOrdType()) {
            case LIMIT:
                return validLimitOrder(order);
            default:
                return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR,
                        "Invalid Order Type");
        }
    }

    /**
     * Checks validity of limit orders
     * @param order: order to be checked
     * @return AcknowledgementMessage
     */
    private AcknowledgementMessage validLimitOrder (Order order) {
        if (!order.getChecksum().equals(order.recalculateChecksum())) {
            return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR,
                    "Checksum Doesn't Match");
        }

        if (!(order.getPrice() > 0)) {
            return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR,
                    "Price must be positive");
        }
        if (!(order.getOrderQty() > 0)) {
            return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR,
                    "Order Quantity must be an integer > 0");
        }
        return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ORDER_ACCEPTED,
                "Order has been placed.");
    }
}
