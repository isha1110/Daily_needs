package com.skinfotech.dailyneeds.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.OrderDetailRequest;
import com.skinfotech.dailyneeds.models.responses.OrderDetailResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFragment extends BaseFragment {

    private TrackOrderListAdapter mTrackOrderListAdapter;
    private String mOrderId = "";
    private String mOrderDisplayId = "";
    private TextView cartTotalPrice;
    private TextView youSaved;

    public OrderDetailFragment(String orderId, String orderDisplayId) {
        mOrderId = orderId;
        mOrderDisplayId = orderDisplayId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        setupUI();
        fetchOrderDetailsServerCall();
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.downloadInvoiceTextView){
            Uri uri = Uri.parse("https://www.desibazaar.co.in/panel/user/order-details.php?orderId="+mOrderId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        mActivity.hideCartIcon();
        hideKeyboard();
        mActivity.hideFilterIcon();
    }

    private void fetchOrderDetailsServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderDetailRequest request = new OrderDetailRequest();
                    request.setOrderId(mOrderId);
                    Call<OrderDetailResponse> call = RetrofitApi.getAppServicesObject().fetchOrderDetails(request);
                    final Response<OrderDetailResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<OrderDetailResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    OrderDetailResponse productResponse = response.body();
                    if (productResponse != null) {
                        if (productResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            List<OrderDetailResponse.ProductItem> orderList = productResponse.getProductList();
                            mTrackOrderListAdapter.setOrderList(orderList);
                            mTrackOrderListAdapter.notifyDataSetChanged();
                            cartTotalPrice.setText("Cart Total : "+getString(R.string.ruppee_symbol) + Utility.getAmountInCurrencyFormat(productResponse.getTotalPrice()));
                            youSaved.setText("You Saved : "+getString(R.string.ruppee_symbol) + Utility.getAmountInCurrencyFormat(productResponse.getSavePrice()));
                        }
                    }
                }
            }
        }).start();
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mOrderDisplayId);
        mActivity.isToggleButtonEnabled(true);
        RecyclerView recyclerView = mContentView.findViewById(R.id.productRecyclerView);
        cartTotalPrice = mContentView.findViewById(R.id.cartTotalPrice);
        youSaved = mContentView.findViewById(R.id.youSaved);
        mTrackOrderListAdapter = new TrackOrderListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mTrackOrderListAdapter);
    }

    private class TrackOrderListAdapter extends RecyclerView.Adapter<TrackOrderListAdapter.RecyclerViewHolder> {

        private List<OrderDetailResponse.ProductItem> orderList = new ArrayList<>();

        public void setOrderList(List<OrderDetailResponse.ProductItem> orderList) {
            this.orderList = orderList;
        }

        @NonNull
        @Override
        public TrackOrderListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TrackOrderListAdapter.RecyclerViewHolder holder, int position) {
            OrderDetailResponse.ProductItem item = orderList.get(position);
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            holder.productName.setText(item.getProductName());
            holder.productQuantity.setText(item.getProductQuantity());
            String sizeStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.productSize.setText(sizeStr);
            holder.amountPaidSpecial.setText(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice()));
            holder.mrpPriceOriginal.setText(Utility.getAmountInCurrencyFormat(item.getProductPrice()));
            double diff = Double.parseDouble(item.getProductPrice()) - Double.parseDouble(item.getProductSpecialPrice());
            holder.amountSaved.setText(getString(R.string.ruppee_symbol) + " " + diff);
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView productImage;
            private TextView productName;
            private TextView productSize;
            private TextView productQuantity;
            private TextView amountPaidSpecial;
            private TextView mrpPriceOriginal;
            private TextView amountSaved;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                amountSaved = itemView.findViewById(R.id.amountSaved);
                mrpPriceOriginal = itemView.findViewById(R.id.mrpPrice);
                amountPaidSpecial = itemView.findViewById(R.id.amountPaid);
                productQuantity = itemView.findViewById(R.id.productQuantity);
                productSize = itemView.findViewById(R.id.productSize);
                productImage = itemView.findViewById(R.id.productImage);
                productName = itemView.findViewById(R.id.productName);
            }
        }
    }
}
