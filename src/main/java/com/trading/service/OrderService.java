package com.trading.service;

import com.trading.exchange.Exchange;
import com.trading.model.Order;
import com.trading.model.OrderResult;
import com.trading.model.OrderSide;
import com.trading.portfolio.Portfolio;

/**
 * Every order (from a bot or from the user) goes through this class:
 * 1. check the order (exchange connected? enough money? good limit price?)
 * 2. send it to the exchange
 * 3. save the trade in the portfolio
 * Bots and the facade both use it, so this logic is written only once.
 */
public class OrderService {

    private final Portfolio portfolio;
    private Exchange exchange;

    public OrderService(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public OrderResult execute(Order order) {
        String problem = findProblem(order);
        if (problem != null) {
            return OrderResult.rejected(order, problem);
        }

        OrderResult result = exchange.placeOrder(order);
        if (result.isFilled()) {
            saveTrade(order, result.getPrice());
        }
        return result;
    }

    /** Returns the reason why the order cannot be sent, or null if everything is OK. */
    private String findProblem(Order order) {
        if (exchange == null) {
            return "no exchange connected";
        }
        String symbol = order.getSymbol();
        if (!exchange.supports(symbol)) {
            return symbol + " is not traded on " + exchange.getName();
        }

        double marketPrice = exchange.getPrice(symbol);
        if (order.isLimit() && !isLimitPriceReached(order, marketPrice)) {
            return "limit price not reached, market price is " + marketPrice;
        }

        if (order.getSide() == OrderSide.BUY) {
            double cost = order.getQuantity() * marketPrice;
            if (cost > portfolio.getCash()) {
                return String.format("not enough cash: need %.2f, have %.2f", cost, portfolio.getCash());
            }
        } else if (order.getQuantity() > portfolio.getPosition(symbol)) {
            return "not enough " + symbol + " to sell, have " + portfolio.getPosition(symbol);
        }
        return null;
    }

    private boolean isLimitPriceReached(Order order, double marketPrice) {
        if (order.getSide() == OrderSide.BUY) {
            return marketPrice <= order.getLimitPrice(); // buy only if it is cheap enough
        }
        return marketPrice >= order.getLimitPrice();     // sell only if it is expensive enough
    }

    private void saveTrade(Order order, double price) {
        if (order.getSide() == OrderSide.BUY) {
            portfolio.addBuy(order.getSymbol(), order.getQuantity(), price);
        } else {
            portfolio.addSell(order.getSymbol(), order.getQuantity(), price);
        }
    }
}
