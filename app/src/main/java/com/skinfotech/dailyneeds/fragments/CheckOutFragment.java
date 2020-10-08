package com.skinfotech.dailyneeds.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.skinfotech.dailyneeds.models.requests.PaymentRequest;
import com.skinfotech.dailyneeds.models.responses.CheckOutResponse;
import com.skinfotech.dailyneeds.models.responses.PaymentResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class CheckOutFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private CheckoutAdapter mCheckoutAdapter;
    private SelectAddressListAdapter mSelectAddressListAdapter;
    private String mSelectedPaymentMode = "";
    private String mSelectedAddressId = "";
    private RadioButton onlineRadioButton;
    private RadioButton cashRadioButton;
    private static final String TAG = "CheckOutFragment";
    private BottomSheetDialog bottomSheetDialog;
    private  RecyclerView selectAddressRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_checkout_screen, container, false);
        setupUI();
        fetchCheckoutServerCall();
        fetchAddressServerCall();
        mSelectedPaymentMode = getString(R.string.online_payment);
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        mActivity.hideSearchIcon();
        hideKeyboard();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buyNow:
                bottomSheetDialog.show();
                break;
            case R.id.proceedToPay:
                if (Utility.isEmpty(mSelectedAddressId)){
                    showToast("Please Select Delivery Address");
                    return;
                }
                if (Utility.isNotEmpty(mSelectedPaymentMode) && mSelectedPaymentMode.equalsIgnoreCase(getString(R.string.cash_on_delivery))) {
                    savePaymentServerCall("");
                } else {
                    String amount = ((TextView) mContentView.findViewById(R.id.deliveryCharges)).getText().toString();
                    showProgress();
                    mActivity.startPayment(amount);
                }
                bottomSheetDialog.hide();
                break;
            case R.id.addMoreItem:
                launchFragment(new HomeScreenFragment(),false);
                break;
            default:
                break;
        }
    }

    private void fetchCheckoutServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommonRequest request = new CommonRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    Call<CheckOutResponse> call = RetrofitApi.getAppServicesObject().fetchCheckout(request);
                    final Response<CheckOutResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CheckOutResponse> response) {
                if (response.isSuccessful()) {
                    CheckOutResponse checkOutResponse = response.body();
                    if (checkOutResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(checkOutResponse.getErrorCode())) {
                            List<CheckOutResponse.ProductItem> productList = checkOutResponse.getProductList();
                            if (!Utility.isEmpty(productList)) {
                                mCheckoutAdapter.setProductList(productList);
                                mCheckoutAdapter.notifyDataSetChanged();
                            }
                            String bagItemStr = "(" + checkOutResponse.getBagItems() + ")";
                            ((TextView) mContentView.findViewById(R.id.textView8)).setText(checkOutResponse.getDiscountMessage());
                            ((TextView) mContentView.findViewById(R.id.totalitem)).setText(bagItemStr);
                            ((TextView) mContentView.findViewById(R.id.bagTotalAmount)).setText(Utility.getAmountInCurrencyFormat(checkOutResponse.getBagTotal()));
                            ((TextView) mContentView.findViewById(R.id.bagDiscount)).setText(Utility.getAmountInCurrencyFormat(checkOutResponse.getBagDiscount()));
                            ((TextView) mContentView.findViewById(R.id.orderTotal)).setText(Utility.getAmountInCurrencyFormat(checkOutResponse.getDeliveryCharge()));
                            ((TextView) mContentView.findViewById(R.id.deliveryCharges)).setText(Utility.getAmountInCurrencyFormat(checkOutResponse.getOrderTotal()));
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void savePaymentServerCall(String txnId) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PaymentRequest request = new PaymentRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setTxnId(txnId);
                    request.setAddressId(mSelectedAddressId);
                    Call<PaymentResponse> call = RetrofitApi.getAppServicesObject().savePayment(request);
                    final Response<PaymentResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<PaymentResponse> response) {
                if (response.isSuccessful()) {
                    PaymentResponse paymentResponse = response.body();
                    if (paymentResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(paymentResponse.getErrorCode())) {
                            launchFragment(new ThankYouFragment(paymentResponse.getOrderId(),
                                                                paymentResponse.getExpectedDelivery()), true);
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.onlinePayment:
                mSelectedPaymentMode = onlineRadioButton.getText().toString();
                break;
            case R.id.cashPayment:
                mSelectedPaymentMode = cashRadioButton.getText().toString();
                break;
        }
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.checkout));
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_select_address);
        selectAddressRecyclerView = bottomSheetDialog.findViewById(R.id.selectAddressListRecycler);
        RadioGroup paymentMode = bottomSheetDialog.findViewById(R.id.paymentMode);
        if (null != paymentMode) {
            paymentMode.setOnCheckedChangeListener(this);
        }
        mSelectAddressListAdapter = new SelectAddressListAdapter();
        if (null != selectAddressRecyclerView) {
            selectAddressRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            selectAddressRecyclerView.setAdapter(mSelectAddressListAdapter);
        }
        onlineRadioButton = bottomSheetDialog.findViewById(R.id.onlinePayment);
        if (null != onlineRadioButton) {
            onlineRadioButton.setChecked(true);
        }
        cashRadioButton = bottomSheetDialog.findViewById(R.id.cashPayment);
        RecyclerView recyclerView = mContentView.findViewById(R.id.addedItemListRecycler);
        mCheckoutAdapter = new CheckoutAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mCheckoutAdapter);
    }

    private static class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.RecyclerViewHolder> {

        private List<CheckOutResponse.ProductItem> productList = new ArrayList<>();

        public void setProductList(List<CheckOutResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public CheckoutAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CheckoutAdapter.RecyclerViewHolder holder, int position) {
            CheckOutResponse.ProductItem item = productList.get(position);
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            holder.productName.setText(item.getProductName());
            holder.productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductPrice()));
            holder.mProductPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice()));
            String sizeStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.measureTextView.setText(sizeStr);
            holder.mQuantity.setText(item.getProductQuantity());
            double diff = Double.parseDouble(item.getProductPrice()) - Double.parseDouble(item.getProductSpecialPrice());
            holder.saveAmount.setText(String.valueOf(diff));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        private static class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private TextView mQuantity;
            private TextView mProductPrice;
            private ImageView productImage;
            private TextView productName;
            private TextView saveAmount;
            private TextView measureTextView;
            private TextView productOriginalPrice;

            @SuppressLint("SetTextI18n")
            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                measureTextView = itemView.findViewById(R.id.measureTextView);
                productOriginalPrice = itemView.findViewById(R.id.mrpPrice);
                saveAmount = itemView.findViewById(R.id.amountSaved);
                mQuantity = itemView.findViewById(R.id.productQuantity);
                productName = itemView.findViewById(R.id.productName);
                mProductPrice = itemView.findViewById(R.id.amountPaid);
                productImage = itemView.findViewById(R.id.productImage);
            }
        }
    }

    @Override
    public void onPaymentSuccess(String txn) {
        stopProgress();
        updateOnUiThread(() -> savePaymentServerCall(txn));
    }

    @Override
    public void onPaymentError(int i, String s) {
        stopProgress();
        showToast("FAILED :: " + i + " :: " + s);
    }

    private void fetchAddressServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<AddressResponse> call = RetrofitApi.getAppServicesObject().fetchAddress(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
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
                            List<AddressResponse.AddressItem> addressList = addressResponse.getAddressList();
                            mSelectAddressListAdapter.setAddressList(addressList);
                            mSelectAddressListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private class SelectAddressListAdapter extends RecyclerView.Adapter<SelectAddressListAdapter.RecyclerViewHolder> {

        private List<AddressResponse.AddressItem> mAddressList = new ArrayList<>();

        public void setAddressList(List<AddressResponse.AddressItem> addressList) {
            mAddressList = addressList;
        }

        @NonNull
        @Override
        public SelectAddressListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_bottom_sheet_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectAddressListAdapter.RecyclerViewHolder holder, int position) {
            AddressResponse.AddressItem currentItem = mAddressList.get(position);
            holder.selectAddressListButton.setText(currentItem.getAddressStr());
            holder.selectAddressListButton.setChecked(currentItem.isDefaultAddress());
            if (currentItem.isDefaultAddress()) {
                mSelectedAddressId = currentItem.getAddressId();
            }
        }

        @Override
        public int getItemCount() {
            return mAddressList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private RadioButton selectAddressListButton;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                selectAddressListButton = itemView.findViewById(R.id.selectAddressButton);
                selectAddressListButton.setOnClickListener(view -> {
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
