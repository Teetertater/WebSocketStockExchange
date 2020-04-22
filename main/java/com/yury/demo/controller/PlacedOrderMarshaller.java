package com.yury.demo.controller;

import com.yury.demo.model.messages.AcknowledgementMessage;
import com.yury.demo.model.messages.NewOrderSingleMessage;
import org.springframework.beans.factory.annotation.Autowired;

//TODO refactor (combine with producerService)
public class PlacedOrderMarshaller {

    public PlacedOrderMarshaller() {};

    public AcknowledgementMessage marshallOrder(NewOrderSingleMessage orderMessage) {
        /**
        orderMessage.getOrdType(),
                orderMessage.getSide(),
                orderMessage.getSymbol(),
                orderMessage.getOrderQty(),
                orderMessage.getPrice(),
                orderMessage.getClOrdID(),
                orderMessage.getChecksum()
        **/

        boolean hasErrors = false;
        if (hasErrors) {
            return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR, "Please review order");
        }

        return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ORDER_ACCEPTED, "Order has been placed");
    }
}
