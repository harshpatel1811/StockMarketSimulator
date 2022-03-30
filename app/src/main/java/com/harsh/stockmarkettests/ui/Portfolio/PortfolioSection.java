package com.harsh.stockmarkettests.ui.Portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.harsh.stockmarkettests.ApiCall;
import com.harsh.stockmarkettests.LocalStorage;
import com.harsh.stockmarkettests.PortfolioRecyclerAdapter;
import com.harsh.stockmarkettests.PortfolioStock;
import com.harsh.stockmarkettests.R;
import com.harsh.stockmarkettests.databinding.FragmentNotificationsBinding;
import com.harsh.stockmarkettests.stock;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PortfolioSection extends Fragment {
    public static final String PRICE_URL = "https://money.rediff.com/money1/currentstatus.php?companycode=";
    TextView current_value, total_invested, tvpnl;
    public List<PortfolioStock> stockList;
    RecyclerView recyclerView;
    PortfolioRecyclerAdapter portfolioRecyclerAdapter;
    LocalStorage localStorage;
    ArrayList<Double> arrayList;
    public double g_pnl = 0;
    public double g_current_vlaue = 0;
    public double g_total_value = 0;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        total_invested  = view.findViewById(R.id.totalinvestment);
        current_value = view.findViewById(R.id.currentvalue);
        stockList = new ArrayList<>();
        localStorage = new LocalStorage(getContext());
        stockList = localStorage.getPortfolioStocks();
        tvpnl = view.findViewById(R.id.overallpnl);
        arrayList = new ArrayList<>();

        for(int i=0; i < stockList.size(); i++)
        {
            makeApiCallPrice(stockList.get(i).getTicker(), i);
        }
        recyclerView = view.findViewById(R.id.recyclerview_portfolio);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        portfolioRecyclerAdapter = new PortfolioRecyclerAdapter(stockList, getContext());
        recyclerView.setAdapter(portfolioRecyclerAdapter);
      // double pnl =  portfolioRecyclerAdapter.getPnl();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void makeApiCallPrice(String ticker,int position)
    {
        ApiCall.make(getContext(), ticker, PRICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String lastprice = null;
                try {
                    lastprice = jsonObject.getString("LastTradedPrice");
                    String change = jsonObject.getString("Change");
                    String changeinPercent = jsonObject.getString("ChangePercent");
                    String finalchange = change + " (" + changeinPercent + "%)";
                    stock st = new stock(lastprice, ticker, finalchange);

                    lastprice = lastprice.replace("₹", "");
                    String buyprice = stockList.get(position).getTickerAvgBuy();
                    int qty = stockList.get(position).getTickerQTY();

                     Number number_lastprice = NumberFormat.getNumberInstance(java.util.Locale.US).parse(lastprice);
                     buyprice = buyprice.replace("₹", "");
                     Number number_buyprice = NumberFormat.getNumberInstance(java.util.Locale.US).parse(buyprice);

                     double investment_value = number_buyprice.doubleValue() * qty;
                     g_total_value = g_total_value + investment_value;
                     total_invested.setText("₹"+ String.format(Locale.US, "%,.2f", g_total_value));
                    double pnl = qty * (number_lastprice.doubleValue() - number_buyprice.doubleValue());
                    g_pnl = g_pnl + pnl;

                    g_current_vlaue = g_total_value + g_pnl;
                    current_value.setText("₹"+String.format(Locale.ENGLISH, "%,.2f",g_current_vlaue));
                    if(g_pnl < 0)
                        tvpnl.setTextColor(getResources().getColor(R.color.red_stock));
                    else
                        tvpnl.setTextColor(getResources().getColor(R.color.green_stock));

                    tvpnl.setText(String.format(Locale.ENGLISH, "%,.2f",g_pnl));
                    stockList.get(position).setTickerPnL(String.format("%.2f", pnl));
                    stockList.get(position).setTickerPriceLTP(lastprice);
                    portfolioRecyclerAdapter.notifyItemChanged(position);


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public int getPositionOfScrip(String ticker , List<PortfolioStock> stockList)
    {
        if(stockList.size() ==1)
            return 0;

        for(int i=0; i < stockList.size(); i++)
        {
            if(stockList.get(i).getTicker().equals(ticker))
                return i;
        }
        return 0;
    }

}