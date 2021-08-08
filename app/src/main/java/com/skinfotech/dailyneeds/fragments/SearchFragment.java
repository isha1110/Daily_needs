Hpackage com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.SearchRequest;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class SearchFragment extends BaseFragment {

    private SearchItemListAdapter mSearchItemListAdapter;
    private EditText searchEditText;
    private static final String TAG = "SearchFragment";
    private TextView textView;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_search, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        textView = mContentView.findViewById(R.id.noItemfoundTextView);
        RecyclerView recyclerView = mContentView.findViewById(R.id.searchItemListRecyclerView);
        mSearchItemListAdapter = new SearchItemListAdapter();
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mSearchItemListAdapter);
        searchEditText = mContentView.findViewById(R.id.searchEditText);
        mContentView.findViewById(R.id.searchImageView).setOnClickListener(view -> startSearching());
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startSearching();
                return true;
            }
            return false;
        });
        return mContentView;
    }

    private void startSearching() {
        String text = searchEditText.getText().toString();
        if (Utility.isNotEmpty(text)) {
            stopProgress();
            fetchAllProductsServerCall(text);
        }
    }

    private void fetchAllProductsServerCall(String searchStr) {
        hideKeyboard();
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchRequest request = new SearchRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setSearchStr(searchStr);
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().fetchSearchProducts(request);
                    final Response<ProductResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            List<ProductResponse.ProductItem> productList = categoryResponse.getProductList();
                            if (!Utility.isEmpty(productList)) {
                                mSearchItemListAdapter.setProductList(productList);
                                textView.setVisibility(View.GONE);
                            } else {
                                mSearchItemListAdapter.setProductList(new ArrayList<>());
                                textView.setVisibility(View.VISIBLE);
                            }
                            showToast(productList.size() + " Products Found");
                            mSearchItemListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchEditText != null && Utility.isNotEmpty(searchEditText.getText().toString())) {
            mContentView.findViewById(R.id.searchImageView).callOnClick();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        mActivity.hideSearchIcon();
        hideKeyboard();
        if (null != searchEditText) {
            String text = searchEditText.getText().toString();
            if (Utility.isNotEmpty(text)) {
                fetchAllProductsServerCall(text);
            }
        }
    }

    private class SearchItemListAdapter extends RecyclerView.Adapter<SearchItemListAdapter.RecyclerViewHolder> {

        private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public SearchItemListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchItemListAdapter.RecyclerViewHolder holder, int position) {
            ProductResponse.ProductItem item = productList.get(position);
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            String measureStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.quantityValue.setText(String.valueOf(item.getCount()));
            holder.productMeasure.setText(measureStr);
            holder.productName.setText(item.getProductName());
            holder.productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductPrice()));
            holder.productSpecialPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice()));
            holder.saveAmount.setText(item.getProductDiscount().concat("% OFF"));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView productImage;
            private TextView productSpecialPrice;
            private TextView productOriginalPrice;
            private TextView productName;
            private TextView productMeasure;
            private TextView quantityValue;
            private TextView saveAmount;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                quantityValue = itemView.findViewById(R.id.quantityValue);
                saveAmount = itemView.findViewById(R.id.percentOFFTextView);
                productName = itemView.findViewById(R.id.productName);
                productMeasure = itemView.findViewById(R.id.measureTextView);
                productOriginalPrice = itemView.findViewById(R.id.productMrpPrice);
                productSpecialPrice = itemView.findViewById(R.id.productPrice);
                productImage = itemView.findViewById(R.id.productImage);
                ImageView minusQuantity = itemView.findViewById(R.id.minusQuantity);
                ImageView addQuantity = itemView.findViewById(R.id.addQuantity);
                TextView addToCart = itemView.findViewById(R.id.addToCart);
                addToCart.setOnClickListener(view -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    request.setProductsSizeId(item.getProductPUId());
                    addToCartServerCall(request);
                });
                productImage.setOnClickListener(view -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    launchFragment(new ProductDetailFragment(request), true);
                });
                minusQuantity.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    int counter = item.getCount();
                    if (counter == 1) {
                        return;
                    }
                    counter--;
                    double originalPrice = counter * Double.parseDouble(item.getProductPrice());
                    double discountPrice = counter * Double.parseDouble(item.getProductSpecialPrice());
                    productSpecialPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                    productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                    quantityValue.setText(String.valueOf(counter));
                    item.setCount(counter);
                });
                addQuantity.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    int counter = item.getCount();
                    counter++;
                    double originalPrice = counter * Double.parseDouble(item.getProductPrice());
                    double discountPrice = counter * Double.parseDouble(item.getProductSpecialPrice());
                    productSpecialPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                    productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                    quantityValue.setText(String.valueOf(counter));
                    item.setCount(counter);
                });
            }
        }
    }
}
