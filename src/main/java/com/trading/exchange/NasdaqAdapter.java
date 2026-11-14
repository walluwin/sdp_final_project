package com.trading.exchange;

import com.trading.exchange.api.NasdaqApi;
import com.trading.model.Order;
import com.trading.model.OrderResult;
import com.trading.model.OrderSide;

/**
 * PATTERN: ADAPTER (structural).
 * Makes NasdaqApi look like our Exchange interface:
 * dollars <-> cents, BUY/SELL -> 1/2, double quantity -> whole shares,
 * result code -> OrderResult.
 */
public class NasdaqAdapter implements Exchange {

    private final NasdaqApi api;

    public NasdaqAdapter(NasdaqApi api) {
        this.api = api;
    }

    @Override
    public String getName() {
        return "NASDAQ";
    }

    @Override
    public void connect() {
        api.openSession("trading-platform");
    }

    @Override
    public void disconnect() {
        api.closeSession();
    }

    @Override
    public boolean supports(String symbol) {
        return symbol.equals("AAPL") || symbol.equals("TSLA");
    }

    @Override
    public double getPrice(String symbol) {
        return api.getPriceInCents(symbol) / 100.0;
    }

    @Override
    public OrderResult placeOrder(Order order) {
        double quantity = order.getQuantity();
        if (quantity != Math.floor(quantity)) {
            return OrderResult.rejected(order, "Nasdaq: stocks can be traded only in whole shares");
        }

        int side;
        if (order.getSide() == OrderSide.BUY) {
            side = NasdaqApi.SIDE_BUY;
        } else {
            side = NasdaqApi.SIDE_SELL;
        }

        int code = api.sendOrder(order.getSymbol(), side, (int) quantity);
        if (code == NasdaqApi.CODE_OK) {
            double price = api.getLastFillPriceInCents() / 100.0;
            return OrderResult.filled(order, price, getName());
        }
        return OrderResult.rejected(order, "Nasdaq: order failed with code " + code);
    }
}
