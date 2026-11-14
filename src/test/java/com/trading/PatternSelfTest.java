package com.trading;

import com.trading.decorator.RiskLimitExchange;
import com.trading.exchange.Exchange;
import com.trading.facade.TradingPlatform;
import com.trading.factory.BinanceFactory;
import com.trading.factory.NasdaqFactory;
import com.trading.marketdata.MarketDataFeed;
import com.trading.marketdata.MarketDataListener;
import com.trading.model.Order;
import com.trading.model.OrderResult;
import com.trading.model.OrderSide;
import com.trading.strategy.MeanReversionStrategy;
import com.trading.strategy.MomentumStrategy;
import com.trading.strategy.MovingAverageStrategy;
import com.trading.strategy.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simple checks for every pattern (no libraries needed).
 * Start with: test.bat
 */
public class PatternSelfTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        testBuilder();
        testObserver();
        testStrategy();
        testFactoryMethodAndAdapter();
        testDecorator();
        testFacade();

        System.out.println();
        System.out.println("Passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // Builder

    private static void testBuilder() {
        Order order = new Order.Builder().symbol("BTC/USD").side(OrderSide.BUY).quantity(0.5).limitPrice(60000).build();
        check("Builder: creates a limit order", order.isLimit() && order.getLimitPrice() == 60000);

        boolean error = false;
        try {
            new Order.Builder().symbol("BTC/USD").quantity(1).build(); // no side
        } catch (IllegalArgumentException e) {
            error = true;
        }
        check("Builder: order without side is not allowed", error);
    }

    // Observer

    private static void testObserver() {
        MarketDataFeed market = new MarketDataFeed();
        market.addSymbol("AAPL", 100);

        List<Double> received = new ArrayList<>();
        MarketDataListener listener = (symbol, price) -> received.add(price);

        market.subscribe(listener);
        market.nextTick();
        check("Observer: subscriber gets the new price", received.size() == 1);

        market.unsubscribe(listener);
        market.nextTick();
        check("Observer: after unsubscribe no more prices", received.size() == 1);
    }

    // Strategy

    private static void testStrategy() {
        List<Double> fallingPrices = List.of(100.0, 99.0, 98.0, 97.0, 96.0, 95.0);
        check("Strategy: momentum sells when price falls",
                new MomentumStrategy().decide(fallingPrices) == Signal.SELL);

        List<Double> dip = List.of(100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 90.0);
        check("Strategy: mean reversion buys after a big drop",
                new MeanReversionStrategy().decide(dip) == Signal.BUY);

        check("Strategy: not enough prices means HOLD",
                new MovingAverageStrategy().decide(List.of(1.0, 2.0)) == Signal.HOLD);
    }

    // Factory Method + Adapter

    private static void testFactoryMethodAndAdapter() {
        MarketDataFeed market = new MarketDataFeed();
        market.addSymbol("BTC/USD", 65000);
        market.addSymbol("AAPL", 190.25);

        Exchange binance = new BinanceFactory(market).connectToExchange();
        Exchange nasdaq = new NasdaqFactory(market).connectToExchange();
        check("Factory Method: factories create the right exchanges",
                binance.getName().equals("BINANCE") && nasdaq.getName().equals("NASDAQ"));

        check("Adapter: Binance price text is converted to a number", binance.getPrice("BTC/USD") == 65000);
        check("Adapter: Nasdaq cents are converted to dollars", nasdaq.getPrice("AAPL") == 190.25);

        OrderResult crypto = binance.placeOrder(market(OrderSide.BUY, "BTC/USD", 0.1));
        check("Adapter: Binance order is filled", crypto.isFilled());

        OrderResult halfShare = nasdaq.placeOrder(market(OrderSide.BUY, "AAPL", 1.5));
        check("Adapter: Nasdaq does not accept half shares", !halfShare.isFilled());
    }

    // Decorator

    private static void testDecorator() {
        MarketDataFeed market = new MarketDataFeed();
        market.addSymbol("BTC/USD", 65000);
        Exchange binance = new BinanceFactory(market).connectToExchange();
        Exchange limited = new RiskLimitExchange(binance, 10000);

        check("Decorator: too big order is rejected", !limited.placeOrder(market(OrderSide.BUY, "BTC/USD", 1)).isFilled());
        check("Decorator: small order goes to the exchange", limited.placeOrder(market(OrderSide.BUY, "BTC/USD", 0.1)).isFilled());
        check("Decorator: other methods still work", limited.getName().equals("BINANCE"));
    }

    // Facade

    private static void testFacade() {
        TradingPlatform platform = new TradingPlatform();
        check("Facade: no order without exchange",
                !platform.placeMarketOrder(OrderSide.BUY, "BTC/USD", 0.1).isFilled());

        platform.connect("binance");
        OrderResult buy = platform.placeMarketOrder(OrderSide.BUY, "BTC/USD", 0.1);
        check("Facade: manual order is filled", buy.isFilled());
        check("Facade: cash goes down after buying", platform.getCash() < 100_000);
        check("Facade: cannot sell more than we have",
                !platform.placeMarketOrder(OrderSide.SELL, "BTC/USD", 5).isFilled());

        String bot = platform.startBot("ETH/USD", "momentum", 1);
        platform.runMarket(30);
        platform.changeBotStrategy(bot, "moving-average");
        platform.runMarket(30);
        check("Facade: bot works after changing strategy", platform.getBotNames().contains(bot));

        platform.shutdown();
        check("Facade: shutdown stops bots and disconnects",
                platform.getBotNames().isEmpty() && platform.getCurrentExchangeName().equals("not connected"));
    }

    // helpers

    private static Order market(OrderSide side, String symbol, double quantity) {
        return new Order.Builder().symbol(symbol).side(side).quantity(quantity).build();
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }
}
