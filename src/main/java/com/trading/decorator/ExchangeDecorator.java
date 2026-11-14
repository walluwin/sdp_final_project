package com.trading.decorator;

import com.trading.exchange.Exchange;
import com.trading.model.Order;
import com.trading.model.OrderResult;

/**
 * PATTERN: DECORATOR (structural). This is the base decorator.
 * It is also an Exchange, and it holds another Exchange inside.
 * By default it just passes every call to the wrapped exchange.
 * Concrete decorators override only what they want to change.
 *
 * Decorators can be put one inside another:
 *   new LoggingExchange(new RiskLimitExchange(new BinanceAdapter(...), 50000))
 */
public abstract class ExchangeDecorator implements Exchange {

    protected final Exchange exchange;

    public ExchangeDecorator(Exchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String getName() {
        return exchange.getName();
    }

    @Override
    public void connect() {
        exchange.connect();
    }

    @Override
    public void disconnect() {
        exchange.disconnect();
    }

    @Override
    public boolean supports(String symbol) {
        return exchange.supports(symbol);
    }

    @Override
    public double getPrice(String symbol) {
        return exchange.getPrice(symbol);
    }

    @Override
    public OrderResult placeOrder(Order order) {
        return exchange.placeOrder(order);
    }
}
