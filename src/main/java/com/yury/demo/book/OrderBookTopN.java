package com.yury.demo.book;

/**
 * Class to contain top N buy and sell orders, used for JSON formatting message
 */
public class OrderBookTopN {
    SortedBuyOrderArray<BuyOrder> buyTopN;
    SortedSellOrderArray<SellOrder> sellTopN;

    public OrderBookTopN(SortedBuyOrderArray<BuyOrder> buyTopN, SortedSellOrderArray<SellOrder> sellTopN) {
        this.buyTopN = buyTopN;
        this.sellTopN = sellTopN;
    }

    public SortedBuyOrderArray<BuyOrder> getBuyTopN() {
        return buyTopN;
    }

    public void setBuyTopN(SortedBuyOrderArray<BuyOrder> buyTopN) {
        this.buyTopN = buyTopN;
    }

    public SortedSellOrderArray<SellOrder> getSellTopN() {
        return sellTopN;
    }

    public void setSellTopN(SortedSellOrderArray<SellOrder> sellTopN) {
        this.sellTopN = sellTopN;
    }
}
