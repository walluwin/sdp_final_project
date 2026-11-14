# Financial Trading Platform

Final project for **Software Design Patterns**. It is a console trading platform in Java (17+).
Trading bots get live prices from a simulated market. Each bot uses its own trading algorithm and
sends orders to one of two simulated exchanges (BINANCE for crypto, NASDAQ for stocks).
The two exchanges have different APIs but work through one common interface.

The project uses **7 design patterns**, at least one of each type.

| # | Pattern | Type | Classes | What it does in the project | Author |
|---|---------|------|---------|-----------------------------|--------|
| 1 | **Observer** | Behavioral | `marketdata/MarketDataFeed`, `MarketDataListener`, `PriceDisplay` | The market sends every new price to all subscribers (bots, price display) | Altyn |
| 2 | **Strategy** | Behavioral | `strategy/*`, `bot/TradingBot` | Three trading algorithms. A bot can change its algorithm while it runs | Altyn |
| 3 | **Adapter** | Structural | `exchange/BinanceAdapter`, `exchange/NasdaqAdapter`, `exchange/api/*` | Turns two different exchange APIs into our `Exchange` interface | Yerketay |
| 4 | **Factory Method** | Creational | `factory/*` | `createExchange()`: each factory subclass creates its own exchange | Yerketay |
| 5 | **Builder** | Creational | `model/Order` | `new Order.Builder()...build()` creates an order step by step and checks it | Dinmukhammed |
| 6 | **Decorator** | Structural | `decorator/*` | Adds logging and a risk limit around any exchange without changing it | Dinmukhammed |
| 7 | **Facade** | Structural | `facade/TradingPlatform` | One simple class for the whole platform. The menu uses only this class | Dinmukhammed |

## How the patterns work together

```
MarketDataFeed (Observer: subject)
   |-- PriceDisplay       (observer, prints prices)
   '-- TradingBot         (observer)
          |  strategy.decide(prices) -> BUY / SELL / HOLD          (Strategy)
          |  new Order.Builder()...build()                         (Builder)
          v
      OrderService  (checks money, sends the order, saves the trade in Portfolio)
          v
      LoggingExchange( RiskLimitExchange( BinanceAdapter( BinanceApi ) ) )   (Decorator + Adapter)
          ^ created by BinanceFactory.createExchange()                       (Factory Method)

TradingPlatform (Facade) connects all the parts. Main (the menu) talks only to it.
```

## How to run

You need JDK 17 or newer. No libraries are needed.

Windows (PowerShell):
```
.\run.bat --demo     automatic demo of all patterns
.\run.bat            menu
.\test.bat           checks for every pattern
```
Linux / macOS: `./run.sh --demo`, `./run.sh`, `./test.sh`

IntelliJ IDEA: open the folder and run `com.trading.Main`. For the demo, add `--demo` in Run → Edit Configurations → Program arguments.

## Project structure

```
src/main/java/com/trading/
  Main.java                  console menu
  Demo.java                  automatic demo
  model/                     Order (+ Builder), OrderResult, OrderSide, OrderType      [Builder]
  marketdata/                MarketDataFeed, MarketDataListener, PriceDisplay          [Observer]
  strategy/                  TradingStrategy, 3 strategies, Signal, PriceUtils          [Strategy]
  bot/                       TradingBot (Observer + Strategy)
  exchange/                  Exchange interface, BinanceAdapter, NasdaqAdapter         [Adapter]
  exchange/api/              BinanceApi, NasdaqApi (the "foreign" exchange libraries)
  decorator/                 ExchangeDecorator, LoggingExchange, RiskLimitExchange      [Decorator]
  factory/                   ExchangeFactory, BinanceFactory, NasdaqFactory             [Factory Method]
  service/                   OrderService (every order goes through it)
  portfolio/                 Portfolio (cash, positions, trades)
  facade/                    TradingPlatform                                            [Facade]
src/test/java/com/trading/PatternSelfTest.java
docs/uml/                    UML diagrams (PlantUML)
```

## Clean code principles

- **S (Single Responsibility).** Each class has one job: `MarketDataFeed` sends prices, strategies decide, `Portfolio` keeps money and positions, `OrderService` executes orders, and each decorator adds one feature.
- **O (Open/Closed).** A new strategy is a new class. A new exchange is a new adapter plus a new factory. A new feature for exchanges is a new decorator. Old classes do not change.
- **L (Liskov Substitution).** Any `Exchange` (plain adapter or decorated) and any `TradingStrategy` can be used in place of another.
- **I (Interface Segregation).** Interfaces are small: `MarketDataListener` has one method, `TradingStrategy` has two.
- **D (Dependency Inversion).** `TradingBot` depends on the `TradingStrategy` interface, `OrderService` on the `Exchange` interface, and the feed on `MarketDataListener`, not on concrete classes.
- **DRY.** All order checks are only in `OrderService`, the average price is only in `PriceUtils`, and delegation is only in `ExchangeDecorator`.
- **KISS.** No frameworks and no threads. The market uses a fixed random seed, so every run gives the same result.

## Further work

- Connect real exchange APIs as new adapters.
- Keep limit orders waiting until the price is reached (an order book).
- Save trades to a database.
- Add a graphical or web interface.
- Test strategies on historical prices.
