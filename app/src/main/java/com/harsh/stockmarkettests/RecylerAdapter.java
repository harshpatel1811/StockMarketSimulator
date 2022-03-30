package com.harsh.stockmarkettests;

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

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecylerAdapter extends RecyclerView.Adapter<RecylerAdapter.myviewholder> {

  //  String[] tickers;
    List<stock> stockList;
   // stock st;

    public static final String EXTRA_TICKER = "com.harsh.stockmarkettest.MESSAGE";
    public static final String EXTRA_TICKERNAME = "com.harsh.stockmarkettest.TICKERNAME";

    public RecylerAdapter(List<stock> stockList)
    {
       this.stockList = stockList;
    }
    @NonNull
    @Override
    public RecylerAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist, parent,false);
        return new myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull RecylerAdapter.myviewholder holder, int position) {

        /*
        String ticker = st.getTicker();
        String tickerprice = st.getLastprice();
        String change = st.getChange();
         */

        String ticker = stockList.get(position).getTicker();
        String tickerprice = stockList.get(position).getLastprice();
        String change = stockList.get(position).getChange();

        if(change.startsWith("-"))
        {
            //holder.tvtickerprice.setTextColor(Color.parseColor("#F0616C"));
            holder.iv_trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
            holder.tvtickerprice.setText("₹"+tickerprice);
            holder.tvticker.setText(ticker);
            holder.tvchange.setTextColor(Color.parseColor("#F0616C"));
            holder.tvchange.setText(change);
        }
        else
        {
           // holder.tvtickerprice.setTextColor(Color.parseColor("#55AA14"));
            holder.iv_trending.setImageResource(R.drawable.ic_baseline_trending_up_24);
            holder.tvtickerprice.setText("₹"+tickerprice);
            holder.tvticker.setText(ticker);
            holder.tvchange.setTextColor(Color.parseColor("#55AA14"));
            holder.tvchange.setText("+"+change);
        }

    }

    @Override
    public int getItemCount() {
         return stockList.size();
        //return tickers.length;
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        private TextView tvticker, tvtickerprice, tvchange;
        private ImageView iv_trending;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Toast.makeText(itemView.getContext(),stockList.get(getLayoutPosition()).getTicker(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                    intent.putExtra(EXTRA_TICKER ,tvticker.getText().toString());
                    intent.putExtra(EXTRA_TICKERNAME, "UNknown");
                    itemView.getContext().startActivity(intent);

                }
            });
            tvticker = itemView.findViewById(R.id.tickername);
            tvtickerprice = itemView.findViewById(R.id.price);
            tvchange = itemView.findViewById(R.id.tv_change);
            iv_trending = itemView.findViewById(R.id.iv_trending);
        }
    }


    //TODO:- This is left
    /*
    public void maketickername(String ticker, String url)
    {
       ApiCall.make(this, ticker, url, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {

           }
       });
    }

     */
}
