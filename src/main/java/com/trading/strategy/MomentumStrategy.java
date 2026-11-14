package com.trading.strategy;

import java.util.List;

/**
 * Concrete Strategy: momentum.
 * If the price grew more than 2% in the last 5 ticks -> BUY.
 * If it fell more than 2% -> SELL.
 */
public class MomentumStrategy implements TradingStrategy {

    private static final int PERIOD = 5;
    private static final double THRESHOLD = 0.02; // 2%

    @Override
    public String getName() {
        return "Momentum (" + PERIOD + " ticks, 2%)";
    }

    @Override
    public Signal decide(List<Double> prices) {
        if (prices.size() <= PERIOD) {
            return Signal.HOLD;
        }
        double oldPrice = prices.get(prices.size() - 1 - PERIOD);
        double newPrice = PriceUtils.lastPrice(prices);
        double change = (newPrice - oldPrice) / oldPrice;

        if (change > THRESHOLD) {
            return Signal.BUY;
        }
        if (change < -THRESHOLD) {
            return Signal.SELL;
        }
        return Signal.HOLD;
    }
}
