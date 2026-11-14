package com.trading.model;

/**
 * What happened with an order: it was filled (executed) or rejected.
 * Every exchange returns this same class, so the rest of the program
 * does not care which exchange was used.
 */
public class OrderResult {

    private final Order order;
    private final boolean filled;
    private final double price;
    private final String message;

    private OrderResult(Order order, boolean filled, double price, String message) {
        this.order = order;
        this.filled = filled;
        this.price = price;
        this.message = message;
    }

    public static OrderResult filled(Order order, double price, String exchangeName) {
        return new OrderResult(order, true, price, "filled on " + exchangeName);
    }

    public static OrderResult rejected(Order order, String reason) {
        return new OrderResult(order, false, 0, reason);
    }

    public Order getOrder() {
        return order;
    }

    public boolean isFilled() {
        return filled;
    }

    public double getPrice() {
        return price;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String orderText = "#" + order.getId() + " " + order.getSide() + " "
                + order.getQuantity() + " " + order.getSymbol();
        if (filled) {
            return "FILLED " + orderText + " at " + String.format("%.2f", price) + " (" + message + ")";
        }
        return "REJECTED " + orderText + ": " + message;
    }
}
