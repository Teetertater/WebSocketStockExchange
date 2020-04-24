package com.yury.demo.book;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/** Message template for Orders
 * Follows FIX 5.0 template
 * https://www.onixs.biz/fix-dictionary/5.0/msgType_D_68.html
 *
 **/
public class Order {
    public enum OrdType implements Serializable {
        LIMIT;

        public String getOrdType() {
            return this.name();
        }
    }

    @NotNull
    private String timestamp;
    @NotNull
    private OrdType ordType = OrdType.LIMIT; // (FIX <40>)
    @NotNull
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    @NotNull
    private String symbol;          //FIX <55>
    @NotNull
    private Integer orderQty;       //Order quantity (FIX <38>
    @NotNull
    private Double price;           //price for limit orders (FIX <44>)
    @NotNull
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)
    @NotNull
    private Double checksum;        //This is generated from client and checked on server for validity

    public Order(){ }

    public Order(String timestamp, OrdType ordType, Boolean side, String symbol,
                                 Integer orderQty, Double price, String clOrdID, Double checksum) {
        this.timestamp = timestamp;
        this.ordType = ordType;
        this.side = side;
        this.symbol = symbol;
        this.orderQty = orderQty;
        this.price = price;
        this.clOrdID = clOrdID;
        this.checksum = checksum;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public OrdType getOrdType() { return ordType; }
    public Boolean getSide() {
        return side;
    }
    public String getSymbol() {
        return symbol;
    }
    public Integer getOrderQty() {return orderQty; }
    public Double getPrice() { return price; }
    public String getClOrdID() {
        return clOrdID;
    }
    public Double getChecksum() { return checksum; }
    public Double recalculateChecksum () {
        return (10 - (this.orderQty + this.price) % 10);
    }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

