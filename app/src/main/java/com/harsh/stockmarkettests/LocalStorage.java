package com.harsh.stockmarkettests;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.common.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//LocalStorage Management for Portfolio and Funds

// NumberFormat.getNumberInstance(Locale.FRANCE).parse("265,858")
//NumberFormat.getNumberInstance(java.util.Locale.US).parse("265,858")
public class LocalStorage {

    Context context;

    public LocalStorage(Context context)
    {
      //  localstorage = sharedPreferences;
        this.context = context;
    }

    public void fillScript(String ticker, int qty, String buyprice)
    {
        if(isPortfolioFileExist())
        {
            try {
                Number buy_price  = NumberFormat.getNumberInstance(Locale.US).parse(buyprice);
                ticker = String.valueOf(buy_price.doubleValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String str = sharedPreferences.getString("Stocks", null);
            if(str!= null)
            {
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Ticker", ticker);
                    jsonObject.put("QTY", qty);
                    jsonObject.put("Buy Price", buyprice);
                    jsonArray.put(jsonObject);
                    editor.putString("Stocks", jsonArray.toString());
                    editor.apply();
                    //Toast.makeText(context, ticker + " Bought", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }
        else
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Ticker", ticker);
                jsonObject.put("QTY", qty);
                jsonObject.put("Buy Price", buyprice);
                jsonArray.put(jsonObject);
                editor.putString("Stocks", jsonArray.toString());
                editor.apply();
                Toast.makeText(context, ticker + "Bought", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) { e.printStackTrace(); }
        }
    }

    public void addToExistingScrip(String ticker, int qty, String buyprice)
    {
        //TODO;- Complete the function
    }

    public boolean isPortfolioFileExist()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("Stocks", null);
        if(str != null)
            return true;
        else
            return false;
    }

    public boolean isScripExist(String ticker) {

        if(isPortfolioFileExist())
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
            String str = sharedPreferences.getString("Stocks", null);

            try {
                JSONArray jsonArray = new JSONArray(str);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = jsonArray.getJSONObject(i);
                    String ticker1 = jsonObject.getString("Ticker");
                    if(ticker1.equals(ticker))
                         return true;
                }

            } catch (JSONException e) { e.printStackTrace(); }
        }
        else
            return false;

        return false;
    }

    public List<PortfolioStock> getPortfolioStocks()
    {
        List<PortfolioStock> stockList = new ArrayList<>();
        if(isPortfolioFileExist())
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
            String str = sharedPreferences.getString("Stocks", null);

            try {
                JSONArray jsonArray = new JSONArray(str);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).toString());
                    PortfolioStock portfolioStock  = new PortfolioStock();
                    portfolioStock.setTicker(jsonObject.getString("Ticker"));
                    portfolioStock.setTickerAvgBuy(jsonObject.getString("Buy Price"));
                    portfolioStock.setTickerQTY(jsonObject.getInt("QTY"));
                    stockList.add(portfolioStock);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            return stockList;
    }

    public boolean isWatchlistFileExists()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Stocks", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("WATCHLIST", null);
        if(str == null)
            return false;
        else
            return true;

    }
    public List<stock> getWatchlistStocks()
    {
        List<stock> stockList = new ArrayList<>();
        if(isWatchlistFileExists())
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Stocks", Context.MODE_PRIVATE);
            String str = sharedPreferences.getString("WATCHLIST", null);

            try {
                JSONArray jsonArray = new JSONArray(str);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    stock st = new stock("-", jsonArray.get(i).toString(), "-");
                    stockList.add(st);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return stockList;
        }
        else
            Toast.makeText(context, "No data in Watchlist", Toast.LENGTH_SHORT).show();

        return stockList;
    }

}
