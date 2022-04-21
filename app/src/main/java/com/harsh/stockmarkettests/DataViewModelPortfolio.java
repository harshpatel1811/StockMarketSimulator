package com.harsh.stockmarkettests;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.harsh.stockmarkettests.ui.Portfolio.BottomSheetPortfolio;

public class DataViewModelPortfolio extends DataViewModel {
    private BottomSheetPortfolio bottomSheetPortfolio;

    public void setBottomSheetPortfolio(BottomSheetPortfolio bottomSheetPortfolio) {
        this.bottomSheetPortfolio = bottomSheetPortfolio;
    }
    public BottomSheetPortfolio getBottomSheetPortfolio()
    {
        return bottomSheetPortfolio;
    }
}

