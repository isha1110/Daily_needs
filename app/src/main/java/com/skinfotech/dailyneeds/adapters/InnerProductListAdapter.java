package com.skinfotech.dailyneeds.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.dailyneeds.HomeActivity;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.fragments.ProductDetailFragment;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.responses.InnerCategoryProduct;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.skinfotech.dailyneeds.Constants.SHARED_PREF_NAME;
import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class InnerProductListAdapter extends RecyclerView.Adapter<InnerProductListAdapter.InnerProductListViewHolder> {

    private HomeActivity mActivity;
    private List<InnerCategoryProduct> innerCategoryProducts = new ArrayList<>();

    public InnerProductListAdapter(HomeActivity mActivity, List<InnerCategoryProduct> innerCategoryProducts) {
        this.mActivity = mActivity;
        this.innerCategoryProducts = innerCategoryProducts;
    }

    @NonNull
    @Override
    public InnerProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item, parent, false);
        return new InnerProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerProductListViewHolder holder, int position) {
        InnerCategoryProduct innerCategoryProduct = innerCategoryProducts.get(position);
        holder.productDiscount.setText(innerCategoryProduct.getmProductDiscount().concat("% OFF"));
        if(innerCategoryProduct.getmProductImage() != null)
            Picasso.get().load(innerCategoryProduct.getmProductImage()).placeholder(R.drawable.icon_aata).into(holder.productImage);
        holder.productSellingPrice.setText(Utility.getAmountInCurrencyFormat(innerCategoryProduct.getmProductSpecialPrice()));
        holder.productMRP.setText(Utility.getAmountInCurrencyFormat(innerCategoryProduct.getmProductMRP()));
        holder.productName.setText(innerCategoryProduct.getmProductName());
        holder.productMeasure.setText(innerCategoryProduct.getmProductMeasure());
        holder.productContainer.setOnClickListener(v -> {
            CommonProductRequest request = new CommonProductRequest();
            request.setUserId(mActivity.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).getString(USER_ID, ""));
            request.setProductsId(innerCategoryProduct.getmProductId());
            request.setProductsSizeId(innerCategoryProduct.getProductMeasureId());
            mActivity.launchFragment(new ProductDetailFragment(request), true);
        });
    }

    @Override
    public int getItemCount() {
        return innerCategoryProducts.size();
    }

    public class InnerProductListViewHolder extends RecyclerView.ViewHolder {

        private TextView productDiscount;
        private ImageView productImage;
        private TextView productSellingPrice;
        private TextView productMRP;
        private TextView productName;
        private TextView productMeasure;
        private ImageView productAddToCart;
        private ConstraintLayout productContainer;
        public InnerProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            productDiscount = itemView.findViewById(R.id.textView9);
            productImage = itemView.findViewById(R.id.productImage);
            productSellingPrice = itemView.findViewById(R.id.productPrice);
            productMRP = itemView.findViewById(R.id.textView40);
            productName = itemView.findViewById(R.id.productName);
            productMeasure = itemView.findViewById(R.id.productQuantity);
            productAddToCart = itemView.findViewById(R.id.addToCart);
            productContainer = itemView.findViewById(R.id.productContainer);
            productAddToCart.setOnClickListener(view -> {
                InnerCategoryProduct item = innerCategoryProducts.get(getAdapterPosition());
                CommonProductRequest request = new CommonProductRequest();
                request.setUserId(USER_ID);
                request.setProductsId(item.getmProductId());
                request.setProductsQuantity(String.valueOf(item.getCount()));
                request.setProductsSizeId(item.getProductMeasureId());
            });
        }
    }
}
