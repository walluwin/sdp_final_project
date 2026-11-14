package com.trading.exchange.api;

import com.trading.marketdata.MarketDataFeed;

/**
 * Imagine this is a library from the NASDAQ stock exchange that we cannot change.
 * It is completely different from BinanceApi:
 * prices are in cents (long), not dollars
 * side is a number: 1 = buy, 2 = sell
 * only whole shares (int)
 * sendOrder returns a code: 0 = OK, 1 = error
 *
 * This is another "Adaptee" in the Adapter pattern.
 */
public class NasdaqApi {

    public static final int SIDE_BUY = 1;
    public static final int SIDE_SELL = 2;
    public static final int CODE_OK = 0;
    public static final int CODE_ERROR = 1;

    private final MarketDataFeed market;
    private boolean sessionOpen = false;
    private long lastFillPriceInCents = 0;

    public NasdaqApi(MarketDataFeed market) {
        this.market = market;
    }

    public void openSession(String userName) {
        sessionOpen = true;
    }

    public void closeSession() {
        sessionOpen = false;
    }

    public long getPriceInCents(String ticker) {
        return Math.round(market.getPrice(ticker) * 100);
    }

    public int sendOrder(String ticker, int side, int shares) {
        if (!sessionOpen || shares <= 0 || getPriceInCents(ticker) == 0) {
            return CODE_ERROR;
        }
        if (side != SIDE_BUY && side != SIDE_SELL) {
            return CODE_ERROR;
        }
        lastFillPriceInCents = getPriceInCents(ticker);
        return CODE_OK;
    }

    public long getLastFillPriceInCents() {
        return lastFillPriceInCents;
    }
}
