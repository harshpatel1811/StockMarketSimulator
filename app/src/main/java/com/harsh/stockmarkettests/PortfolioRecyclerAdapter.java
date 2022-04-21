package com.harsh.stockmarkettests;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.harsh.stockmarkettests.ui.Portfolio.BottomSheetPortfolio;
import com.harsh.stockmarkettests.ui.Portfolio.PortfolioSection;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.PortfolioViewHolder>{
        Context ctx;
        private List<PortfolioStock> stockList;

        Timer timer;

        public PortfolioRecyclerAdapter(List<PortfolioStock> stockList, Context ctx)
        {
            this.ctx = ctx;
            this.stockList = stockList;
        }

    public class PortfolioViewHolder extends RecyclerView.ViewHolder {
         private TextView tickerPriceLTP,ticker,tickerPnL,tickerQTY;
         String tickerchange;

        public PortfolioViewHolder(@NonNull View itemView) {
            super(itemView);

            tickerPriceLTP = itemView.findViewById(R.id.tickerLTP);
            ticker = itemView.findViewById(R.id.ticker_portfolio);
         //   tickerPriceChange = itemView.findViewById(R.id.tickerchangefull);
            //tickerPriceChange = itemView.findViewById(R.id.tickerchange);
            tickerQTY = itemView.findViewById(R.id.tickerQTY);
            tickerPnL = itemView.findViewById(R.id.tickerchange);
            tickerchange = null;
          //  tickerAvgBuy = itemView.findViewById(R.id.tickerBuyAvg);
           // tickerinvested = itemView.findViewById(R.id.tickerinvested);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataViewModel dataViewModel = new ViewModelProvider(((AppCompatActivity)ctx)).get(DataViewModel.class);
                    BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetPortfolio();
                    bottomSheetDialogFragment.show(((AppCompatActivity)ctx).getSupportFragmentManager(),bottomSheetDialogFragment.getTag());
                    Bundle bundle = new Bundle();
                    bundle.putString("Ticker", ticker.getText().toString());
                    bundle.putInt("Position", getAdapterPosition());
                    bottomSheetDialogFragment.setArguments(bundle);
                }
            });
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
            holder.tickerchange = tickerpricechange;
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

