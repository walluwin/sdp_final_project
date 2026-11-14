package com.trading.marketdata;

/**
 * PATTERN: OBSERVER (behavioral). This is the Observer interface.
 * Everyone who wants to know about new prices implements this interface
 * and subscribes to the MarketDataFeed.
 */
public interface MarketDataListener {

    void onPriceUpdate(String symbol, double price);
}
