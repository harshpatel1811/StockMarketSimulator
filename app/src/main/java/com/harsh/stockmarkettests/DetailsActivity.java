package com.harsh.stockmarkettests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.harsh.stockmarkettests.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class DetailsActivity extends AppCompatActivity {

    public static final String PRICE_URL = "https://money.rediff.com/money1/currentstatus.php?companycode=";
    public static final int AUTO_REFRESH_PERIOD_MSEC = 5000;
    TextView tvStockName, tvStockChange, tvStockChangePercent,tvstockprice,
            tvlow,tvhigh,tvprevclose,tvmarketcap,tvvolume,tv52weekhigh, tv52weeklow;
    ToggleButton toggleButton;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    Button tradebutton;
    String ticker, tickerName;
    SharedPreferences localstorage;
    Timer timer;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Getting String from search query
        DataViewModel dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        Intent intent = getIntent();
        ticker = intent.getStringExtra(HomeFragment.EXTRA_TICKER);
        tickerName = intent.getStringExtra(HomeFragment.EXTRA_TICKERNAME);
        setTitle(ticker);
        tradebutton = findViewById(R.id.trade_btn);
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
       // ivstarblank = findViewById(R.id.ivstartblank);
        toggleButton = findViewById(R.id.toggleButton);



        File file = new File("/data/data/com.harsh.stockmarkettests/shared_prefs/Stocks.xml");
        if(!file.exists())
        {
            localstorage = getSharedPreferences("Stocks", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = localstorage.edit();
            editor.putString("WATCHLIST","[]");
        }

        nestedScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        makeApiCallPrice(ticker);

        if(istickerexist(ticker))
            toggleButton.toggle();


        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(toggleButton.isChecked())
               {
                   Toast.makeText(DetailsActivity.this, "Added to Favourited", Toast.LENGTH_SHORT).show();
                   addToWatchlist(ticker);
               }
               else
               {
                   Toast.makeText(DetailsActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                   removeFromWatchlist(ticker);
               }
            }
        });

        tradebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new bottom_sheet_trade();
                bottomSheetDialogFragment.show(getSupportFragmentManager(),bottomSheetDialogFragment.getTag());
                dataViewModel.setBottomSheetDialogFragment(bottomSheetDialogFragment);
                dataViewModel.setTicker(ticker);
                String finalchange = tvStockChange.getText().toString() + " " + tvStockChangePercent.getText().toString();
                dataViewModel.setTickerprice(tvstockprice.getText().toString());
                dataViewModel.setTickerpricechange(finalchange);
                tvstockprice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String finalchange = tvStockChange.getText().toString() + " " + tvStockChangePercent.getText().toString();
                        dataViewModel.setTickerprice(tvstockprice.getText().toString());
                        dataViewModel.setTickerpricechange(finalchange);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


            }
        });

        timer = new Timer();
        counter = 0;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                makeApiCallPrice(ticker);
            }
        }, 0, AUTO_REFRESH_PERIOD_MSEC);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void makeApiCallPrice(String ticker) {

        ApiCall.make(this, ticker, PRICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject row = new JSONObject(response);
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
                        tvstockprice.setText("₹"+last);
                        tvStockChange.setText(change);
                        tvStockChangePercent.setText("("+changepercent+"%)");
                    }
                    else
                    {
                        tvstockprice.setTextColor(getResources().getColor(R.color.green_stock));
                        tvstockprice.setText("₹"+last);
                        tvStockChange.setTextColor(getResources().getColor(R.color.green_stock));
                        tvStockChange.setText("+" + change);
                        tvStockChangePercent.setTextColor(getResources().getColor(R.color.green_stock));
                        tvStockChangePercent.setText("("+changepercent+"%)");
                    }


                    tvprevclose.setText("₹"+prevClose);
                    tvmarketcap.setText(marketcap+" Cr");
                    tvvolume.setText(volume);
                    tvhigh.setText("₹"+high);
                    tvlow.setText("₹"+low);
                    tv52weekhigh.setText("₹"+high52week);
                    tv52weeklow.setText("₹"+low52week);
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
            try {
           JSONArray jsonArray = new JSONArray(data);
            for(int i = 0; i< jsonArray.length(); i++)
            {
                if(jsonArray.getString(i).equals(ticker))
                    return true;
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public void addToWatchlist(String ticker)
    {
        Toast.makeText(this, "Running", Toast.LENGTH_SHORT).show();

     //  localstorage = getSharedPreferences("Stocks", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = localstorage.edit();
       String data = localstorage.getString("WATCHLIST", null);

       if(data != null)
       {
           try {
               JSONArray jsonArray = new JSONArray(data);
               jsonArray.put(ticker);
               editor.putString("WATCHLIST", jsonArray.toString());
               editor.apply();
               //
               // Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show();
           } catch (JSONException e) {
               Log.d("WATCHLIST", "addToWatchlist: "+ e.toString());
               e.printStackTrace();
           }
       }
       else
           Toast.makeText(this, "Data Adding Failed", Toast.LENGTH_SHORT).show();

    }

    public void removeFromWatchlist(String ticker) {
        SharedPreferences.Editor editor = localstorage.edit();
        String data = localstorage.getString("WATCHLIST", null);
        if(data!= null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(data);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    if(jsonArray.get(i).equals(ticker))
                    {
                        jsonArray.remove(i);
                        editor.putString("WATCHLIST", jsonArray.toString());
                        editor.apply();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}