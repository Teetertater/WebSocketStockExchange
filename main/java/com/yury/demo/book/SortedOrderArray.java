package com.yury.demo.book;

import com.yury.demo.book.BuyOrder;
import com.yury.demo.book.Order;

import java.util.ArrayList;
import java.util.Collections;

class SortedBuyOrderArray<BuyOrder> extends ArrayList<BuyOrder> {
    @SuppressWarnings("unchecked")
    public void insertBuyOrder(BuyOrder order) {
        add(order);
        Comparable<BuyOrder> cmp = (Comparable<BuyOrder>) order;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
            Collections.swap(this, i, i-1);
    }
}

class SortedSellOrderArray<SellOrder> extends ArrayList<SellOrder> {
    @SuppressWarnings("unchecked")
    public void insertSellOrder(SellOrder order) {
        add(order);
        Comparable<SellOrder> cmp = (Comparable<SellOrder>) order;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
            Collections.swap(this, i, i-1);
    }
}