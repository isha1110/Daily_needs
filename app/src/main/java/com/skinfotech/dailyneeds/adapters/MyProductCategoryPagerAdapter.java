package com.skinfotech.dailyneeds.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.skinfotech.dailyneeds.fragments.ProductCategoryListFragment;
import com.skinfotech.dailyneeds.models.responses.InnerCategoryProduct;
import com.skinfotech.dailyneeds.models.responses.ProductsLabels;

import java.util.ArrayList;
import java.util.List;

public class MyProductCategoryPagerAdapter extends FragmentStatePagerAdapter {

    List<ProductsLabels.CategoryLabels> categoryLabelsList = new ArrayList<>();

    public void setCategoryLabelsList(List<ProductsLabels.CategoryLabels> categoryLabelsList) {
        this.categoryLabelsList = categoryLabelsList;
    }

    public MyProductCategoryPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        ProductsLabels.CategoryLabels categoryLabels = categoryLabelsList.get(position);
        return categoryLabels.getLabelName();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ProductCategoryListFragment(categoryLabelsList.get(position).getInnerCategoryProducts());
    }

    @Override
    public int getCount() {
        return categoryLabelsList.size();
    }
}
