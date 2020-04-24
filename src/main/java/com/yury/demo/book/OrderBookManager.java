package com.yury.demo.book;

import com.yury.demo.util.TimeStampGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * The main service responsible for maintaining the Order Book. Incoming buy/sell orders are checked against
 * existing orders to see if a transaction is possible. If transactions are possible, they are returned and all
 * orders are updated to reflect new Quantity/Prices
 */
@Service
public class OrderBookManager {

    @Autowired
    private TimeStampGenerator tsGenerator;
    private SortedSellOrderArray<SellOrder> sellOrders = new SortedSellOrderArray<>();
    private SortedBuyOrderArray<BuyOrder> buyOrders = new SortedBuyOrderArray<>();

    public SortedSellOrderArray<SellOrder> getSellOrders() { return sellOrders; }
    public SortedBuyOrderArray<BuyOrder> getBuyOrders() { return buyOrders; }

    public String generateTimeStamp(){
        return tsGenerator.getCurrentTimeStamp();
    }

    /**
     * Checks order book against matches, updates order book, and returns transactions
     * @param order: the buy order to be processed
     * @return a list of transactions to be made
     */
    private ArrayList<Transaction> processLimitBuyOrder(BuyOrder order){

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
                        true,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
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
                currentMatchingSellOrder.setOrderQty(sellQty-buyQty);
                sellOrders.set(sellOrders.indexOf(currentMatchingSellOrder), currentMatchingSellOrder);
                currentTransactions.add(new Transaction(
                        true,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
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
                sellOrdersToRemove.add(currentMatchingSellOrder);
                currentTransactions.add(new Transaction(
                        true,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.PARTIAL_FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        false,
                        order.getClOrdID(),
                        currentMatchingSellOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                order.setOrderQty(order.getOrderQty() - currentMatchingSellOrder.getOrderQty());
                //continue filling order
            }
        }
        buyOrders.insertBuyOrder(order);
        sellOrders.removeAll(sellOrdersToRemove);
        return currentTransactions;
    }

    /**
     * Checks order book against matches, updates order book, and returns transactions
     * @param order: sell order to process
     * @return transactions to be made
     */
    private ArrayList<Transaction> processLimitSellOrder(SellOrder order){

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
                        false,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
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
                        false,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        sellQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
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
                        false,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.PARTIAL_FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                currentTransactions.add(new Transaction(
                        true,
                        order.getClOrdID(),
                        currentMatchingBuyOrder.getClOrdID(),
                        Transaction.StatusCode.FILL,
                        buyQty,
                        generateTimeStamp()
                ));
                order.setOrderQty(order.getOrderQty() - currentMatchingBuyOrder.getOrderQty());
                //continue filling order
            }
        }
        sellOrders.insertSellOrder(order);
        buyOrders.removeAll(buyOrdersToRemove);
        return currentTransactions;
    }

    public ArrayList<Transaction> processOrder(Order order){
        if (order.getSide()) { //process buy order
            BuyOrder buyOrder = new BuyOrder(order);
            if (sellOrders.isEmpty()){
                buyOrders.insertBuyOrder(buyOrder);
            } else {
                return processLimitBuyOrder(buyOrder);
            }
        } else { //process sell order
            SellOrder sellOrder = new SellOrder(order);
            if (buyOrders.isEmpty()){
                sellOrders.insertSellOrder(sellOrder);
            } else {
                return processLimitSellOrder(sellOrder);
            }
        }
        //return empty list of transactions if did not process anything
        return new ArrayList<>();
    }
}
