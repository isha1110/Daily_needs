package com.skinfotech.dailyneeds.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.skinfotech.dailyneeds.fragments.ProductCategoryListFragment;


public class MyProductCategoryPagerAdapter extends FragmentStatePagerAdapter {

    public MyProductCategoryPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    String[] leadsArray = new String[]{"Staples", "Snack and Beverages","Packed Food","Fruits & Vegetable","Breakfast & Dairy"};
    Integer count = 5;

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return leadsArray[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return new ProductCategoryListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
