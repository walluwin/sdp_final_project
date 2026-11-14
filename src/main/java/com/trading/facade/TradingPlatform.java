package com.trading.facade;

import com.trading.bot.TradingBot;
import com.trading.exchange.Exchange;
import com.trading.factory.BinanceFactory;
import com.trading.factory.ExchangeFactory;
import com.trading.factory.NasdaqFactory;
import com.trading.marketdata.MarketDataFeed;
import com.trading.marketdata.PriceDisplay;
import com.trading.model.Order;
import com.trading.model.OrderResult;
import com.trading.model.OrderSide;
import com.trading.portfolio.Portfolio;
import com.trading.service.OrderService;
import com.trading.strategy.MeanReversionStrategy;
import com.trading.strategy.MomentumStrategy;
import com.trading.strategy.MovingAverageStrategy;
import com.trading.strategy.TradingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * PATTERN: FACADE (structural).
 * One simple class for the whole platform. The menu (Main) talks only to this class
 * and does not need to know about factories, adapters, decorators, the market feed
 * or the portfolio. The facade does not do the work itself, it calls the right classes.
 */
public class TradingPlatform {

    private static final double START_CASH = 100_000;

    private final MarketDataFeed market = new MarketDataFeed();
    private final Portfolio portfolio = new Portfolio(START_CASH);
    private final OrderService orderService = new OrderService(portfolio);
    private final PriceDisplay priceDisplay = new PriceDisplay();
    private final List<ExchangeFactory> factories = new ArrayList<>();
    private final List<TradingBot> bots = new ArrayList<>();
    private Exchange exchange;
    private int botCounter = 0;

    public TradingPlatform() {
        market.addSymbol("BTC/USD", 65_000);
        market.addSymbol("ETH/USD", 3_200);
        market.addSymbol("AAPL", 190);
        market.addSymbol("TSLA", 250);

        factories.add(new BinanceFactory(market));
        factories.add(new NasdaqFactory(market));
    }

    // exchanges

    public void connect(String exchangeName) {
        for (ExchangeFactory factory : factories) {
            if (factory.getExchangeName().equalsIgnoreCase(exchangeName)) {
                disconnect();
                exchange = factory.connectToExchange();
                orderService.setExchange(exchange);
                return;
            }
        }
        throw new IllegalArgumentException("unknown exchange: " + exchangeName);
    }

    public void disconnect() {
        if (exchange != null) {
            exchange.disconnect();
            exchange = null;
            orderService.setExchange(null);
        }
    }

    public String getCurrentExchangeName() {
        if (exchange == null) {
            return "not connected";
        }
        return exchange.getName();
    }

    public List<String> getExchangeNames() {
        List<String> names = new ArrayList<>();
        for (ExchangeFactory factory : factories) {
            names.add(factory.getExchangeName());
        }
        return names;
    }

    // market

    public List<String> getSymbols() {
        return market.getSymbols();
    }

    public void runMarket(int ticks) {
        for (int i = 0; i < ticks; i++) {
            market.nextTick();
        }
    }

    public void showPrices(boolean show) {
        if (show) {
            market.subscribe(priceDisplay);
        } else {
            market.unsubscribe(priceDisplay);
        }
    }

    // bots

    public List<String> getStrategyNames() {
        List<String> names = new ArrayList<>();
        names.add("moving-average");
        names.add("momentum");
        names.add("mean-reversion");
        return names;
    }

    public String startBot(String symbol, String strategyName, double quantity) {
        if (exchange == null) {
            throw new IllegalStateException("connect to an exchange first");
        }
        if (!exchange.supports(symbol)) {
            throw new IllegalArgumentException(symbol + " is not traded on " + exchange.getName());
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }

        TradingStrategy strategy = createStrategy(strategyName);
        botCounter++;
        TradingBot bot = new TradingBot("bot-" + botCounter, symbol, quantity, strategy, orderService, portfolio);
        bots.add(bot);
        market.subscribe(bot);
        return bot.getName();
    }

    public void changeBotStrategy(String botName, String strategyName) {
        findBot(botName).setStrategy(createStrategy(strategyName));
    }

    public void stopBot(String botName) {
        TradingBot bot = findBot(botName);
        market.unsubscribe(bot);
        bots.remove(bot);
    }

    public List<String> getBotNames() {
        List<String> names = new ArrayList<>();
        for (TradingBot bot : bots) {
            names.add(bot.getName());
        }
        return names;
    }

    public void printBots() {
        if (bots.isEmpty()) {
            System.out.println("No bots are running");
        }
        for (TradingBot bot : bots) {
            System.out.println(bot);
        }
    }

    // manual orders

    public OrderResult placeMarketOrder(OrderSide side, String symbol, double quantity) {
        Order order = new Order.Builder()
                .symbol(symbol)
                .side(side)
                .quantity(quantity)
                .build();
        return orderService.execute(order);
    }

    public OrderResult placeLimitOrder(OrderSide side, String symbol, double quantity, double limitPrice) {
        Order order = new Order.Builder()
                .symbol(symbol)
                .side(side)
                .quantity(quantity)
                .limitPrice(limitPrice)
                .build();
        return orderService.execute(order);
    }

    // portfolio

    public void printPortfolio() {
        portfolio.printReport(market);
    }

    public double getCash() {
        return portfolio.getCash();
    }

    public void shutdown() {
        for (TradingBot bot : bots) {
            market.unsubscribe(bot);
        }
        bots.clear();
        disconnect();
    }

    // helpers

    private TradingStrategy createStrategy(String name) {
        switch (name) {
            case "moving-average":
                return new MovingAverageStrategy();
            case "momentum":
                return new MomentumStrategy();
            case "mean-reversion":
                return new MeanReversionStrategy();
            default:
                throw new IllegalArgumentException("unknown strategy: " + name);
        }
    }

    private TradingBot findBot(String botName) {
        for (TradingBot bot : bots) {
            if (bot.getName().equals(botName)) {
                return bot;
            }
        }
        throw new IllegalArgumentException("no bot with name " + botName);
    }
}
