# Financial Trading Platform

Final project for **Software Design Patterns**: a console trading platform written in Java (17+).
Bots subscribe to a simulated live market, run interchangeable trading algorithms, and send orders to
two simulated exchanges with incompatible APIs through one unified interface.

The project uses **7 GoF patterns**, covering all three categories.

| # | Pattern | Type | Where | Role in the project | Owner            |
|---|---------|------|-------|---------------------|------------------|
| 1 | **Observer** | Behavioral | `marketdata/` | `MarketDataFeed` pushes price ticks to subscribers: `PriceBook`, `TickerDisplay`, `TradingBot` | Altyn            |
| 2 | **Strategy** | Behavioral | `strategy/`, `bot/TradingBot` | Trading algorithms (MA crossover, momentum, mean reversion) that can be swapped at runtime | Altyn            |
| 3 | **Adapter** | Structural | `exchange/adapter/`, `exchange/external/` | `BinanceAdapter` and `NasdaqAdapter` wrap incompatible third-party SDKs behind `ExchangeConnector` | Yerketay         |
| 4 | **Factory Method** | Creational | `exchange/factory/` | `ExchangeConnectorFactory.createConnector()`: subclasses decide which connector to build | Yerketay         |
| 5 | **Builder** | Creational | `model/Order` | `Order.builder()...build()` builds validated, immutable orders | Dinmukhammed     |
| 6 | **Decorator** | Structural | `exchange/decorator/` | Logging and risk-limit layers stacked around any connector | Dinmukhammed     |
| 7 | **Facade** | Structural | `facade/TradingPlatformFacade` | One simple API over the market feed, exchanges, bots and portfolio | Dinmukhammed         |

## How the patterns work together

```
MarketSimulator --publish--> MarketDataFeed (Observer: Subject)
                                   |-- PriceBook        (Observer)
                                   |-- TickerDisplay    (Observer)
                                   '-- TradingBot       (Observer + Strategy Context)
                                          | strategy.evaluate(history) -> BUY / SELL / HOLD   (Strategy)
                                          | Order.builder()...build()                         (Builder)
                                          v
                                     OrderExecutor -- Portfolio check
                                          v
             Logging( RiskLimit( BinanceAdapter( BinanceRestClient ) ) )   (Decorator + Adapter)
                     ^ created by BinanceConnectorFactory.createConnector()  (Factory Method)

TradingPlatformFacade (Facade) wires everything and is the only class the UI talks to.
```

## Run

You need JDK 17 or newer. No external libraries.

```bash
./run.sh --demo        # scripted demo of all patterns (best for screenshots)
./run.sh               # interactive menu
./test.sh              # 27 self-checks, one group per pattern
```

On Windows use `run.bat --demo`, `run.bat`, `test.bat`.
In IntelliJ IDEA, open the folder (or `pom.xml`) and run `com.trading.Main`. Add `--demo` to the program arguments for the demo.

## Project structure

```
src/main/java/com/trading/
  Main.java                    entry point (--demo or interactive)
  app/                         ConsoleApp (menu), DemoScenario
  facade/                      TradingPlatformFacade, PlatformSettings        [Facade]
  marketdata/                  MarketDataFeed, MarketDataListener, PriceBook,
                               TickerDisplay, MarketSimulator                  [Observer]
  strategy/                    TradingStrategy + 3 algorithms, PriceHistory   [Strategy]
  bot/                         TradingBot (Observer + Strategy context)
  model/                       Order (+Builder), ExecutionReport, MarketTick  [Builder]
  exchange/ExchangeConnector   common interface (Target / Component / Product)
  exchange/external/           simulated third-party SDKs (Adaptees)
  exchange/adapter/            BinanceAdapter, NasdaqAdapter                   [Adapter]
  exchange/decorator/          Logging / RiskLimit decorators                  [Decorator]
  exchange/factory/            ExchangeConnectorFactory + 2 creators           [Factory Method]
  execution/                   OrderExecutor (one path for every order)
  portfolio/                   Portfolio, PortfolioReportFormatter
  common/                      PlatformLogger, Formats
src/test/java/com/trading/PatternSelfTest.java
docs/uml/                      PlantUML class and sequence diagrams
```

## Design principles applied

- **S**ingle Responsibility: the feed only distributes ticks, strategies only decide, `Portfolio` only keeps the books, `PortfolioReportFormatter` only formats, decorators each add one concern.
- **O**pen/Closed: a new strategy is one class plus one `StrategyCatalog.register` call. A new exchange is an adapter plus a factory registered with `registerExchange`. A new cross-cutting concern is a new decorator. No existing class has to change.
- **L**iskov Substitution: any `ExchangeConnector` (raw adapter or decorated) and any `TradingStrategy` can replace another without the caller noticing.
- **I**nterface Segregation: subscribers see `MarketDataSubject` (subscribe/unsubscribe) without `publish`. Exchanges read prices through the narrow `PriceProvider`.
- **D**ependency Inversion: `TradingBot` depends on `TradingStrategy`, `OrderExecutor` on `ExchangeConnector`, and components log through `PlatformLogger` instead of `System.out`.
- **DRY**: shared adapter logic is in `AbstractExchangeAdapter`, price statistics in `PriceHistory`, number formatting in `Formats`, and the order pipeline in `OrderExecutor`.
- **KISS**: single-threaded, deterministic simulation (fixed seed), no frameworks.

## Further work

- Real exchange APIs (REST/WebSocket) as new adapters.
- Order book with partial fills and pending limit orders.
- Persistence of trades (database) and a GUI or web dashboard.
- Back-testing strategies on historical data.
