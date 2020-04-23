package com.yury.demo.book;

import com.yury.demo.util.TimeStampGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderBookManager {

    @Autowired
    private TimeStampGenerator tsGenerator;
    private SortedSellOrderArray<SellOrder> sellOrders = new SortedSellOrderArray<>();
    private SortedBuyOrderArray<BuyOrder> buyOrders = new SortedBuyOrderArray<>();

    public OrderBookManager(TimeStampGenerator tsGenerator) {
        this.tsGenerator = tsGenerator;
    }

    public SortedSellOrderArray<SellOrder> getSellOrders() { return sellOrders; }
    public SortedBuyOrderArray<BuyOrder> getBuyOrders() { return buyOrders; }

    public String generateTimeStamp(){
        return tsGenerator.getCurrentTimeStamp();
    }

    private ArrayList<Transaction> processBuyOrder(BuyOrder order){

        ArrayList<Transaction> currentTransactions = new ArrayList<>();
        ArrayList<SellOrder> sellOrdersToRemove = new ArrayList<>();

        //Iterate through ordered list of sell orders looking for potential match
        for (SellOrder currentMatchingSellOrder : sellOrders) {
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
                sellOrders.removeAll(sellOrdersToRemove);
                return currentTransactions; //stop processing
            }

            //case where the entire order can be filled with leftover shares
            else if (sellQty > buyQty) {
                System.out.println("qty > equal");
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
                sellOrders.removeAll(sellOrdersToRemove);
                return currentTransactions; //stop processing
            }

            //order needs multiple transactions to be filled
            else {
                System.out.println("qty <");
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

    private ArrayList<Transaction> processSellOrder(SellOrder order){

        ArrayList<Transaction> currentTransactions = new ArrayList<>();
        ArrayList<BuyOrder> buyOrdersToRemove = new ArrayList<>();

        //Iterate through ordered list of buy orders looking for potential match
        for (BuyOrder currentMatchingBuyOrder : buyOrders) {
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
                buyOrders.removeAll(buyOrdersToRemove);
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
                buyOrders.removeAll(buyOrdersToRemove);
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
            BuyOrder buyOrder = new BuyOrder(order);
            if (sellOrders.isEmpty()){
                buyOrders.insertBuyOrder(buyOrder);
            } else {
                return processBuyOrder(buyOrder);
            }

        } else { //process sell order
            SellOrder sellOrder = new SellOrder(order);
            if (buyOrders.isEmpty()){
                sellOrders.insertSellOrder(sellOrder);
            } else {
                return processSellOrder(sellOrder);
            }
        }
        return new ArrayList<>(); //return no transactions if did not process anything
    }
}
