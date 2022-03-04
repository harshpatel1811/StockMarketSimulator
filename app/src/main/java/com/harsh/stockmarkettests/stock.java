package com.harsh.stockmarkettests;

public class stock {

    private String lastprice;
    private String ticker;
    private String change;

    public stock(String lastprice, String ticker, String change)
    {
        this.lastprice = lastprice;
        this.ticker = ticker;
        this.change= change;
    }

    public String getLastprice() {
        return lastprice;
    }

    public String getTicker() {
        return ticker;
    }

    public String getChange() {return change;}

}
