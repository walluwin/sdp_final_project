package com.trading.bot;

import com.trading.marketdata.MarketDataListener;
import com.trading.model.Order;
import com.trading.model.OrderResult;
import com.trading.model.OrderSide;
import com.trading.portfolio.Portfolio;
import com.trading.service.OrderService;
import com.trading.strategy.Signal;
import com.trading.strategy.TradingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * A bot that trades one symbol automatically. Here two patterns work together:
 * OBSERVER: the bot is a MarketDataListener, the market sends it every new price.
 * STRATEGY: the bot does not decide itself, it asks its TradingStrategy.
 *   The strategy can be changed at any time with setStrategy().
 * Orders are created with the Order.Builder (BUILDER pattern).
 */
public class TradingBot implements MarketDataListener {

    private static final int MAX_PRICES = 100; // remember only the last 100 prices

    private final String name;
    private final String symbol;
    private final double quantity;
    private final OrderService orderService;
    private final Portfolio portfolio;
    private final List<Double> prices = new ArrayList<>();
    private TradingStrategy strategy;
    private int ordersSent = 0;

    public TradingBot(String name, String symbol, double quantity, TradingStrategy strategy,
                      OrderService orderService, Portfolio portfolio) {
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
        this.strategy = strategy;
        this.orderService = orderService;
        this.portfolio = portfolio;
    }

    @Override
    public void onPriceUpdate(String symbol, double price) {
        if (!this.symbol.equals(symbol)) {
            return; // this price is not for our symbol
        }
        prices.add(price);
        if (prices.size() > MAX_PRICES) {
            prices.remove(0);
        }

        Signal signal = strategy.decide(prices);
        if (signal == Signal.BUY && !portfolio.hasPosition(symbol)) {
            sendOrder(OrderSide.BUY, quantity);
        } else if (signal == Signal.SELL && portfolio.hasPosition(symbol)) {
            sendOrder(OrderSide.SELL, portfolio.getPosition(symbol));
        }
    }

    private void sendOrder(OrderSide side, double orderQuantity) {
        Order order = new Order.Builder()
                .symbol(symbol)
                .side(side)
                .quantity(orderQuantity)
                .source(name)
                .build();
        ordersSent++;
        OrderResult result = orderService.execute(order);
        if (!result.isFilled()) {
            System.out.println("[" + name + "] " + result);
        }
    }

    public void setStrategy(TradingStrategy strategy) {
        this.strategy = strategy;
    }

    public TradingStrategy getStrategy() {
        return strategy;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getOrdersSent() {
        return ordersSent;
    }

    @Override
    public String toString() {
        return name + " | " + symbol + " | " + strategy.getName()
                + " | quantity " + quantity + " | orders sent: " + ordersSent;
    }
}
