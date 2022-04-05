package com.harsh.stockmarkettests.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.stockmarkettests.DetailsActivity;
import com.harsh.stockmarkettests.R;
import com.harsh.stockmarkettests.RecylerAdapter;
import com.harsh.stockmarkettests.ui.home.HomeFragment;

import java.util.List;

public class MoversRecyclerAdapter extends RecyclerView.Adapter<MoversRecyclerAdapter.MyViewHolder> {
    public static final String LOSERS_TICKER = "com.harsh.stockmarkettest.MESSAGE";
    List<com.harsh.stockmarkettests.stock> stockList;
    Context ctx;

    public MoversRecyclerAdapter(List<com.harsh.stockmarkettests.stock> stockList, Context ctx)
    {
        this.stockList = stockList;
        this.ctx = ctx;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_trending;
        private TextView tvticker, tvtickerprice, tvchange;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvticker = itemView.findViewById(R.id.tickername);
            tvtickerprice = itemView.findViewById(R.id.price);
            tvchange = itemView.findViewById(R.id.tv_change);
            iv_trending = itemView.findViewById(R.id.iv_trending);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                    intent.putExtra(HomeFragment.EXTRA_TICKER,tvticker.getText().toString());
                    intent.putExtra(HomeFragment.EXTRA_TICKERNAME, tvticker.getText());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
        }

        @NonNull
        @Override
        public MoversRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist, parent, false);
            return new MyViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(@NonNull MoversRecyclerAdapter.MyViewHolder holder, int position) {


            String ticker = stockList.get(position).getTicker();
            String tickerprice = stockList.get(position).getLastprice();
            String change = stockList.get(position).getChange();


            if (change.startsWith("-")) {
                //holder.tvtickerprice.setTextColor(Color.parseColor("#F0616C"));
                holder.iv_trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
                holder.tvtickerprice.setText("₹" + tickerprice);
                holder.tvticker.setText(ticker);
                holder.tvchange.setTextColor(Color.parseColor("#F0616C"));
                holder.tvchange.setText(change);
            } else {
                // holder.tvtickerprice.setTextColor(Color.parseColor("#55AA14"));
                holder.iv_trending.setImageResource(R.drawable.ic_baseline_trending_up_24);
                holder.tvtickerprice.setText("₹" + tickerprice);
                holder.tvticker.setText(ticker);
                holder.tvchange.setTextColor(Color.parseColor("#55AA14"));
                holder.tvchange.setText("+" + change);
            }

        }

        @Override
        public int getItemCount() {
            return stockList.size();
        }
    }

