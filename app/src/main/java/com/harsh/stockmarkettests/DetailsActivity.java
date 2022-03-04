package com.harsh.stockmarkettests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.harsh.stockmarkettests.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    public static final String PRICE_URL = "https://money.rediff.com/money1/currentstatus.php?companycode=";
    TextView tvStockName, tvStockChange, tvStockChangePercent,tvstockprice,
            tvlow,tvhigh,tvprevclose,tvmarketcap,tvvolume,tv52weekhigh, tv52weeklow;
    ImageView ivstartblank;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    Button tradebutton;
    String ticker, tickerName;
    SharedPreferences localstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Getting String from search query
        Intent intent = getIntent();
        ticker = intent.getStringExtra(HomeFragment.EXTRA_TICKER);
        tickerName = intent.getStringExtra(HomeFragment.EXTRA_TICKERNAME);
        setTitle(ticker);
        progressBar = findViewById(R.id.progressBar2);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        tvStockName = findViewById(R.id.tv_stockname);
        tvstockprice = findViewById(R.id.tv_stockPrice);
        tvStockChange = findViewById(R.id.tv_details_stock_change);
        tvStockChangePercent = findViewById(R.id.tv_details_stock_change_percent);
        tvlow = findViewById(R.id.tv_low);
        tvhigh = findViewById(R.id.tv_high);
        tvprevclose = findViewById(R.id.tv_prev_close);
        tvmarketcap = findViewById(R.id.tv_marketcap);
        tvvolume = findViewById(R.id.tv_volume);
        tv52weekhigh = findViewById(R.id.tv_52weekhigh);
        tv52weeklow = findViewById(R.id.tv_52weeklow);
        ivstartblank = findViewById(R.id.ivstartblank);

        nestedScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        makeApiCallPrice(ticker);

        if(istickerexist(ticker))
            ivstartblank.setImageResource(R.drawable.ic_baseline_star_rate_24);

        //  700005  700002
        ivstartblank.setOnClickListener(new View.OnClickListener() {

            Drawable star_border = getResources().getDrawable( R.drawable.ic_baseline_star_border_24);
            Drawable start_filled = getResources().getDrawable(R.drawable.ic_baseline_star_rate_24);
            @Override
            public void onClick(View view) {
             if(ivstartblank.getDrawable().getConstantState().equals(star_border.getConstantState()))
                 ivstartblank.setImageResource(R.drawable.ic_baseline_star_rate_24);
             else
                 ivstartblank.setImageResource(R.drawable.ic_baseline_star_border_24);

            }
        });

    }


    private void makeApiCallPrice(String ticker) {

        ApiCall.make(this, ticker, PRICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject row = new JSONObject(response);
                    //  JSONArray array = responseObject.getJSONArray("results");
                    //  Log.i("length:", "len: " + array.length());
                    //   for (int i = 0; i < array.length(); i++) {
                    //TODO:- Stock details here
                    //  JSONObject row = array.getJSONObject(i);
                    String last = row.getString("LastTradedPrice");
                    String prevClose = row.getString("PrevClose");
                    String low = row.getString("Low");
                    String high = row.getString("High");
                    String marketcap = row.getString("MarketCap");
                    String high52week = row.getString("FiftyTwoWeekHigh");
                    String low52week = row.getString("FiftyTwoWeekLow");
                    String change = row.getString("Change");
                    String changepercent = row.getString("ChangePercent");
                    String volume = row.getString("Volume");


                    tvStockName.setText(tickerName);

                    if(change.startsWith("-"))
                    {
                        tvstockprice.setText("₹ "+last);
                        tvStockChange.setText(change);
                        tvStockChangePercent.setText("("+changepercent+"%)");
                    }
                    else
                    {
                        tvstockprice.setTextColor(getResources().getColor(R.color.green_stock));
                        tvstockprice.setText("₹"+last);
                        tvStockChange.setTextColor(getResources().getColor(R.color.green_stock));
                        tvStockChange.setText(change);
                        tvStockChangePercent.setTextColor(getResources().getColor(R.color.green_stock));
                        tvStockChangePercent.setText("("+changepercent+"%)");
                    }


                    tvprevclose.setText(prevClose);

                    tvmarketcap.setText(marketcap);
                    tvvolume.setText(volume);
                    tvhigh.setText(high);
                    tvlow.setText(low);
                    tv52weekhigh.setText(high52week);
                    tv52weeklow.setText(low52week);
                    progressBar.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Log.i("Lol", e.toString());
                    e.printStackTrace();
                }

            }
        }, (Response.ErrorListener) error -> {
            Log.i("error", "error in search http " + error);
        });
    }

    public boolean istickerexist(String ticker)
    {
        localstorage = getSharedPreferences("Stocks", Context.MODE_PRIVATE);
        String data = localstorage.getString("WATCHLIST", null);
        if(data!= null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(data);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    if(jsonArray.get(i).equals(ticker))
                        return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }
}