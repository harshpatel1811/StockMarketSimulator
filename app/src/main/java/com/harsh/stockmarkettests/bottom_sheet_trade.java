package com.harsh.stockmarkettests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ncorti.slidetoact.SlideToActView;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class bottom_sheet_trade extends BottomSheetDialogFragment {

    TextView ticker, tickerprice, tickerchange, maxqty;
    EditText et_qty;
    SlideToActView slideToActView;
    ThemedToggleButtonGroup themedToggleButtonGroup;
    DataViewModel dataViewModel;
    Button button;
    NestedScrollView nestedScrollView;
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_bottom_sheet_trade, container, false);
        ticker = view.findViewById(R.id.ticker);
        tickerprice = view.findViewById(R.id.tickerprice);
        tickerchange = view.findViewById(R.id.tickerpricechange);
        //aSwitch = view.findViewById(R.id.labeledswitch);
        slideToActView = view.findViewById(R.id.slidertoactview);
        themedToggleButtonGroup = view.findViewById(R.id.themedtoggled);
        themedToggleButtonGroup.selectButton(R.id.btn1);
        et_qty = view.findViewById(R.id.edittext_qty);

        themedToggleButtonGroup.setOnSelectListener((ThemedButton btn) -> {
                // handle selected button
                if(btn.isSelected())
                {
                    if(btn.getText().equals("Buy"))
                    {
                      //  btn.setBgColor(getResources().getColor(R.color.default_slider_color));
                        slideToActView.setText("Slide to Buy");
                        slideToActView.setInnerColor(getResources().getColor(R.color.white));
                        slideToActView.setOuterColor(getResources().getColor(R.color.default_slider_color));
                    }
                    else
                    {
                        slideToActView.setText("Slide to Sell");
                       // btn.setBgColor(getResources().getColor(R.color.red_stock));
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
                if(qty.isEmpty())
                {
                    slideToActView.resetSlider();
                }
                else
                {
                    if(slideToActView.isAnimateCompletion())
                    {
                        LocalStorage localStorage = new LocalStorage(getContext());
                        localStorage.fillScript(strticker, Integer.parseInt(qty), strprice);
                        dataViewModel.getBottomSheetDialogFragment().dismiss();
                    }
                }
            }
        });

        dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
        dataViewModel.getticker().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ticker.setText(s);
            }
        });

        dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
        dataViewModel.gettickerprice().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tickerprice.setText(s);

            }
        });

        dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
        dataViewModel.gettickerpricechange().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.startsWith("-"))
                {
                   tickerchange.setTextColor(getResources().getColor(R.color.red_stock));
                    tickerchange.setText(s);
                }
                else
                {
                    tickerchange.setTextColor(getResources().getColor(R.color.green_stock));
                    tickerchange.setText(s);
                }
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