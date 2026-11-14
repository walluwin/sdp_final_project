package com.trading.decorator;

import com.trading.exchange.Exchange;
import com.trading.model.Order;
import com.trading.model.OrderResult;

/**
 * Concrete Decorator: protection from too big orders.
 * If one order costs more than the limit, it is rejected and never reaches the exchange.
 */
public class RiskLimitExchange extends ExchangeDecorator {

    private final double maxOrderValue;

    public RiskLimitExchange(Exchange exchange, double maxOrderValue) {
        super(exchange);
        this.maxOrderValue = maxOrderValue;
    }

    @Override
    public OrderResult placeOrder(Order order) {
        double price = order.isLimit() ? order.getLimitPrice() : getPrice(order.getSymbol());
        double orderValue = order.getQuantity() * price;

        if (orderValue > maxOrderValue) {
            return OrderResult.rejected(order, String.format(
                    "risk limit: order value %.2f is bigger than the limit %.2f", orderValue, maxOrderValue));
        }
        return super.placeOrder(order);
    }
}
