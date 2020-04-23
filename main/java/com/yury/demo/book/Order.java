package com.yury.demo.book;

import java.util.Comparator;

// Message template for Orders
// Follows FIX 5.0 template
// https://www.onixs.biz/fix-dictionary/5.0/msgType_D_68.html
public class Order {
    private String timestamp;
    private Boolean ordType = true; //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)
    private Double checksum;

    public Order(){ };

    public Order(String timestamp, Boolean ordType, Boolean side, String symbol,
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

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Boolean getOrdType() {
        return ordType;
    }
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

    public void setPrice(Double price) { this.price = price; }
    public void setOrderQty(Integer orderQty) { this.orderQty = orderQty; }

    @Override
    public String toString(){
        String timestampString = ((this.timestamp == null) ? "null" : this.timestamp.toString());
        String priceString = ((this.price == null) ? "null" : this.price.toString());

        return priceString + " " + timestampString;

    }

}

/**The only difference between Order, BuyOrder and SellOrder are the comparator methods,
 * due to the way the sorted array needs to be able to compare orders. BuyOrder and SellOrder also
 * cannot be subclasses of Order because of casting issues resulting in null values.
**/
class BuyOrder implements Comparator<BuyOrder>, Comparable<BuyOrder> {
    private String timestamp;
    private Boolean ordType;        //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)
    private Double checksum;

    //For casting an Order to BuyOrder (needed for sorting comparator)
    public BuyOrder(Order O) {
        this.timestamp = O.getTimestamp();
        this.ordType = O.getOrdType();
        this.side = O.getSide();
        this.symbol = O.getSymbol();
        this.orderQty = O.getOrderQty();
        this.price = O.getPrice();
        this.clOrdID = O.getClOrdID();
        this.checksum = O.getChecksum();
    }

    public Boolean getOrdType() { return ordType; }
    public Boolean getSide() { return side; }
    public String getSymbol() { return symbol; }
    public Integer getOrderQty() {return orderQty; }
    public Double getPrice() { return price; }
    public String getClOrdID() { return clOrdID; }
    public Double getChecksum() { return checksum; }
    public String getTimestamp() { return timestamp; }

    public void setOrderQty(Integer orderQty) { this.orderQty = orderQty; }

    @Override //Largest first (Price, then timestamp)
    public int compareTo(BuyOrder order){
        if (order == null || order.getPrice() == null){
            return 1;
        }
        if (!order.getPrice().equals(this.price)) {
            return -this.price.compareTo(order.getPrice());
        } else {
            return -this.timestamp.compareTo(order.getTimestamp());
        }
    };

    @Override //Largest first (Price, then timestamp)
    public int compare(BuyOrder order, BuyOrder order1){
        if (!order1.getPrice().equals(order.getPrice())) {
            return -order.getPrice().compareTo(order1.getPrice());
        } else {
            return -order.getTimestamp().compareTo(order1.getTimestamp());
        }
    };

    @Override
    public String toString(){
        String timestampString = ((this.timestamp == null) ? "null" : this.timestamp.toString());
        String priceString = ((this.price == null) ? "null" : this.price.toString());

        return priceString + " " + timestampString;

    } //TODO REMOVE
}


class SellOrder implements Comparator<SellOrder>, Comparable<SellOrder> {
    private String timestamp;
    private Boolean ordType;        //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)
    private Double checksum;

    //For casting an Order to SellOrder (needed for sorting comparator)
    public SellOrder(Order O) {
        this.timestamp = O.getTimestamp();
        this.ordType = O.getOrdType();
        this.side = O.getSide();
        this.symbol = O.getSymbol();
        this.orderQty = O.getOrderQty();
        this.price = O.getPrice();
        this.clOrdID = O.getClOrdID();
        this.checksum = O.getChecksum();
    }

    public Boolean getOrdType() { return ordType; }
    public Boolean getSide() { return side; }
    public String getSymbol() { return symbol; }
    public Integer getOrderQty() {return orderQty; }
    public Double getPrice() { return price; }
    public String getClOrdID() { return clOrdID; }
    public Double getChecksum() { return checksum; }
    public String getTimestamp() { return timestamp; }

    public void setOrderQty(Integer orderQty) { this.orderQty = orderQty; }

    @Override //Smallest first (Price, then timestamp)
    public int compareTo(SellOrder order){
        if (order == null || order.getPrice() == null){
            return -1;
        }
        if (!order.getPrice().equals(this.price)) {
            return this.price.compareTo(order.getPrice());
        } else {
            return this.timestamp.compareTo(order.getTimestamp());
        }
    };

    @Override //Smallest first (Price, then timestamp)
    public int compare(SellOrder order, SellOrder order1){
        if (!order1.getPrice().equals(order.getPrice())) {
            return order.getPrice().compareTo(order1.getPrice());
        } else {
            return order.getTimestamp().compareTo(order1.getTimestamp());
        }
    };

    @Override
    public String toString(){
        String timestampString = ((this.timestamp == null) ? "null" : this.timestamp.toString());
        String priceString = ((this.price == null) ? "null" : this.price.toString());

        return priceString + " " + timestampString;

    }
    // TODO REMOVE
}

