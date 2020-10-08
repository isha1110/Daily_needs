package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.AddressResponse;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.DefaultAddressRequest;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class SelectAddressFragment extends BaseFragment {

    private List<AddressResponse.AddressItem> mAddressResponseList = new ArrayList<>();
    private static final String TAG = "Address List";
    private AddressListAdapter mAddressListAdapter;
    private View addressGroup;
    private String mSelectedAddressId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_select_address, container, false);
        setupUI();
        return mContentView;
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(TAG);
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        RecyclerView addressRecyclerView = mContentView.findViewById(R.id.selectAddressRecycler);
        addressGroup = mContentView.findViewById(R.id.addressGroup);
        mAddressListAdapter = new AddressListAdapter(mAddressResponseList);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        addressRecyclerView.setAdapter(mAddressListAdapter);
        fetchAddressServerCall();
    }

    private void fetchAddressServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<AddressResponse> call = RetrofitApi.getAppServicesObject().fetchAddress(new CommonRequest(getStringDataFromSharedPref(Constants.USER_ID)));
                    final Response<AddressResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<AddressResponse> response) {
                if (response.isSuccessful()) {
                    AddressResponse addressResponse = response.body();
                    if (addressResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(addressResponse.getErrorCode())) {
                            if (!Utility.isEmpty(mAddressResponseList)) {
                                mAddressResponseList.clear();
                            }
                            mAddressResponseList = addressResponse.getAddressList();
                            if (Utility.isEmpty(mAddressResponseList)) {
                                mContentView.findViewById(R.id.noItemFoundContainer).setVisibility(View.VISIBLE);
                            } else {
                                mContentView.findViewById(R.id.noItemFoundContainer).setVisibility(View.GONE);
                            }
                            addressGroup.setVisibility(Utility.isEmpty(mAddressResponseList) ? View.GONE : View.VISIBLE);
                            mAddressListAdapter.setAddressList(mAddressResponseList);
                            mAddressListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNewAddressLayout:
                launchFragment(new AddNewAddressFragment(), true);
                break;
            case R.id.makeDefaultButton:
                if (Utility.isEmpty(mSelectedAddressId)) {
                    showToast(getString(R.string.default_address_msg));
                    return;
                }
                saveAddressServerCall(mSelectedAddressId);
                break;
        }
    }

    private void saveAddressServerCall(String addressId) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DefaultAddressRequest request = new DefaultAddressRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setAddressId(addressId);
                    Call<AddressResponse> call = RetrofitApi.getAppServicesObject().makeDefaultAddress(request);
                    final Response<AddressResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<AddressResponse> response) {
                if (response.isSuccessful()) {
                    AddressResponse addressResponse = response.body();
                    if (addressResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(addressResponse.getErrorCode())) {
                            if (!Utility.isEmpty(mAddressResponseList)) {
                                mAddressResponseList.clear();
                            }
                            mAddressResponseList = addressResponse.getAddressList();
                            addressGroup.setVisibility(Utility.isEmpty(mAddressResponseList) ? View.GONE : View.VISIBLE);
                            mAddressListAdapter.setAddressList(mAddressResponseList);
                            mAddressListAdapter.notifyDataSetChanged();
                            showToast(addressResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideSearchIcon();
        mActivity.showCartIcon();
    }

    private class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.RecyclerViewHolder> {

        private List<AddressResponse.AddressItem> mAddressList;

        public void setAddressList(List<AddressResponse.AddressItem> addressList) {
            mAddressList = addressList;
        }

        AddressListAdapter(List<AddressResponse.AddressItem> list) {
            mAddressList = list;
        }

        @NonNull
        @Override
        public AddressListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_list, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressListAdapter.RecyclerViewHolder holder, int position) {
            AddressResponse.AddressItem currentItem = mAddressList.get(position);
            holder.mRadioButton.setText(currentItem.getAddressStr());
            holder.mRadioButton.setChecked(currentItem.isDefaultAddress());
        }

        @Override
        public int getItemCount() {
            return mAddressList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private RadioButton mRadioButton;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                mRadioButton = itemView.findViewById(R.id.checkBox);
                mRadioButton.setOnClickListener(v -> {
                    mSelectedAddressId = mAddressList.get(getAdapterPosition()).getAddressId();
                    setDefaultValueToAddressList();
                    AddressResponse.AddressItem currentItem = mAddressList.get(getAdapterPosition());
                    currentItem.setDefaultAddress(true);
                    notifyDataSetChanged();
                });
            }

            private void setDefaultValueToAddressList() {
                for (AddressResponse.AddressItem addressItem : mAddressList) {
                    addressItem.setDefaultAddress(false);
                }
            }
        }
    }
}
