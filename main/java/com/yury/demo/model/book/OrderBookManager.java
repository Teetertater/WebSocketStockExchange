package com.yury.demo.model.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.model.SortedOrderArray;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;

public class OrderBookManager {

    ObjectMapper mapper = new ObjectMapper(); //TODO remove

    private static OrderBookManager orderBookManagerInstance = null;
    private SortedOrderArray<Order> sellOrders = new SortedOrderArray<Order>();
    private SortedOrderArray<Order> buyOrders = new SortedOrderArray<Order>();

    private OrderBookManager() { }

    public static OrderBookManager getInstance()
    {
        if (orderBookManagerInstance == null)
            orderBookManagerInstance = new OrderBookManager();
        return orderBookManagerInstance;
    }

    public Transaction[] processOrder(Order order){

        ArrayList<Transaction> currentOrders = new ArrayList<>();



        if (order.getSide()) { //process buy order
            if (sellOrders.isEmpty()){
                buyOrders.insertDescending(order);
            }

            for (int i = sellOrders.size()-1; i > 0; i--) {
                Order currentSellOrder = sellOrders.get(i);
                if (currentSellOrder.getPrice() > order.getPrice()) break; //Stop when sell price exceeds buy price

                if (currentSellOrder.getOrderQty() >= order.getOrderQty()) { //case where the entire order can be filled

                }

                if (currentSellOrder.getOrderQty() < order.getOrderQty()) { //case where the entire order can be filled

                }


            }


        } else if (!order.getSide()) { //process sell order
            if (buyOrders.isEmpty()){
                sellOrders.insertAscending(order);
            }

        }

        System.out.println(sellOrders.toString());
        System.out.println(buyOrders.toString());

        return null;
    }



}
