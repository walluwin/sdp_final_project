package com.trading.strategy;

import java.util.List;

/**
 * Concrete Strategy: mean reversion.
 * The idea: the price usually comes back to its average.
 * Price is 2% below the average -> it is cheap -> BUY.
 * Price is 2% above the average -> it is expensive -> SELL.
 */
public class MeanReversionStrategy implements TradingStrategy {

    private static final int PERIOD = 10;
    private static final double DISTANCE = 0.02; // 2%

    @Override
    public String getName() {
        return "Mean Reversion (" + PERIOD + " ticks, 2%)";
    }

    @Override
    public Signal decide(List<Double> prices) {
        if (prices.size() < PERIOD) {
            return Signal.HOLD;
        }
        double average = PriceUtils.average(prices, PERIOD);
        double price = PriceUtils.lastPrice(prices);

        if (price < average * (1 - DISTANCE)) {
            return Signal.BUY;
        }
        if (price > average * (1 + DISTANCE)) {
            return Signal.SELL;
        }
        return Signal.HOLD;
    }
}
