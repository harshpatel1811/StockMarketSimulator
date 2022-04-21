package com.harsh.stockmarkettests;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ncorti.slidetoact.SlideToActView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class bottom_sheet_trade extends BottomSheetDialogFragment {

    TextView ticker, tickerprice, tickerchange, tv_maxqty, tv_available_fund, tv_buyOrSellQty, tv_sharesQty;
    EditText et_qty;
    SlideToActView slideToActView;
    ThemedToggleButtonGroup themedToggleButtonGroup;
    DataViewModel dataViewModel;
    int max_qty = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_bottom_sheet_trade, container, false);
        ticker = view.findViewById(R.id.ticker);
        tickerprice = view.findViewById(R.id.tickerprice);
        tickerchange = view.findViewById(R.id.tickerpricechange);
        tv_maxqty = view.findViewById(R.id.maxqty);
        //aSwitch = view.findViewById(R.id.labeledswitch);
        slideToActView = view.findViewById(R.id.slidertoactview_portfolio);
        themedToggleButtonGroup = view.findViewById(R.id.themedtoggled);

        tv_available_fund = view.findViewById(R.id.tv_available_fund);
        et_qty = view.findViewById(R.id.edittext_qty);
        tv_sharesQty = view.findViewById(R.id.tv_shares_qty);
        tv_buyOrSellQty = view.findViewById(R.id.tv_buyorsellqty);
        LocalStorage localStorage = new LocalStorage(getContext());
        float fund = localStorage.getFundsvalue();

        themedToggleButtonGroup.selectButton(R.id.btn1);
        tv_buyOrSellQty.setText("Enter Buy QTY");
        tv_available_fund.setText("₹" +String.format(Locale.ENGLISH, "%,.2f", fund));
        themedToggleButtonGroup.setOnSelectListener((ThemedButton btn) -> {
                // handle selected button
                if(btn.isSelected())
                {
                    if(btn.getText().equals("Buy"))
                    {
                      //  btn.setBgColor(getResources().getColor(R.color.default_slider_color));
                        slideToActView.setText("Slide to Buy");
                        tv_buyOrSellQty.setText("Enter Buy QTY");
                        slideToActView.setInnerColor(getResources().getColor(R.color.white));
                        slideToActView.setOuterColor(getResources().getColor(R.color.default_slider_color));
                    }
                    else
                    {
                        slideToActView.setText("Slide to Sell");
                        tv_buyOrSellQty.setText("Enter Sell QTY");
                        slideToActView.setInnerColor(getResources().getColor(R.color.white));
                        slideToActView.setOuterColor(getResources().getColor(R.color.red_stock));
                    }
                }
                return kotlin.Unit.INSTANCE;
            });

        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {

                String strticker = ticker.getText().toString();
                String strprice = tickerprice.getText().toString();
                String qty = et_qty.getText().toString().trim();
                double tickerprice = 0;
                try {
                    Number number = NumberFormat.getNumberInstance().parse(strprice.substring(1));
                    tickerprice = number.doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(qty.isEmpty())
                {
                    slideToActView.resetSlider();
                    Toast.makeText(getContext(), "Enter Quantity", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(themedToggleButtonGroup.getSelectedButtons().get(0).getText().equals("Buy"))
                    {
                        //This isanimatedCompletion func can be reduced
                        if(Integer.parseInt(qty) <= max_qty)
                        {
                            float funds = localStorage.getFundsvalue();
                            if(!localStorage.isScripExist(strticker))
                                localStorage.fillScript(strticker, Integer.parseInt(qty), strprice);
                            else
                                localStorage.averagePrice(ticker.getText().toString(), tickerprice, Integer.parseInt(qty));

                            localStorage.modiftyFundsBuy((float)(tickerprice * Integer.parseInt(qty)));
                            dataViewModel.getBottomSheetDialogFragment().dismiss();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "You don't have enough fund", Toast.LENGTH_SHORT).show();
                            slideToActView.resetSlider();
                        }
                    }
                    else
                    {
                        if(Integer.parseInt(qty) <= Integer.parseInt(tv_sharesQty.getText().toString()))
                        {
                            localStorage.modiftyFundsSell(ticker.getText().toString(), Integer.parseInt(qty), (float)tickerprice, localStorage.getTickerPosition(ticker.getText().toString()));
                            dataViewModel.getBottomSheetDialogFragment().dismiss();
                        }

                        else
                            slideToActView.resetSlider();

                    }
                }
            }
        });

        dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
        dataViewModel.getticker().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ticker.setText(s);
                tv_sharesQty.setText(String.valueOf(localStorage.getQTY(ticker.getText().toString())));
            }
        });

      //  dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
        dataViewModel.gettickerprice().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tickerprice.setText(s);
                Number tkprice = null;
                try {
                    tkprice = NumberFormat.getNumberInstance().parse(s.substring(1));
                    max_qty =(int) (fund/tkprice.floatValue());
                    tv_maxqty.setText(String.valueOf(max_qty));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
        dataViewModel.gettickerpricechange().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if(s.startsWith("-"))
                   tickerchange.setTextColor(getResources().getColor(R.color.red_stock));
                else
                    tickerchange.setTextColor(getResources().getColor(R.color.green_stock));

                tickerchange.setText(s);
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
      //  nestedScrollView.setVisibility(View.VISIBLE);
    }
}