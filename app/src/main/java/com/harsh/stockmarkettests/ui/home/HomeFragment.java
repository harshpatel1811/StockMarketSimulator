package com.harsh.stockmarkettests.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.harsh.stockmarkettests.ApiCall;
import com.harsh.stockmarkettests.AutoSuggestAdapter;
import com.harsh.stockmarkettests.DetailsActivity;
import com.harsh.stockmarkettests.MainActivity;
import com.harsh.stockmarkettests.R;
import com.harsh.stockmarkettests.RecylerAdapter;
import com.harsh.stockmarkettests.databinding.FragmentHomeBinding;
import com.harsh.stockmarkettests.stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private static final long AUTO_REFRESH_PERIOD_MSEC = 15000;

    public static final String EXTRA_TICKER = "com.harsh.stockmarkettest.MESSAGE";
    public static final String EXTRA_TICKERNAME = "com.harsh.stockmarkettest.TICKERNAME";

    public static final String SEARCH_URL = "https://www.screener.in/api/company/search/?q=";
    public static final String PRICE_URL = "https://money.rediff.com/money1/currentstatus.php?companycode=";
    public static final String PRICE_NIFTY = "https://priceapi.moneycontrol.com/pricefeed/notapplicable/inidicesindia/in%3BNSX";
    public static final String PRICE_SENSEX = "https://priceapi.moneycontrol.com/pricefeed/notapplicable/inidicesindia/in%3BSEN";
    private AutoSuggestAdapter autoSuggestAdapter;
    RecyclerView recyclerView;
    List<stock> stockList;
    ArrayList<String> testlist;
    RecylerAdapter recylerAdapter;
    ProgressDialog pd;
    List<String> stockurls;
    private FragmentHomeBinding binding;
    private boolean isSelectedFromList;
    Handler handler;
    private Runnable myrunnable;
    TextView tv_sensex, tv_sensexchange, tv_nifty, tv_niftychange;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setHasOptionsMenu(true);
        tv_sensex = root.findViewById(R.id.tv_sensex);
        tv_sensexchange = root.findViewById(R.id.tv_sensex_change);

        tv_nifty = root.findViewById(R.id.tv_nifty);
        tv_niftychange = root.findViewById(R.id.tv_nifty_change);

        stockList = new ArrayList<>();
        isSelectedFromList = false;

        stockurls = new ArrayList<>();
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=INFY");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=HCLTECH");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=GAIL");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=TCS");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=IDEA");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=TATAMOTORS");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=SUZLON");
        stockurls.add("https://money.rediff.com/money1/currentstatus.php?companycode=SUNPHARMA");

        String[] tickers = new String[8];
        tickers[0] = "INFY";
        tickers[1] = "HCLTECH";
        tickers[2] = "GAIL";
        tickers[3] = "TCS";
        tickers[4] = "IDEA";
        tickers[5] = "TATAMOTORS";
        tickers[6] = "SUZLON";
        tickers[7] = "SUNPHARMA";

        //new JsonTask().execute(stockurls);

         makeCallApiIndex(PRICE_NIFTY);
         makeCallApiIndex(PRICE_SENSEX);
        //TODO;- this is temporary

        for(int i=0; i < stockurls.size(); i++)
        {
            makeApiCallPrice(tickers[i]);

        }

        recyclerView = root.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recylerAdapter = new RecylerAdapter(stockList);
        recyclerView.setAdapter(recylerAdapter);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


     */


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
       inflater.inflate(R.menu.search_menu,menu);
       SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
       SearchView searchView = (SearchView)menu.findItem(R.id.app_bar_search).getActionView();
       searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        SearchView.SearchAutoComplete mSearchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);


        autoSuggestAdapter = new AutoSuggestAdapter(getContext(),
                android.R.layout.simple_dropdown_item_1line);

        mSearchAutoComplete.setAdapter(autoSuggestAdapter);
        mSearchAutoComplete.setDropDownHeight(1000);
        mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {

//            String query = (String)parent.getItemAtPosition(position);

            mSearchAutoComplete.setText(autoSuggestAdapter.getObject(position));
            isSelectedFromList = true;
        });

        mSearchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                isSelectedFromList = false;
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!isSelectedFromList){

                    return false;
                }
                String[] input = query.split("-");
                String[] tick = input[1].split("/");
                Toast.makeText(getActivity(), "Hiiiii", Toast.LENGTH_SHORT).show();
                redirectToDetails(tick[2],input[0]);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSelectedFromList = false;
                return false;
            }
        });

        handler = new Handler( msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {

                //TODO:- As our Price api is not returning name of Stock so we have to get that name from Search api and pass to Details.java activity
                String input = mSearchAutoComplete.getText().toString();

                if (!TextUtils.isEmpty(mSearchAutoComplete.getText()) && input.length() >= 3) {

                    makeApiCall(input);
                }
            }
            return false;
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);

                        }
                    }, 200);
                }
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void makeApiCall(String text) {
        ApiCall.make(getContext(), text, SEARCH_URL, new Response.Listener<String>() {

            //TODO:- Here our implementations need to be done for autocomplete
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    // JSONObject responseObject = new JSONObject(response);
                    // JSONArray array = responseObject.getJSONArray(response);
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        stringList.add(row.getString("name") + "-" + row.getString("url"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, error -> Log.i("error", "error in search http " + error));
    }

    public void redirectToDetails(String ticker, String tickerName) {


        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(EXTRA_TICKER, ticker);
        intent.putExtra(EXTRA_TICKERNAME, tickerName);
        startActivity(intent);


    }
    public void makeApiCallPrice(String ticker)
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

                    stockList.add(st);
                    recylerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
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

    public void makeCallApiIndex(String index_url)
    {
        ApiCall.makeIndex(getContext(), index_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    double lastprice = jsonObject1.getDouble("pricecurrent");
                    double change = jsonObject1.getDouble("pricechange");
                    double pricepercentchange = jsonObject1.getDouble("pricepercentchange");
                    String comapny  = jsonObject1.getString("company");

                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setGroupingUsed(true);

                    String finalchange = String.valueOf(String.format("%.2f", change));
                    String finalchangeinpercent = String.valueOf(String.format("%.2f", pricepercentchange));

                    if(comapny.equals("NIFTY 50"))
                    {
                        //For nifty
                        if(change > 0)
                        {
                            tv_nifty.setText(numberFormat.format( lastprice));
                            tv_niftychange.setText(finalchange + "  ("+ finalchangeinpercent + "%)");
                        }
                        else
                        {
                           // tv_nifty.setTextColor(getResources().getColor(R.color.red_stock));
                            tv_nifty.setText(numberFormat.format(lastprice));
                            tv_niftychange.setTextColor(getResources().getColor(R.color.red_stock));
                            tv_niftychange.setText(finalchange + "  ("+ finalchangeinpercent + "%)");
                        }

                    }
                    else
                    {
                            //For sensex
                            if(change > 0)
                            {
                                tv_sensex.setText(numberFormat.format(lastprice));
                                tv_sensexchange.setText(finalchange + "  ("+ finalchangeinpercent + "%)");
                            }
                            else
                            {
                                //tv_sensex.setTextColor(getResources().getColor(R.color.red_stock));
                                tv_sensex.setText(numberFormat.format(lastprice));
                                tv_sensexchange.setTextColor(getResources().getColor(R.color.red_stock));
                                tv_sensexchange.setText(finalchange + "  ("+ finalchangeinpercent + "%)");
                            }
                    }


                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Somethings problem in parsing", Toast.LENGTH_SHORT).show();
                    Log.d("Parse", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //This method is for backup option for now its no use
    public class JsonTask extends AsyncTask<List<String>, String, List<stock>> {


        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getActivity());
            pd.setMessage("Please wait");
            pd.setCancelable(true);
            pd.show();


        }

        protected List<stock> doInBackground(List<String>... params) {

            List<stock> stockls = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            for(int i = 0; i < params[0].size(); i++)
            {
                try {
                    URL url = new URL(params[0].get(i));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();


                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffe = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffe.append(line + "\n");
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                    }

                    JSONObject jsonObject = new JSONObject(buffe.toString());
                    String price = jsonObject.getString("LastTradedPrice");
                    String ticker = params[0].get(i).substring(params[0].get(i).lastIndexOf("=")+ 1);
                    String change = jsonObject.getString("Change");
                    String changepercent = jsonObject.getString("ChangePercent");
                    String finalchange = change + " (" + changepercent + "%)";
                    stock st = new stock(price, ticker, finalchange);
                    stockls.add(st);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return stockls;
        }

        @Override
        protected void onPostExecute(List<stock> result) {
            super.onPostExecute(result);
            if(pd.isShowing())
                pd.dismiss();

            for(int i=0; i < result.size(); i++)
            {
                stockList.add(result.get(i));
                recylerAdapter.notifyDataSetChanged();
            }


        }
    }

}

