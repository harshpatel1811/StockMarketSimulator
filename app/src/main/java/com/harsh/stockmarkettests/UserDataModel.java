package com.harsh.stockmarkettests;

public class UserDataModel {
    String email, pass, watchlist, holdings;
    float funds;

    public UserDataModel(String email, String pass, String watchlist, String holdings, float funds) {
        this.email = email;
        this.pass = pass;
        this.watchlist = watchlist;
        this.holdings = holdings;
        this.funds = funds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(String watchlist) {
        this.watchlist = watchlist;
    }

    public String getHoldings() {
        return holdings;
    }

    public void setHoldings(String holdings) {
        this.holdings = holdings;
    }
}
