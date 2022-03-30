package com.harsh.stockmarkettests;

public class PortfolioStock {

    private String ticker;
    private String tickerPriceLTP;
    private String tickerPriceChangeToLTP;
    private String tickerPriceChangePercentToLTP;
    private String tickerPnL;
    private int tickerQTY;
    private String tickerInvested;
    private String tickerAvgBuy;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTickerPriceLTP() {
        return tickerPriceLTP;
    }

    public void setTickerPriceLTP(String tickerPriceLTP) {
        this.tickerPriceLTP = tickerPriceLTP;
    }

    public String getTickerPriceChangeToLTP() {
        return tickerPriceChangeToLTP;
    }

    public void setTickerPriceChangeToLTP(String tickerPriceChangeToLTP) {
        this.tickerPriceChangeToLTP = tickerPriceChangeToLTP;
    }
    public void setTickerInvested(String tickerInvested)
    {
        this.tickerInvested = tickerInvested;
    }

    public String getTickerPriceChangePercentToLTP() {
        return tickerPriceChangePercentToLTP;
    }

    public void setTickerPriceChangePercentToLTP(String tickerPriceChangePercentToLTP) {
        this.tickerPriceChangePercentToLTP = tickerPriceChangePercentToLTP;
    }

    public String getTickerPnL() {
        return tickerPnL;
    }

    public void setTickerPnL(String tickerPnL) {
        this.tickerPnL = tickerPnL;
    }

    public int getTickerQTY() {
        return tickerQTY;
    }

    public void setTickerQTY(int tickerQTY) {
        this.tickerQTY = tickerQTY;
    }

    public String getTickerAvgBuy() {
        return tickerAvgBuy;
    }

    public void setTickerAvgBuy(String tickerAvgBuy) {
        this.tickerAvgBuy = tickerAvgBuy;
    }

    public String getTickerInvested()
    {
        return tickerInvested;
    }
}
