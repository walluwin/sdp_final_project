package com.trading.factory;

import com.trading.decorator.LoggingExchange;
import com.trading.decorator.RiskLimitExchange;
import com.trading.exchange.Exchange;

/**
 * PATTERN: FACTORY METHOD (creational). This is the Creator.
 * createExchange() is the factory method: every subclass decides
 * which concrete exchange to create.
 * connectToExchange() is the common part for all exchanges:
 * create -> add decorators -> connect.
 * To add a new exchange we only write a new subclass, nothing else changes.
 */
public abstract class ExchangeFactory {

    private static final double MAX_ORDER_VALUE = 50_000;

    public abstract String getExchangeName();

    /** The factory method. */
    protected abstract Exchange createExchange();

    public Exchange connectToExchange() {
        Exchange exchange = createExchange();
        exchange = new RiskLimitExchange(exchange, MAX_ORDER_VALUE);
        exchange = new LoggingExchange(exchange);
        exchange.connect();
        return exchange;
    }
}
