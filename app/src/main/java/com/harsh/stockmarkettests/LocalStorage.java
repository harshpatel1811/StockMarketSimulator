package com.harsh.stockmarkettests;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.harsh.stockmarkettests.ui.Portfolio.PortfolioSection;
import com.harsh.stockmarkettests.ui.dashboard.MoversRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public float getFundsvalue()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
          float fund = sharedPreferences.getFloat("Fund", -1);
            if(fund != -1)
                return fund;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("Fund", 1000000);
            editor.apply();
            return  1000000;


    }

    public void modiftyFundsBuy(float amount) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        float fund = sharedPreferences.getFloat("Fund", -1);
        if(fund != -1)
        {
            float remaning_balance = fund - amount;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Toast.makeText(context, String.valueOf(remaning_balance), Toast.LENGTH_SHORT).show();
            editor.remove("Fund");
            editor.putFloat("Fund", remaning_balance);
            editor.apply();
        }
        else
        {
            Toast.makeText(context.getApplicationContext(), "Somethings wwrong with modiftyfund() function", Toast.LENGTH_SHORT).show();
        }
      //  Toast.makeText(context, "Funds modified", Toast.LENGTH_SHORT).show();
    }

    public void averagePrice(String scrip, double tickerprice, int qty)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("Stocks", null);
        if(str != null)
        {
            try {
                JSONArray jsonArray = new JSONArray(str);
                JSONArray newJsonArray = new JSONArray();
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());

                    if(jsonObject.getString("Ticker").equals(scrip))
                    {
                       int m_qty =jsonObject.getInt("QTY");
                       String m_buyprice = jsonObject.getString("Buy Price").substring(1);
                       Number number = NumberFormat.getNumberInstance().parse(m_buyprice);
                       double prev_cost = number.doubleValue() * m_qty;
                       int newqty = m_qty + qty;
                       double new_cost = tickerprice * qty;
                       double avg_price = (new_cost + prev_cost) / newqty;


                       JSONObject newJsonObjet = new JSONObject();
                       newJsonObjet.put("Ticker", scrip);
                       newJsonObjet.put("QTY", newqty);
                       newJsonObjet.put("Buy Price","₹" + String.format(Locale.ENGLISH, "%,.2f", avg_price));
                       newJsonArray.put(newJsonObjet);
                    }

                    else
                    {
                        JSONObject newJsonObjet = new JSONObject();
                        newJsonObjet.put("Ticker", jsonObject.getString("Ticker"));
                        newJsonObjet.put("QTY", jsonObject.getInt("QTY"));
                        newJsonObjet.put("Buy Price", jsonObject.getString("Buy Price"));
                        newJsonArray.put(newJsonObjet);
                    }
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Stocks");
                editor.putString("Stocks", newJsonArray.toString());
                editor.apply();

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public Object[] getPortFolioData(String ticker)
    {
        Object object[] = new Object[3];
        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        String str =  sharedPreferences.getString("Stocks", null);
       if(str!= null)
       {
           try {
               JSONArray jsonArray = new JSONArray(str);
               for(int i=0; i < jsonArray.length(); i++)
               {
                   JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                   if(jsonObject.getString("Ticker").equals(ticker))
                   {
                       object[0] = ticker;
                       object[1] = jsonObject.getInt("QTY");
                       object[2] = jsonObject.getString("Buy Price");
                   }
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       else
           Toast.makeText(context, "Something wrong with getportfoliodata() function", Toast.LENGTH_SHORT);

       return object;
    }


    public void modiftyFundsSell(String ticker, int qty, float lastprice, int position) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String str = sharedPreferences.getString("Stocks", null);
        float funds = sharedPreferences.getFloat("Fund", -1);
        if(str!=null)
        {
            //sell
            try {
                JSONArray jsonArray = new JSONArray(str);
                JSONArray jsonArrayNew = new JSONArray();
               // Number number = NumberFormat.getNumberInstance().parse(lastprice.substring(1));
                for(int i=0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject jsonObjectNew = new JSONObject();
                    if (jsonObject.getString("Ticker").equals(ticker))
                    {
                        if (jsonObject.getInt("QTY") != qty)
                        {
                            //Sells less than total qty
                            Number buy_price = NumberFormat.getNumberInstance().parse(jsonObject.getString("Buy Price").substring(1));
                            int totalqty = jsonObject.getInt("QTY");
                            float modifiedfund = qty * lastprice;
                            jsonObjectNew.put("Ticker", ticker);
                            jsonObjectNew.put("QTY", totalqty-qty);
                            jsonObjectNew.put("Buy Price", jsonObject.getString("Buy Price"));
                            editor.remove("Fund");
                            editor.putFloat("Fund", funds + modifiedfund);
                            editor.apply();
                            jsonArrayNew.put(jsonObjectNew);
                            PortfolioSection.stockList.get(i).setTickerQTY(totalqty-qty);
                        }
                        else
                        {
                            int totalqty = jsonObject.getInt("QTY");
                            float modifiedfund = totalqty * lastprice;
                            editor.remove("Fund");
                            editor.putFloat("Fund", (funds + modifiedfund));
                            editor.apply();
                            PortfolioSection.stockList.remove(position);
                        }
                    }
                    else
                    {
                        jsonObjectNew.put("Ticker", jsonObject.getString("Ticker"));
                        jsonObjectNew.put("QTY", jsonObject.getInt("QTY"));
                        jsonObjectNew.put("Buy Price", jsonObject.getString("Buy Price"));
                        jsonArrayNew.put(jsonObjectNew);
                    }
                }
                editor.remove("Stocks");
                editor.putString("Stocks", jsonArrayNew.toString());
                editor.apply();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(context, "something wrong in modifyFundsSell()", Toast.LENGTH_SHORT).show();

    }

    public int getQTY(String ticker)
    {
        int qty = 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("Stocks", null);
        if(str!= null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(str);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    if(jsonObject.getString("Ticker").equals(ticker))
                        return jsonObject.getInt("QTY");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return qty;
    }

    public int getTickerPosition(String ticker)  {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Portfolio", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("Stocks", null);
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(str);
            for(int i=0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                if(jsonObject.getString("Ticker").equals(ticker))
                    return i;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return 0;
    }
}
