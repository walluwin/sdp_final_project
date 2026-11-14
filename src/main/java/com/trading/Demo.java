package com.trading;

import com.trading.facade.TradingPlatform;
import com.trading.model.OrderSide;

/** Shows all patterns step by step. Start with: run.bat --demo */
public class Demo {

    public static void run(TradingPlatform platform) {
        title("1. Connect to BINANCE (Facade -> Factory Method -> Adapter + Decorators)");
        platform.connect("BINANCE");

        title("2. Start two bots with different strategies (Strategy + Observer)");
        platform.startBot("BTC/USD", "moving-average", 0.2);
        platform.startBot("ETH/USD", "momentum", 3);
        platform.printBots();

        title("3. Show live prices for 2 ticks (Observer: PriceDisplay subscribed)");
        platform.showPrices(true);
        platform.runMarket(2);
        platform.showPrices(false);

        title("4. Market runs 50 ticks, bots trade by themselves");
        platform.runMarket(50);
        platform.printBots();

        title("5. Change the strategy of bot-2 while it is running (Strategy)");
        platform.changeBotStrategy("bot-2", "mean-reversion");
        platform.runMarket(50);
        platform.printBots();

        title("6. Manual orders (Builder + RiskLimit decorator + checks)");
        System.out.println("Result: " + platform.placeMarketOrder(OrderSide.BUY, "BTC/USD", 1));
        System.out.println("Result: " + platform.placeLimitOrder(OrderSide.BUY, "ETH/USD", 1, 100));
        System.out.println("Result: " + platform.placeMarketOrder(OrderSide.BUY, "ETH/USD", 2));
        System.out.println("Result: " + platform.placeMarketOrder(OrderSide.BUY, "AAPL", 10));

        title("7. Stop crypto bots and switch to NASDAQ (another Adapter, same interface)");
        platform.stopBot("bot-1");
        platform.stopBot("bot-2");
        platform.connect("NASDAQ");
        System.out.println("Result: " + platform.placeMarketOrder(OrderSide.BUY, "AAPL", 20));
        System.out.println("Result: " + platform.placeMarketOrder(OrderSide.BUY, "TSLA", 2.5));
        platform.startBot("TSLA", "mean-reversion", 10);
        platform.runMarket(50);
        platform.printBots();

        title("8. Portfolio");
        platform.printPortfolio();
        platform.shutdown();
    }

    private static void title(String text) {
        System.out.println();
        System.out.println(">>> " + text);
    }
}
