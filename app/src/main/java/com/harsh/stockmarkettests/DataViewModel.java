package com.harsh.stockmarkettests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DataViewModel extends ViewModel {

    private final MutableLiveData<String> ticker = new MutableLiveData<>();
    private final MutableLiveData<String> tickerprice = new MutableLiveData<>();
    private final MutableLiveData<String> tickerpricechange = new MutableLiveData<>();
    private BottomSheetDialogFragment bottomSheetDialogFragment;

    public void setTicker(String str)
    {
       ticker.setValue(str);
    }

    public void setTickerprice(String str)
    {
        tickerprice.setValue(str);
    }

    public void setTickerpricechange(String str)
    {
        tickerpricechange.setValue(str);
    }

    public void setBottomSheetDialogFragment(BottomSheetDialogFragment bottomSheetDialogFragment)
    {
        this.bottomSheetDialogFragment = bottomSheetDialogFragment;
    }
    public LiveData<String> getticker()
    {
        return ticker;
    }

    public LiveData<String> gettickerprice()
    {
        return tickerprice;
    }
    public LiveData<String> gettickerpricechange()
    {
        return tickerpricechange;
    }

    public BottomSheetDialogFragment getBottomSheetDialogFragment()
    {
        return bottomSheetDialogFragment;
    }

}
