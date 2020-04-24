package com.yury.demo.book;

import java.util.Comparator;

/** Information about a sell order
 *
 * The only difference between Order, BuyOrder and SellOrder are the comparator methods,
 * due to the way the sorted array needs to be able to compare orders
 **/
public class SellOrder implements Comparator<SellOrder>, Comparable<SellOrder> {
    private String timestamp;
    private Order.OrdType ordType;        //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)

    //For casting an Order to SellOrder (needed for sorting comparator)
    public SellOrder(Order O) {
        this.timestamp = O.getTimestamp();
        this.ordType = O.getOrdType();
        this.side = O.getSide();
        this.symbol = O.getSymbol();
        this.orderQty = O.getOrderQty();
        this.price = O.getPrice();
        this.clOrdID = O.getClOrdID();
    }

    public Order.OrdType getOrdType() { return ordType; }
    public Boolean getSide() { return side; }
    public String getSymbol() { return symbol; }
    public Integer getOrderQty() {return orderQty; }
    public Double getPrice() { return price; }
    public String getClOrdID() { return clOrdID; }
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
    }

    @Override //Smallest first (Price, then timestamp)
    public int compare(SellOrder order, SellOrder order1){
        if (!order1.getPrice().equals(order.getPrice())) {
            return order.getPrice().compareTo(order1.getPrice());
        } else {
            return order.getTimestamp().compareTo(order1.getTimestamp());
        }
    }
}