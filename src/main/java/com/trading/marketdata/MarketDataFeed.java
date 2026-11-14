package com.trading.marketdata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * PATTERN: OBSERVER (behavioral). This is the Subject.
 * Simulates a live market: keeps current prices, changes them randomly
 * on every tick and notifies all subscribed listeners.
 * The feed does not know who the listeners are (bot, display, ...),
 * it only knows the MarketDataListener interface.
 */
public class MarketDataFeed {

    private static final double MAX_CHANGE = 0.03; // price moves from -1.5% to +1.5% per tick

    private final List<MarketDataListener> listeners = new ArrayList<>();
    private final Map<String, Double> prices = new LinkedHashMap<>();
    private final Random random = new Random(42); // fixed seed: same prices on every run
    private int tick = 0;

    public void addSymbol(String symbol, double startPrice) {
        prices.put(symbol, startPrice);
    }

    public void subscribe(MarketDataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(MarketDataListener listener) {
        listeners.remove(listener);
    }

    public int getListenerCount() {
        return listeners.size();
    }

    /** Returns the current price, or 0 if the symbol is unknown. */
    public double getPrice(String symbol) {
        Double price = prices.get(symbol);
        if (price == null) {
            return 0;
        }
        return price;
    }

    public List<String> getSymbols() {
        return new ArrayList<>(prices.keySet());
    }

    public int getTick() {
        return tick;
    }

    /** Moves the market one step forward. */
    public void nextTick() {
        tick++;
        for (String symbol : prices.keySet()) {
            double oldPrice = prices.get(symbol);
            double change = (random.nextDouble() - 0.5) * MAX_CHANGE;
            double newPrice = Math.round(oldPrice * (1 + change) * 100) / 100.0;
            prices.put(symbol, newPrice);
            notifyListeners(symbol, newPrice);
        }
    }

    private void notifyListeners(String symbol, double price) {
        // copy of the list, so a listener can unsubscribe safely during notification
        for (MarketDataListener listener : new ArrayList<>(listeners)) {
            listener.onPriceUpdate(symbol, price);
        }
    }
}
