package com.trading.factory;

import com.trading.exchange.BinanceAdapter;
import com.trading.exchange.Exchange;
import com.trading.exchange.api.BinanceApi;
import com.trading.marketdata.MarketDataFeed;

/** Concrete Creator: creates the Binance exchange. */
public class BinanceFactory extends ExchangeFactory {

    private final MarketDataFeed market;

    public BinanceFactory(MarketDataFeed market) {
        this.market = market;
    }

    @Override
    public String getExchangeName() {
        return "BINANCE";
    }

    @Override
    protected Exchange createExchange() {
        return new BinanceAdapter(new BinanceApi(market));
    }
}
