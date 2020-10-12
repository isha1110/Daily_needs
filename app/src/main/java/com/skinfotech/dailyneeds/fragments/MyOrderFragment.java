package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.AddressResponse;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.OrderDetailRequest;
import com.skinfotech.dailyneeds.models.responses.MyOrderResponse;
import com.skinfotech.dailyneeds.models.responses.PaymentResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class MyOrderFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private MyOrderListAdapter mMyOrderListAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private String mSelectedAddressId = "";
    private String mSelectedOrderId;
    private String mSelectedOrderAmount;
    private String mSelectedPaymentMode = "";
    private static final String TAG = "MyOrderFragment";
    private SelectAddressListAdapter mSelectAddressListAdapter;
    private RadioButton onlineRadioButton;
    private RadioButton cashRadioButton;
    private View noItemFoundContainer;
    private RecyclerView myOrderRecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_my_order, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.myorders));
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        mSwipeRefreshLayout = mContentView.findViewById(R.id.swipeRefreshLayout);
        bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_select_address);
        RecyclerView selectAddressRecyclerView = bottomSheetDialog.findViewById(R.id.selectAddressListRecycler);
        fetchAddressServerCall();
        mSelectAddressListAdapter = new SelectAddressListAdapter();
        RadioGroup paymentMode = bottomSheetDialog.findViewById(R.id.paymentMode);
        noItemFoundContainer = mContentView.findViewById(R.id.noItemFoundContainer);
        if (null != paymentMode) {
            paymentMode.setOnCheckedChangeListener(this);
        }
        if (null != selectAddressRecyclerView) {
            selectAddressRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            selectAddressRecyclerView.setAdapter(mSelectAddressListAdapter);
        }
        onlineRadioButton = bottomSheetDialog.findViewById(R.id.onlinePayment);
        if (null != onlineRadioButton) {
            onlineRadioButton.setChecked(true);
        }
        cashRadioButton = bottomSheetDialog.findViewById(R.id.cashPayment);
        setupUI();
        fetchMyOrdersServerCall();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            fetchMyOrdersServerCall();
            fetchAddressServerCall();
            mSwipeRefreshLayout.setRefreshing(false);
        });
        return mContentView;
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

    private void setupUI() {
        myOrderRecycler = mContentView.findViewById(R.id.myOrderRecycler);
        mMyOrderListAdapter = new MyOrderListAdapter();
        myOrderRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        myOrderRecycler.setAdapter(mMyOrderListAdapter);
    }

    private void fetchMyOrdersServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<MyOrderResponse> call = RetrofitApi.getAppServicesObject().fetchMyOrders(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<MyOrderResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<MyOrderResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    MyOrderResponse orderResponse = response.body();
                    if (orderResponse != null) {
                        if (orderResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            noItemFoundContainer.setVisibility(View.GONE);
                            myOrderRecycler.setVisibility(View.VISIBLE);
                            List<MyOrderResponse.OrderItem> orderList = orderResponse.getOrderList();
                            mMyOrderListAdapter.setOrderList(orderList);
                        } else {
                            mMyOrderListAdapter.setOrderList(new ArrayList<>());
                            noItemFoundContainer.setVisibility(View.VISIBLE);
                            myOrderRecycler.setVisibility(View.GONE);
                        }
                        mMyOrderListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.proceedToPay) {
            if (Utility.isNotEmpty(mSelectedPaymentMode) && mSelectedPaymentMode.equalsIgnoreCase(getString(R.string.cash_on_delivery))) {
                repeatOrderServerCall("");
            } else {
                showProgress();
                mActivity.startPayment(mSelectedOrderAmount);
            }
            bottomSheetDialog.hide();
        }
    }

    @Override
    public void onPaymentSuccess(String txn) {
        stopProgress();
        updateOnUiThread(() -> repeatOrderServerCall(txn));
    }

    @Override
    public void onPaymentError(int i, String s) {
        stopProgress();
    }

    private void cancelOrderServerCall(String orderId) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderDetailRequest request = new OrderDetailRequest();
                    request.setOrderId(orderId);
                    Call<MyOrderResponse> call = RetrofitApi.getAppServicesObject().cancelOrders(request);
                    final Response<MyOrderResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<MyOrderResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    MyOrderResponse orderResponse = response.body();
                    if (orderResponse != null) {
                        showToast(orderResponse.getErrorMessage());
                        if (orderResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            List<MyOrderResponse.OrderItem> orderList = orderResponse.getOrderList();
                            mMyOrderListAdapter.setOrderList(orderList);
                            mMyOrderListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }).start();
    }

    private void repeatOrderServerCall(String transactionId) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderDetailRequest request = new OrderDetailRequest();
                    request.setOrderId(mSelectedOrderId);
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setAddressId(mSelectedAddressId);
                    request.setTxnId(transactionId);
                    Call<PaymentResponse> call = RetrofitApi.getAppServicesObject().repeatOrders(request);
                    final Response<PaymentResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<PaymentResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    PaymentResponse paymentResponse = response.body();
                    if (paymentResponse != null) {
                        showToast(paymentResponse.getErrorMessage());
                        if (paymentResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            launchFragment(new ThankYouFragment(paymentResponse.getOrderId(), paymentResponse.getExpectedDelivery()), true);
                        }
                    }
                }
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.showCartIcon();
        mActivity.hideSearchIcon();
        hideKeyboard();
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

    private class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.RecyclerViewHolder> {

        private List<MyOrderResponse.OrderItem> mOrderList = new ArrayList<>();

        public void setOrderList(List<MyOrderResponse.OrderItem> orderList) {
            mOrderList = orderList;
        }

        @NonNull
        @Override
        public MyOrderListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_list_items, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyOrderListAdapter.RecyclerViewHolder holder, int position) {
            MyOrderResponse.OrderItem item = mOrderList.get(position);
            holder.orderIdNumber.setText(item.getOrderId());
            holder.orderPlaced.setText(item.getDateTime());
            holder.paymentMode.setText(item.getPaymentId());
            String expectedDeliveryStr = item.getDeliveryFlag();
            if (item.isCancel()) {
                holder.repeatOrderButton.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.button_bg));
                holder.repeatOrderButton.setText(getString(R.string.repeat_order));
                holder.statusTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.red));
                holder.orderStatusImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.cancelled_stamp));
            } else if (Constants.IExpectedDeliveryModes.DELIVERED.equalsIgnoreCase(expectedDeliveryStr)) {
                holder.repeatOrderButton.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.button_bg));
                holder.repeatOrderButton.setText(getString(R.string.repeat_order));
                holder.orderStatusImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.delivered_stamp));
                holder.statusTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.green_color));
            } else {
                holder.repeatOrderButton.setText(getString(R.string.cancel_text_order_page));
                holder.repeatOrderButton.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.button_cancel_bg));
                holder.statusTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.grey));
            }
            holder.statusTextView.setText(item.getExpectedDelivery());
            holder.pricePaid.setText(Utility.getAmountInCurrencyFormat(item.getTotalAmount()));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mOrderList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView orderStatusImageView;
            private TextView orderIdNumber;
            private TextView orderPlaced;
            private TextView pricePaid;
            private TextView paymentMode;
            private TextView statusTextView;
            private Button repeatOrderButton;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                repeatOrderButton = itemView.findViewById(R.id.repeatOrderButton);
                orderStatusImageView = itemView.findViewById(R.id.orderStatusImageView);
                statusTextView = itemView.findViewById(R.id.status);
                paymentMode = itemView.findViewById(R.id.paymentMode);
                pricePaid = itemView.findViewById(R.id.pricePaid);
                orderPlaced = itemView.findViewById(R.id.orderPlaced);
                orderIdNumber = itemView.findViewById(R.id.orderIdNumber);
                ConstraintLayout orderItemContainer = itemView.findViewById(R.id.orderItemContainer);
                orderItemContainer.setOnClickListener(v -> {
                    MyOrderResponse.OrderItem orderItem = mOrderList.get(getAdapterPosition());
                    launchFragment(new OrderDetailFragment(orderItem.getId(), orderItem.getOrderId()), true);
                });
                repeatOrderButton.setOnClickListener(v -> {
                    if (repeatOrderButton.getText().toString().equalsIgnoreCase(getString(R.string.cancel_text_order_page))) {
                        MyOrderResponse.OrderItem orderItem = mOrderList.get(getAdapterPosition());
                        showCancellationDialog(orderItem.getId());
                    } else if (repeatOrderButton.getText().toString().equalsIgnoreCase(getString(R.string.repeat_order))) {
                        MyOrderResponse.OrderItem orderItem = mOrderList.get(getAdapterPosition());
                        mSelectedOrderId = orderItem.getId();
                        mSelectedOrderAmount = orderItem.getTotalAmount();
                        bottomSheetDialog.show();
                    }
                });
            }

            private void showCancellationDialog(String orderId) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(getString(R.string.cancellation_message));
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (dialogInterface, i) -> cancelOrderServerCall(orderId));
                builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
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
