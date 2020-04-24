package com.yury.demo.book;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An ArrayList of BuyOrder that orders the orders according to their priority defined in BuyOrder class
 * @param <BuyOrder>
 */
public class SortedBuyOrderArray<BuyOrder> extends ArrayList<BuyOrder> {
    @SuppressWarnings("unchecked")
    public void insertBuyOrder(BuyOrder order) {
        add(order);
        Comparable<BuyOrder> cmp = (Comparable<BuyOrder>) order;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
            Collections.swap(this, i, i-1);
    }
}