package com.trading.exchange;

import com.trading.exchange.api.BinanceApi;
import com.trading.model.Order;
import com.trading.model.OrderResult;

/**
 * PATTERN: ADAPTER (structural).
 * Our platform works with the Exchange interface, but BinanceApi has different methods.
 * The adapter "translates": our symbols -> Binance pairs, numbers -> text,
 * and the text answer -> OrderResult.
 */
public class BinanceAdapter implements Exchange {

    private static final String API_KEY = "demo-key";

    private final BinanceApi api;

    public BinanceAdapter(BinanceApi api) {
        this.api = api;
    }

    @Override
    public String getName() {
        return "BINANCE";
    }

    @Override
    public void connect() {
        api.login(API_KEY);
    }

    @Override
    public void disconnect() {
        api.logout();
    }

    @Override
    public boolean supports(String symbol) {
        return symbol.equals("BTC/USD") || symbol.equals("ETH/USD");
    }

    @Override
    public double getPrice(String symbol) {
        String priceText = api.getTickerPrice(toPair(symbol));
        return Double.parseDouble(priceText);
    }

    @Override
    public OrderResult placeOrder(Order order) {
        String answer = api.newOrder(
                toPair(order.getSymbol()),
                order.getSide().name(),
                String.valueOf(order.getQuantity()));

        if (answer.startsWith("OK:")) {
            double price = Double.parseDouble(answer.substring("OK:".length()));
            return OrderResult.filled(order, price, getName());
        }
        return OrderResult.rejected(order, "Binance: " + answer.substring("ERROR:".length()));
    }

    private String toPair(String symbol) {
        return symbol.replace("/USD", "USDT"); // "BTC/USD" -> "BTCUSDT"
    }
}
