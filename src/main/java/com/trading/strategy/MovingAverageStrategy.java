package com.trading.strategy;

import java.util.List;

/**
 * Concrete Strategy: moving averages.
 * If the short-term average is above the long-term average, the price is going up -> BUY.
 * If it is below -> SELL.
 */
public class MovingAverageStrategy implements TradingStrategy {

    private static final int SHORT_PERIOD = 5;
    private static final int LONG_PERIOD = 20;

    @Override
    public String getName() {
        return "Moving Average (" + SHORT_PERIOD + "/" + LONG_PERIOD + ")";
    }

    @Override
    public Signal decide(List<Double> prices) {
        if (prices.size() < LONG_PERIOD) {
            return Signal.HOLD; // not enough data yet
        }
        double shortAverage = PriceUtils.average(prices, SHORT_PERIOD);
        double longAverage = PriceUtils.average(prices, LONG_PERIOD);

        if (shortAverage > longAverage) {
            return Signal.BUY;
        }
        if (shortAverage < longAverage) {
            return Signal.SELL;
        }
        return Signal.HOLD;
    }
}
