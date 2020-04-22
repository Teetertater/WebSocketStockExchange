package com.yury.demo.model;

import java.util.ArrayList;
import java.util.Collections;

public class SortedOrderArray<Order> extends ArrayList<Order> {
    //https://stackoverflow.com/questions/4031572/sorted-array-list-in-java

    @SuppressWarnings("unchecked")
    public void insertDescending(Order order) {
        add(order);
        Comparable<Order> cmp = (Comparable<Order>) order;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
            Collections.swap(this, i, i-1);
    }

    @SuppressWarnings("unchecked")
    public void insertAscending(Order order) {
        add(order);
        Comparable<Order> cmp = (Comparable<Order>) order;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) >= 0; i--)
            Collections.swap(this, i, i-1);
    }
}
