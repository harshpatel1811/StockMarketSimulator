package com.harsh.stockmarkettests.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.harsh.stockmarkettests.R;
import com.harsh.stockmarkettests.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

   Spinner spinner;
   ViewPageAdapter viewPageAdapter;
   ViewPager2 viewPager2;
   int selcteditem;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        String[] data = {"TOP GAINERS", "TOP LOOSERS"};
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        selcteditem = 0;
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        viewPager2 = root.findViewById(R.id.viewpager2);
        viewPager2.setAdapter(new ViewPageAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
              tab.setText(data[position]);
            }
            }).attach();

        Bundle bundle = new Bundle();
        bundle.putInt("SelectedItem",selcteditem); // Put anything what you want

        TopMovers topMovers = new TopMovers();
        topMovers.setArguments(bundle);

        TopLosers topLosers = new TopLosers();
        topLosers.setArguments(bundle);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}