package com.harsh.stockmarkettests;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.PortfolioViewHolder>{
        Context ctx;
        private List<PortfolioStock> stockList;

        public PortfolioRecyclerAdapter(List<PortfolioStock> stockList, Context ctx)
        {
            this.ctx = ctx;
            this.stockList = stockList;

        }

    public class PortfolioViewHolder extends RecyclerView.ViewHolder {
         private TextView tickerPriceLTP,ticker,tickerPnL,tickerQTY, tickerAvgBuy, tickerPriceChange, tickerinvested;

        public PortfolioViewHolder(@NonNull View itemView) {
            super(itemView);

            tickerPriceLTP = itemView.findViewById(R.id.tickerLTP);
            ticker = itemView.findViewById(R.id.ticker_portfolio);
         //   tickerPriceChange = itemView.findViewById(R.id.tickerchangefull);
            //tickerPriceChange = itemView.findViewById(R.id.tickerchange);
            tickerQTY = itemView.findViewById(R.id.tickerQTY);
            tickerPnL = itemView.findViewById(R.id.tickerchange);
          //  tickerAvgBuy = itemView.findViewById(R.id.tickerBuyAvg);
           // tickerinvested = itemView.findViewById(R.id.tickerinvested);

        }

    }
    @NonNull
    @Override
    public PortfolioRecyclerAdapter.PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist_portfolio, parent,false);
        return new PortfolioRecyclerAdapter.PortfolioViewHolder(itemview);
    }


    @Override
    public void onBindViewHolder(@NonNull PortfolioRecyclerAdapter.PortfolioViewHolder holder, int position) {

            String tickerPriceLTP = stockList.get(position).getTickerPriceLTP();
            String ticker = stockList.get(position).getTicker();
            String tickerPnL = stockList.get(position).getTickerPnL();
            int tickerQTY = stockList.get(position).getTickerQTY();
            String tickerAvgBuy = stockList.get(position).getTickerAvgBuy();
            String tickerpricechange = stockList.get(position).getTickerPriceChangeToLTP() + stockList.get(position).getTickerPriceChangePercentToLTP();
            //String tickerPriceChangeToPercent = stockList.get(position).getTickerPnL();
            String tickerInvested = stockList.get(position).getTickerInvested();

            holder.ticker.setText(ticker);
            holder.tickerQTY.setText(String.valueOf(tickerQTY));
            holder.tickerPriceLTP.setText("₹"+ tickerPriceLTP);
            holder.tickerPnL.setText(tickerPnL);
            NumberFormat myFormat = NumberFormat.getInstance();
            try {
                Number number = myFormat.parse(holder.tickerPnL.getText().toString());
               // pnl = pnl + number.doubleValue();
                if(holder.tickerPnL.getText().toString().startsWith("-"))
                {
                    holder.tickerPnL.setText("-₹"+myFormat.format(number).substring(1));
                    holder.tickerPnL.setTextColor(ctx.getResources().getColor(R.color.red_stock));
                }
                else
                {
                    holder.tickerPnL.setText("+₹"+myFormat.format(number));
                    holder.tickerPnL.setTextColor(ctx.getResources().getColor(R.color.green_stock));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}

