package com.yury.demo.book;

import java.io.Serializable;

/**
 * Contains information about a transaction
 */
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

    public Transaction() {}

    public Transaction(boolean side, String buyClOrdID, String sellClOrdID,
                       StatusCode statusCode, int cumQty, String updateTimeStamp) {
        this.side = side;
        this.buyClOrdID = buyClOrdID;
        this.sellClOrdID = sellClOrdID;
        this.statusCode = statusCode;
        this.cumQty = cumQty;
        this.updateTimeStamp = updateTimeStamp;
        this.orderID = generateNewID(side, buyClOrdID, sellClOrdID, cumQty);
    }

    //Generates a deterministic ID that can be recreated but will be unique for every transaction
    //(Assumes transaction IDs will be unique)
    private String generateNewID(Boolean side, String buyClOrdID, String sellClOrdID, int cumQty) {
        return side.toString() + "_" + buyClOrdID + "_" + sellClOrdID + "_" + cumQty;
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
