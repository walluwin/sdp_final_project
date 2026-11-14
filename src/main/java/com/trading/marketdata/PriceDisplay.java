package com.trading.marketdata;

/**
 * PATTERN: OBSERVER. This is a concrete Observer.
 * Prints every new price to the console (a "live ticker").
 */
public class PriceDisplay implements MarketDataListener {

    @Override
    public void onPriceUpdate(String symbol, double price) {
        System.out.println(String.format("   price  %-8s %10.2f", symbol, price));
    }
}
