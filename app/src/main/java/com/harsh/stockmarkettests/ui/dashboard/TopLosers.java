package com.harsh.stockmarkettests.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.harsh.stockmarkettests.ApiCall;
import com.harsh.stockmarkettests.R;
import com.harsh.stockmarkettests.stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopLosers extends Fragment {

    private static final String url ="https://www1.nseindia.com/live_market/dynaContent/live_analysis/losers/niftyLosers1.json";
    RecyclerView recyclerView;
    MoversRecyclerAdapter moversRecyclerAdapter;
    List<stock> stockList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_top_losers, container, false);
        stockList = new ArrayList<>();
        getTopGainers(url);
        recyclerView = root.findViewById(R.id.recyclerview_toploosers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MoversRecyclerAdapter moversRecyclerAdapter = new MoversRecyclerAdapter(stockList, getContext());
        return  root;

    }

    private void getTopGainers(String url) {
        ApiCall.make(getContext(), "", url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i=0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject2 = new JSONObject(jsonArray.get(i).toString());
                        String tickerName = jsonObject2.getString("symbol");
                        String tickerltp = jsonObject2.getString("ltp");
                        String tickerprev_close = jsonObject2.getString("previousPrice");
                        String tickerper_change = jsonObject2.getString("netPrice");
                        NumberFormat numberFormat = NumberFormat.getNumberInstance();
                        Number number = numberFormat.parse(tickerltp);
                        Number number1 = numberFormat.parse(tickerprev_close);
                        double ticker_change = number.doubleValue() - number1.doubleValue();
                        String ticker_final_change = String.format(Locale.ENGLISH, "%,.2f", ticker_change) + "  " + "("+tickerper_change+"%)";
                        Log.d("Movers", tickerName+ " " + tickerltp + " "+ ticker_final_change);

                        stock stock = new stock(tickerltp, tickerName, ticker_final_change);
                        stockList.add(stock);
                        recyclerView.setAdapter(new MoversRecyclerAdapter(stockList, getContext()));
                    }
                } catch (JSONException | ParseException e) {
                    Toast.makeText(getContext(), "Error in Parsing data", Toast.LENGTH_SHORT).show();
                    // Log.d("Gainers", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error while loading data !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}