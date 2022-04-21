package com.harsh.stockmarkettests.ui.Portfolio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PortfolioSection extends Fragment {
    public static final String PRICE_URL = "https://money.rediff.com/money1/currentstatus.php?companycode=";
    public static final int AUTO_REFRESH_PERIOD_MSEC = 10000;
    public static TextView current_value, total_invested, tvpnl, tv_total_return, tv_funds;
    public static List<PortfolioStock> stockList;
    RecyclerView recyclerView;
    static PortfolioRecyclerAdapter portfolioRecyclerAdapter;
    LocalStorage localStorage;
    ArrayList<Double> arrayList;
    public  double g_pnl = 0;
    public  double g_current_vlaue = 0;
    public  double g_total_value = 0;
    public  double g_total_returns = 0;
    ArrayList<Double> pnlprice;
    ArrayList<Double> g_investment_value;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    public static float funds = 0;
    public static Timer timer;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        total_invested  = view.findViewById(R.id.totalinvestment);
        current_value = view.findViewById(R.id.currentvalue);
        tv_total_return =view.findViewById(R.id.tv_total_return);
        stockList = new ArrayList<>();
        localStorage = new LocalStorage(getContext());
        stockList = localStorage.getPortfolioStocks();
        tvpnl = view.findViewById(R.id.overallpnl);
        tv_funds = view.findViewById(R.id.tv_funds);
        pnlprice = new ArrayList<>();
        g_investment_value = new ArrayList<>();
        for(int i=0; i< stockList.size(); i++)
        {
            pnlprice.add(i, 0.00);
            g_investment_value.add(i, 0.00);
        }

        tv_funds.setText("₹"+String.format(Locale.ENGLISH, "%,.2f",localStorage.getFundsvalue()));
        for(int i=0; i < stockList.size(); i++)
            makeApiCallPrice(stockList.get(i).getTicker(), i);

        recyclerView = view.findViewById(R.id.recyclerview_portfolio);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        portfolioRecyclerAdapter = new PortfolioRecyclerAdapter(stockList, getContext());
        recyclerView.setAdapter(portfolioRecyclerAdapter);
      // double pnl =  portfolioRecyclerAdapter.getPnl();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, AUTO_REFRESH_PERIOD_MSEC);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        timer.cancel();
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

                     //Investment Value
                     double investment_value = number_buyprice.doubleValue() * qty;
                     g_investment_value.set(position,investment_value);
                     g_total_value = calculateFromArrayList(g_investment_value);
                     total_invested.setText("₹"+ String.format(Locale.US, "%,.2f", g_total_value));

                     //Funds
                    funds = localStorage.getFundsvalue();
                    tv_funds.setText(String.format(Locale.ENGLISH, "%,.2f", funds));

                     //Realtime P&L
                    double pnl = qty * (number_lastprice.doubleValue() - number_buyprice.doubleValue());
                    pnlprice.set(position, pnl);
                    g_pnl = calculateFromArrayList(pnlprice);

                    // Investment value with current pnl
                    g_current_vlaue = g_total_value + g_pnl;
                    current_value.setText("₹"+String.format(Locale.ENGLISH, "%,.2f",g_current_vlaue));

                    if(g_pnl < 0)
                    {
                        tvpnl.setTextColor(requireContext().getResources().getColor(R.color.red_stock));
                        g_total_returns = (g_pnl/g_total_value)*100;
                        tv_total_return.setTextColor(requireContext().getResources().getColor(R.color.red_stock));
                        tv_total_return.setText(String.format("%.2f", g_total_returns)+ "%");

                    }
                    else
                    {
                        tvpnl.setTextColor(requireContext().getResources().getColor(R.color.green_stock));
                        g_total_returns = (g_pnl/g_total_value)*100;
                        tv_total_return.setTextColor(requireContext().getResources().getColor(R.color.green_stock));
                        tv_total_return.setText("+"+ String.format("%.2f", g_total_returns)+ "%");

                    }
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

    public void updateGUI()
    {
        for(int i=0; i < stockList.size(); i++)
            makeApiCallPrice(stockList.get(i).getTicker(), i);

    }
    public double calculateFromArrayList(ArrayList<Double> stockpnl)
    {
        double pnl = 0;
        for(int i=0; i < stockpnl.size(); i++)
            pnl = pnl + stockpnl.get(i);
        return pnl;
    }

}