package com.trading.model;

/**
 * An order to buy or sell something.
 *
 * PATTERN: BUILDER (creational)
 * An order has required fields (symbol, side, quantity) and optional fields
 * (type, limit price, source). Instead of a big constructor with many parameters
 * we use a Builder: the order is created step by step and checked in build().
 *
 * Example:
 *   Order order = new Order.Builder()
 *           .symbol("BTC/USD")
 *           .side(OrderSide.BUY)
 *           .quantity(0.5)
 *           .build();
 */
public class Order {

    private static int nextId = 1;

    private final int id;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final double quantity;
    private final double limitPrice;
    private final String source;

    // private: an order can be created only through the Builder
    private Order(Builder builder) {
        this.id = nextId++;
        this.symbol = builder.symbol;
        this.side = builder.side;
        this.type = builder.type;
        this.quantity = builder.quantity;
        this.limitPrice = builder.limitPrice;
        this.source = builder.source;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public String getSource() {
        return source;
    }

    public boolean isLimit() {
        return type == OrderType.LIMIT;
    }

    @Override
    public String toString() {
        String text = "#" + id + " " + side + " " + quantity + " " + symbol + " " + type;
        if (isLimit()) {
            text = text + " at " + limitPrice;
        }
        return text + " [" + source + "]";
    }

    public static class Builder {

        // default values for optional fields
        private String symbol;
        private OrderSide side;
        private OrderType type = OrderType.MARKET;
        private double quantity;
        private double limitPrice;
        private String source = "manual";

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder side(OrderSide side) {
            this.side = side;
            return this;
        }

        public Builder quantity(double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder limitPrice(double limitPrice) {
            this.type = OrderType.LIMIT;
            this.limitPrice = limitPrice;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Order build() {
            if (symbol == null || symbol.isEmpty()) {
                throw new IllegalArgumentException("order must have a symbol");
            }
            if (side == null) {
                throw new IllegalArgumentException("order must have a side (BUY or SELL)");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be greater than 0");
            }
            if (type == OrderType.LIMIT && limitPrice <= 0) {
                throw new IllegalArgumentException("limit price must be greater than 0");
            }
            return new Order(this);
        }
    }
}
