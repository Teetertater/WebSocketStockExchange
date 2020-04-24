package com.yury.demo.book;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An ArrayList of SellOrder that orders the orders according to their priority defined in SellOrder class
 * @param <SellOrder>
 */
public class SortedSellOrderArray<SellOrder> extends ArrayList<SellOrder> {
    @SuppressWarnings("unchecked")
    public void insertSellOrder(SellOrder order) {
        add(order);
        Comparable<SellOrder> cmp = (Comparable<SellOrder>) order;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
            Collections.swap(this, i, i-1);
    }
}