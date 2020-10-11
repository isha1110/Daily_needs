package com.skinfotech.dailyneeds.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.models.responses.InnerCategoryProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InnerProductListAdapter extends RecyclerView.Adapter<InnerProductListAdapter.InnerProductListViewHolder> {

    private List<InnerCategoryProduct> innerCategoryProducts = new ArrayList<>();

    public InnerProductListAdapter(List<InnerCategoryProduct> innerCategoryProducts) {
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
        holder.productSellingPrice.setText(String.valueOf(innerCategoryProduct.getmProductSpecialPrice()));
        holder.productMRP.setText(String.valueOf(innerCategoryProduct.getmProductMRP()));
        holder.productName.setText(innerCategoryProduct.getmProductName());
        holder.productMeasure.setText(innerCategoryProduct.getmProductMeasure());
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

        public InnerProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            productDiscount = itemView.findViewById(R.id.textView9);
            productImage = itemView.findViewById(R.id.productImage);
            productSellingPrice = itemView.findViewById(R.id.productPrice);
            productMRP = itemView.findViewById(R.id.textView40);
            productName = itemView.findViewById(R.id.productName);
            productMeasure = itemView.findViewById(R.id.productQuantity);
            productAddToCart = itemView.findViewById(R.id.addToCart);
        }
    }
}
