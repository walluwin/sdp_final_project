package com.trading.strategy;

import java.util.List;

/**
 * PATTERN: STRATEGY (behavioral). This is the Strategy interface.
 * A trading algorithm looks at recent prices and decides: BUY, SELL or HOLD.
 * The TradingBot only knows this interface, so we can give it any algorithm
 * and even change the algorithm while the bot is running.
 */
public interface TradingStrategy {

    String getName();

    /** prices: recent prices, the last element is the newest one */
    Signal decide(List<Double> prices);
}
