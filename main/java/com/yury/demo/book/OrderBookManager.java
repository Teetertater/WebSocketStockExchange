package com.yury.demo.book;

import com.yury.demo.util.SortedOrderArray;
import com.yury.demo.util.TimeStampGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderBookManager {

    @Autowired
    private TimeStampGenerator tsGenerator;
    private SortedOrderArray<Order> sellOrders = new SortedOrderArray<Order>();
    private SortedOrderArray<Order> buyOrders = new SortedOrderArray<Order>();

    private OrderBookManager() { }
    public OrderBookManager(TimeStampGenerator tsGenerator) {
        this.tsGenerator = tsGenerator;
    }

    public String generateTimeStamp(){
        return tsGenerator.getCurrentTimeStamp();
    }

    private ArrayList<Transaction> processBuyOrder(Order order){

        ArrayList<Transaction> currentTransactions = new ArrayList<>();
        ArrayList<Order> sellOrdersToRemove = new ArrayList<>();

        //Iterate through ordered list of sell orders looking for potential match
        for (Order currentMatchingSellOrder : sellOrders) {
            System.out.println(currentMatchingSellOrder.toString());
            int sellQty = currentMatchingSellOrder.getOrderQty();
            int buyQty = order.getOrderQty();

            //Stop when sell price exceeds buy price
            if (currentMatchingSellOrder.getPrice() > order.getPrice()) break;

            //case where the entire order can be filled by one other order
            if (sellQty == buyQty) {
                sellOrdersToRemove.add(currentMatchingSellOrder);
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        true,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        false,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                return currentTransactions; //stop processing
            }

            //case where the entire order can be filled with leftover shares
            else if (sellQty > buyQty) {
                currentMatchingSellOrder.setOrderQty(sellQty-buyQty);
                sellOrders.set(sellOrders.indexOf(currentMatchingSellOrder), currentMatchingSellOrder);
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        true,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        false,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.PARTIAL_FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                return currentTransactions; //stop processing
            }

            //order needs multiple transactions to be filled
            else {
                sellOrdersToRemove.add(currentMatchingSellOrder);
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        true,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.PARTIAL_FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        false,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                //continue filling order
            }
        }
        sellOrders.removeAll(sellOrdersToRemove);
        return currentTransactions;
    }

    private ArrayList<Transaction> processSellOrder(Order order){

        ArrayList<Transaction> currentTransactions = new ArrayList<>();
        ArrayList<Order> buyOrdersToRemove = new ArrayList<>();

        //Iterate through ordered list of buy orders looking for potential match
        for (Order currentMatchingBuyOrder : buyOrders) {
            System.out.println(currentMatchingBuyOrder.toString());
            int buyQty = currentMatchingBuyOrder.getOrderQty();
            int sellQty = order.getOrderQty();

            //Stop when sell price exceeds buy price
            if (currentMatchingBuyOrder.getPrice() < order.getPrice()) break;

            //case where the entire order can be filled by one other order
            if (sellQty == buyQty) {
                buyOrdersToRemove.add(currentMatchingBuyOrder);
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        false,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        true,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                return currentTransactions; //stop processing
            }

            //case where the entire order can be filled with buy shares leftover
            else if (buyQty > sellQty) {
                currentMatchingBuyOrder.setOrderQty(buyQty-sellQty);
                buyOrders.set(buyOrders.indexOf(currentMatchingBuyOrder), currentMatchingBuyOrder);
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        false,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        true,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.PARTIAL_FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                return currentTransactions; //stop processing
            }

            //order needs multiple transactions to be filled
            else { //buyQty < sellQty
                buyOrdersToRemove.add(currentMatchingBuyOrder);
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        false,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.PARTIAL_FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        "ID", //TODO,
                        true,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                //continue filling order
            }
        }
        buyOrders.removeAll(buyOrdersToRemove);
        return currentTransactions;
    }

    public ArrayList<Transaction> processOrder(Order order){

        if (order.getSide()) { //process buy order
            if (sellOrders.isEmpty()){
                buyOrders.insertDescending(order);
            } else {
                System.out.println(sellOrders.toString());
                System.out.println(buyOrders.toString());
                return processBuyOrder(order);
            }

        } else { //process sell order
            if (buyOrders.isEmpty()){
                sellOrders.insertAscending(order);
            } else {
                System.out.println(sellOrders.toString());
                System.out.println(buyOrders.toString());
                return processSellOrder(order);
            }

        }

        System.out.println(sellOrders.toString());
        System.out.println(buyOrders.toString());
        return new ArrayList<>(); //return no transactions if did not process anything
    }



}
