package com.trading;

import com.trading.facade.TradingPlatform;
import com.trading.model.OrderResult;
import com.trading.model.OrderSide;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Console menu of the trading platform.
 *   run.bat           starts the menu
 *   run.bat --demo    starts the automatic demo of all patterns
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static boolean pricesShown = false;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // numbers like 65000.00, not 65000,00
        TradingPlatform platform = new TradingPlatform();

        if (args.length > 0 && args[0].equals("--demo")) {
            Demo.run(platform);
            return;
        }

        boolean running = true;
        while (running) {
            printMenu(platform);
            String choice = readText("Choose");
            try {
                switch (choice) {
                    case "1" -> platform.connect(choose("Exchange", platform.getExchangeNames()));
                    case "2" -> startBot(platform);
                    case "3" -> changeStrategy(platform);
                    case "4" -> platform.stopBot(choose("Bot", platform.getBotNames()));
                    case "5" -> platform.printBots();
                    case "6" -> platform.runMarket(readInt("How many ticks"));
                    case "7" -> placeOrder(platform);
                    case "8" -> platform.printPortfolio();
                    case "9" -> togglePrices(platform);
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        platform.shutdown();
        System.out.println("Bye!");
    }

    private static void printMenu(TradingPlatform platform) {
        System.out.println();
        System.out.println("=== Trading Platform | exchange: " + platform.getCurrentExchangeName()
                + " | bots: " + platform.getBotNames().size() + " ===");
        System.out.println("1) Connect to exchange");
        System.out.println("2) Start bot");
        System.out.println("3) Change bot strategy");
        System.out.println("4) Stop bot");
        System.out.println("5) Show bots");
        System.out.println("6) Run market");
        System.out.println("7) Buy / sell manually");
        System.out.println("8) Show portfolio");
        System.out.println("9) Show / hide live prices");
        System.out.println("0) Exit");
    }

    private static void startBot(TradingPlatform platform) {
        String symbol = choose("Symbol", platform.getSymbols());
        String strategy = choose("Strategy", platform.getStrategyNames());
        double quantity = readDouble("Quantity per trade");
        String botName = platform.startBot(symbol, strategy, quantity);
        System.out.println("Started " + botName);
    }

    private static void changeStrategy(TradingPlatform platform) {
        String botName = choose("Bot", platform.getBotNames());
        String strategy = choose("New strategy", platform.getStrategyNames());
        platform.changeBotStrategy(botName, strategy);
        System.out.println("Strategy changed");
    }

    private static void placeOrder(TradingPlatform platform) {
        OrderSide side = OrderSide.valueOf(choose("Side", List.of("BUY", "SELL")));
        String symbol = choose("Symbol", platform.getSymbols());
        double quantity = readDouble("Quantity");
        String type = choose("Order type", List.of("MARKET", "LIMIT"));

        OrderResult result;
        if (type.equals("LIMIT")) {
            double limitPrice = readDouble("Limit price");
            result = platform.placeLimitOrder(side, symbol, quantity, limitPrice);
        } else {
            result = platform.placeMarketOrder(side, symbol, quantity);
        }
        System.out.println("Result: " + result);
    }

    private static void togglePrices(TradingPlatform platform) {
        pricesShown = !pricesShown;
        platform.showPrices(pricesShown);
        System.out.println(pricesShown ? "Live prices are shown" : "Live prices are hidden");
    }

    // input helpers

    /** Prints a numbered list and returns the chosen item. */
    private static String choose(String title, List<String> options) {
        if (options.isEmpty()) {
            throw new IllegalStateException("nothing to choose");
        }
        for (int i = 0; i < options.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + options.get(i));
        }
        while (true) {
            int number = readInt(title + " (number)");
            if (number >= 1 && number <= options.size()) {
                return options.get(number - 1);
            }
            System.out.println("Enter a number from 1 to " + options.size());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readText(prompt).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number");
            }
        }
    }

    private static String readText(String prompt) {
        System.out.print(prompt + ": ");
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.exit(0); // input closed
        }
        return scanner.nextLine().replace("﻿", "").trim();
    }
}
