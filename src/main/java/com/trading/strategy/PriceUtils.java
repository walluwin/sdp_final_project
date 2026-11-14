package com.trading.strategy;

import java.util.List;

/** Small math helper used by several strategies (so we do not repeat the same code). */
public class PriceUtils {

    private PriceUtils() {
    }

    /** Average of the last "count" prices. */
    public static double average(List<Double> prices, int count) {
        double sum = 0;
        for (int i = prices.size() - count; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / count;
    }

    public static double lastPrice(List<Double> prices) {
        return prices.get(prices.size() - 1);
    }
}
