package com.trading.exchange;

import com.trading.model.Order;
import com.trading.model.OrderResult;

/**
 * The one interface our platform uses to work with any exchange.
 * Adapter: this is the "Target" interface (adapters implement it)
 * Decorator: this is the "Component" interface (decorators implement and wrap it)
 * Factory Method: this is the "Product" that factories create
 */
public interface Exchange {

    String getName();

    void connect();

    void disconnect();

    boolean supports(String symbol);

    double getPrice(String symbol);

    OrderResult placeOrder(Order order);
}
