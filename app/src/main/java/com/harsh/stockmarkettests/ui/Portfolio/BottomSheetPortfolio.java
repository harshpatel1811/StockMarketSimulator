package com.harsh.stockmarkettests.ui.Portfolio;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.harsh.stockmarkettests.ApiCall;
import com.harsh.stockmarkettests.DataViewModel;
import com.harsh.stockmarkettests.LocalStorage;
import com.harsh.stockmarkettests.PortfolioRecyclerAdapter;
import com.harsh.stockmarkettests.R;
import com.ncorti.slidetoact.SlideToActView;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class BottomSheetPortfolio extends BottomSheetDialogFragment {

    LocalStorage localStorage;
    TextView tv_ticker, tv_tickerprice, tv_tickerchange, tv_totalQty, tv_totalInvest, tv_currentInvest, tv_totalpnl, tv_averagePrice, tv_totalReturn;
    ThemedToggleButtonGroup themedToggleButtonGroup;
    SlideToActView slideToActView;
    EditText editText_buyqty, editText_sellqty;
    private final String PRICE_URL = "https://money.rediff.com/money1/currentstatus.php?companycode=";
    int g_position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bottom_sheet_portfolio, container, false);

        tv_ticker = root.findViewById(R.id.bottom_ticker);
        tv_tickerprice = root.findViewById(R.id.bottom_tickerprice);
        tv_tickerchange = root.findViewById(R.id.bottom_tickerchange);
        tv_totalInvest = root.findViewById(R.id.totalinvested);
        tv_totalQty = root.findViewById(R.id.total_qty);
        tv_currentInvest = root.findViewById(R.id.currentvalue_portfolio_stock);
        tv_totalpnl = root.findViewById(R.id.daypnl);
        tv_averagePrice = root.findViewById(R.id.average_price);
        tv_totalReturn = root.findViewById(R.id.total_return_portfolio);
        editText_buyqty = root.findViewById(R.id.edittext_buyqty);
        editText_sellqty = root.findViewById(R.id.edittext_sellqty);
        slideToActView = root.findViewById(R.id.slidertoactview_portfolio);
        themedToggleButtonGroup = root.findViewById(R.id.themedtoggled_portfolio);


        localStorage = new LocalStorage(getContext());
        double funds = localStorage.getFundsvalue();
        themedToggleButtonGroup.selectButton(R.id.btn_add);
        editText_sellqty.setEnabled(false);

        Bundle bundle = new Bundle();
        bundle.getString("Ticker");
        String ticker = this.getArguments().getString("Ticker");
        int position = this.getArguments().getInt("Position");
        g_position = position;

        makeApiCall(ticker);

        themedToggleButtonGroup.setOnSelectListener((ThemedButton btn) -> {
            // handle selected button
            if(btn.isSelected())
            {
                if(btn.getText().equals("Add"))
                {
                    //  btn.setBgColor(getResources().getColor(R.color.default_slider_color));
                    slideToActView.setText("Slide to Add");
                    slideToActView.setInnerColor(getResources().getColor(R.color.white));
                    slideToActView.setOuterColor(getResources().getColor(R.color.default_slider_color));
                    editText_sellqty.setEnabled(false);
                    editText_buyqty.setEnabled(true);

                }
                else
                {
                    slideToActView.setText("Slide to Sell");
                    // btn.setBgColor(getResources().getColor(R.color.red_stock));
                    slideToActView.setInnerColor(getResources().getColor(R.color.white));
                    slideToActView.setOuterColor(getResources().getColor(R.color.red_stock));
                    editText_buyqty.setEnabled(false);
                    editText_sellqty.setEnabled(true);
                }
            }
            return kotlin.Unit.INSTANCE;
        });

        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                 if(themedToggleButtonGroup.getSelectedButtons().get(0).getText().equals("Add"))
                 {
                     //Adding Stock
                     int qty = Integer.parseInt(editText_buyqty.getText().toString());
                     Number price = null;
                     try {
                         price = NumberFormat.getNumberInstance().parse(tv_tickerprice.getText().toString().substring(1));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }

                     if(qty* price.doubleValue() > funds) {
                         Toast.makeText(getContext(), "You do not have enough fund", Toast.LENGTH_SHORT).show();
                         slideToActView.resetSlider();
                     }
                     else
                     {
                         localStorage.averagePrice(tv_ticker.getText().toString(), price.doubleValue(), qty);
                         PortfolioSection.stockList.get(position).setTickerQTY((Integer.parseInt(tv_totalQty.getText().toString())+ qty));
                         localStorage.modiftyFundsBuy((float)(price.doubleValue() * qty));
                         BottomSheetPortfolio.this.dismiss();
                     }
                 }
                 else
                 {
                     //Squaring off position
                     int qty = Integer.parseInt(editText_sellqty.getText().toString());

                     if(qty > Integer.parseInt(tv_totalQty.getText().toString()))
                        slideToActView.resetSlider();

                     else
                     {
                         Number price = null;
                         try {
                             price = NumberFormat.getNumberInstance().parse(tv_tickerprice.getText().toString().substring(1));
                         } catch (ParseException e) {
                             e.printStackTrace();
                         }
                         if (qty == Integer.parseInt(tv_totalQty.getText().toString())) {
                             Log.d("Square off", "Full");
                             PortfolioSection.portfolioRecyclerAdapter.notifyItemRemoved(position);
                             localStorage.modiftyFundsSell(ticker, qty, price.floatValue(), position);
                             PortfolioSection.portfolioRecyclerAdapter.notifyItemRangeChanged(position, PortfolioSection.stockList.size());
                             BottomSheetPortfolio.this.dismiss();
                         } else {
                             Log.d("Square off", "Limited");
                             localStorage.modiftyFundsSell(ticker, qty, price.floatValue(), position);
                             PortfolioSection.portfolioRecyclerAdapter.notifyItemChanged(position);
                             BottomSheetPortfolio.this.dismiss();
                         }
                     }
                 }
            }
        });

        return root;
    }

    private void makeApiCall(String ticker)
    {
        ApiCall.make(getContext(), ticker, PRICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    Object object[] = new Object[3];
                    object = localStorage.getPortFolioData(ticker);
                    JSONObject row = new JSONObject(response);
                    String last = row.getString("LastTradedPrice");
                    String change = row.getString("Change");
                    String changepercent = row.getString("ChangePercent");
                    tv_ticker.setText(ticker);
                    tv_tickerprice.setText("₹" + last);

                    if(change.startsWith("-"))
                    {
                        tv_tickerchange.setTextColor(getResources().getColor(R.color.red_stock));
                        tv_tickerchange.setText(change + " " + "("+changepercent+"%)");
                    }
                    else
                    {
                        tv_tickerchange.setTextColor(getResources().getColor(R.color.green_stock));
                        tv_tickerchange.setText("+"+change + " " + "("+changepercent+"%)");
                    }

                    tv_totalQty.setText(object[1].toString());
                    tv_averagePrice.setText(object[2].toString());

                    int qty =(int)object[1];
                    Number averagePrice = NumberFormat.getNumberInstance().parse((object[2].toString().substring(1)));
                    Number lastprice = NumberFormat.getNumberInstance().parse(last);

                    double currentdifference = (lastprice.doubleValue() - averagePrice.doubleValue())* qty;
                    double totalInvested = averagePrice.doubleValue() * qty;
                    double currentValue = totalInvested + currentdifference;

                    double totalpnl = (lastprice.doubleValue() - averagePrice.doubleValue()) * qty;
                    double total_return = ((currentValue - totalInvested)/ totalInvested) *100;
                    tv_totalInvest.setText("₹"+String.format(Locale.ENGLISH, "%,.2f", totalInvested));
                    tv_currentInvest.setText("₹"+String.format(Locale.ENGLISH, "%,.2f", currentValue));

                    if(total_return > 0)
                        tv_totalReturn.setTextColor(getResources().getColor(R.color.green_stock));
                    else
                        tv_totalReturn.setTextColor(getResources().getColor(R.color.red_stock));

                    tv_totalReturn.setText(String.format(Locale.ENGLISH, "%,.2f", total_return)+ "%");

                    if(totalpnl > 0)
                        tv_totalpnl.setTextColor(getResources().getColor(R.color.green_stock));
                    else
                        tv_totalpnl.setTextColor(getResources().getColor(R.color.red_stock));

                    tv_totalpnl.setText(String.format(Locale.ENGLISH, "%,.2f", totalpnl));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, (Response.ErrorListener) error -> {
            Log.i("error", "error in search http " + error);
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        //Update recyclerview from here
        PortfolioSection.portfolioRecyclerAdapter.notifyItemChanged(g_position);
        if(PortfolioSection.stockList.size() == 0)
        {
            PortfolioSection.total_invested.setText("₹0.00");
            PortfolioSection.current_value.setText("₹0.00");
            PortfolioSection.tv_total_return.setText("0.00%");
            double funds = localStorage.getFundsvalue();
            PortfolioSection.tv_funds.setText(String.format(Locale.ENGLISH, "%,.2f", funds));
        }
        //TODO:- can refresh data here
    }
}