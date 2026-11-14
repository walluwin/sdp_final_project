package com.trading.decorator;

import com.trading.exchange.Exchange;
import com.trading.model.Order;
import com.trading.model.OrderResult;

/** Concrete Decorator: prints everything that is sent to the exchange and every answer. */
public class LoggingExchange extends ExchangeDecorator {

    public LoggingExchange(Exchange exchange) {
        super(exchange);
    }

    @Override
    public void connect() {
        super.connect();
        log("connected");
    }

    @Override
    public void disconnect() {
        super.disconnect();
        log("disconnected");
    }

    @Override
    public OrderResult placeOrder(Order order) {
        log("sending " + order);
        OrderResult result = super.placeOrder(order);
        log("answer  " + result);
        return result;
    }

    private void log(String message) {
        System.out.println("[" + getName() + "] " + message);
    }
}
