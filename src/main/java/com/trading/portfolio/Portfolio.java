package com.trading.portfolio;

import com.trading.marketdata.MarketDataFeed;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Our trading account: cash, what we own (positions) and the list of trades. */
public class Portfolio {

    private final double startCash;
    private double cash;
    private final Map<String, Double> positions = new LinkedHashMap<>();
    private final List<String> history = new ArrayList<>();

    public Portfolio(double startCash) {
        this.startCash = startCash;
        this.cash = startCash;
    }

    public double getCash() {
        return cash;
    }

    /** How many units of this symbol we own (0 if none). */
    public double getPosition(String symbol) {
        Double quantity = positions.get(symbol);
        if (quantity == null) {
            return 0;
        }
        return quantity;
    }

    public boolean hasPosition(String symbol) {
        return getPosition(symbol) > 0;
    }

    public List<String> getHistory() {
        return history;
    }

    public void addBuy(String symbol, double quantity, double price) {
        cash = cash - quantity * price;
        positions.put(symbol, getPosition(symbol) + quantity);
        history.add("BUY  " + quantity + " " + symbol + " at " + String.format("%.2f", price));
    }

    public void addSell(String symbol, double quantity, double price) {
        cash = cash + quantity * price;
        double left = getPosition(symbol) - quantity;
        if (left < 0.000001) {
            positions.remove(symbol); // sold everything
        } else {
            positions.put(symbol, left);
        }
        history.add("SELL " + quantity + " " + symbol + " at " + String.format("%.2f", price));
    }

    /** Cash + value of everything we own at current market prices. */
    public double getTotalValue(MarketDataFeed market) {
        double total = cash;
        for (String symbol : positions.keySet()) {
            total += positions.get(symbol) * market.getPrice(symbol);
        }
        return total;
    }

    public void printReport(MarketDataFeed market) {
        double total = getTotalValue(market);
        double profit = total - startCash;

        System.out.println("============== PORTFOLIO ==============");
        System.out.println(String.format("Cash:          %.2f", cash));
        System.out.println("Positions:");
        if (positions.isEmpty()) {
            System.out.println("   (none)");
        }
        for (String symbol : positions.keySet()) {
            double quantity = positions.get(symbol);
            double price = market.getPrice(symbol);
            System.out.println(String.format("   %-8s %8s x %10.2f = %.2f", symbol, quantity, price, quantity * price));
        }
        System.out.println(String.format("Total value:   %.2f", total));
        System.out.println(String.format("Profit/loss:   %.2f (%.2f%%)", profit, profit / startCash * 100));
        System.out.println("Trades (" + history.size() + "):");
        for (String trade : history) {
            System.out.println("   " + trade);
        }
        System.out.println("=======================================");
    }
}
