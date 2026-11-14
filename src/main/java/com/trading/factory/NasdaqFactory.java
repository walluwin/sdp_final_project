package com.trading.factory;

import com.trading.exchange.Exchange;
import com.trading.exchange.NasdaqAdapter;
import com.trading.exchange.api.NasdaqApi;
import com.trading.marketdata.MarketDataFeed;

/** Concrete Creator: creates the NASDAQ exchange. */
public class NasdaqFactory extends ExchangeFactory {

    private final MarketDataFeed market;

    public NasdaqFactory(MarketDataFeed market) {
        this.market = market;
    }

    @Override
    public String getExchangeName() {
        return "NASDAQ";
    }

    @Override
    protected Exchange createExchange() {
        return new NasdaqAdapter(new NasdaqApi(market));
    }
}
