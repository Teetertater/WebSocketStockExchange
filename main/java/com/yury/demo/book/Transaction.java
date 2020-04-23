package com.yury.demo.book;

import java.io.Serializable;

public class Transaction {
    public enum StatusCode implements Serializable {
        FILL,
        PARTIAL_FILL,
        DONE_FOR_DAY,
        CANCELED,
        EXPIRED;

        public String getStatus() {
            return this.name();
        }
    }

    private String orderID;
    private boolean side;
    private String buyClOrdID;
    private String sellClOrdID;
    private StatusCode statusCode;
    private int cumQty;
    private String updateTimeStamp;

    public Transaction() {};

    public Transaction(String orderID, boolean side, String buyClOrdID, String sellClOrdID,
                       StatusCode statusCode, int cumQty, String updateTimeStamp) {
        this.orderID = orderID;
        this.side = side;
        this.buyClOrdID = buyClOrdID;
        this.sellClOrdID = sellClOrdID;
        this.statusCode = statusCode;
        this.cumQty = cumQty;
        this.updateTimeStamp = updateTimeStamp;
    }

    public String getOrderID() {
        return orderID;
    }

    public boolean getSide() {
        return side;
    }

    public String getBuyClOrdID() {
        return buyClOrdID;
    }

    public String getSellClOrdID() {
        return sellClOrdID;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public int getCumQty() {
        return cumQty;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }
}
