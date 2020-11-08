package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.constant.ToolBarManager;

public class ThankYouFragment extends BaseFragment {

    private String mOrderId;
    private String mExpectedDelivery;

    public ThankYouFragment(String orderId, String expectedDelivery) {
        mOrderId = orderId;
        mExpectedDelivery = expectedDelivery;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_thank_you, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        TextView textView = mContentView.findViewById(R.id.orderIdNumber);
        TextView messageText = mContentView.findViewById(R.id.messageText);
        textView.setText(getString(R.string.order_id) + " : " + mOrderId);
        String message = getString(R.string.expected_delivey_message) + " " + mExpectedDelivery;
        messageText.setText(message);
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.homeTextView){
            launchFragment(new HomeScreenFragment(),false);
        }
    }

    @Override
    public boolean onBackPressed() {
        launchFragment(new HomeScreenFragment(), false);
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        mActivity.hideSearchIcon();
        hideKeyboard();
    }
}
