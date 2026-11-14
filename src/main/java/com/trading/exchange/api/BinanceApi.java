package com.trading.exchange.api;

import com.trading.marketdata.MarketDataFeed;

/**
 * Imagine this is a library from the Binance exchange that we cannot change.
 * Its methods do not match our Exchange interface:
 * symbols look like "BTCUSDT" (we use "BTC/USD")
 * prices and quantities are passed as text
 * the answer is a text: "OK:65000.0" or "ERROR:some reason"
 *
 * This is the "Adaptee" in the Adapter pattern.
 * (Simulation: the fake exchange takes its prices from our market feed.)
 */
public class BinanceApi {

    private final MarketDataFeed market;
    private boolean loggedIn = false;

    public BinanceApi(MarketDataFeed market) {
        this.market = market;
    }

    public void login(String apiKey) {
        loggedIn = apiKey != null && !apiKey.isEmpty();
    }

    public void logout() {
        loggedIn = false;
    }

    public String getTickerPrice(String pair) {
        return String.valueOf(market.getPrice(toMarketSymbol(pair)));
    }

    public String newOrder(String pair, String side, String quantity) {
        if (!loggedIn) {
            return "ERROR:not logged in";
        }
        if (!pair.equals("BTCUSDT") && !pair.equals("ETHUSDT")) {
            return "ERROR:unknown pair " + pair;
        }
        if (Double.parseDouble(quantity) <= 0) {
            return "ERROR:bad quantity " + quantity;
        }
        // a market order is filled at the current price
        return "OK:" + getTickerPrice(pair);
    }

    private String toMarketSymbol(String pair) {
        return pair.replace("USDT", "/USD"); // "BTCUSDT" -> "BTC/USD"
    }
}
