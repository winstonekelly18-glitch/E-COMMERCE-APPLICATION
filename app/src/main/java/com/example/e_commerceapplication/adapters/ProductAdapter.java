package com.example.e_commerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.models.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying products in a 2-column grid with optimized image caching.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    private final Context context;
    private List<Product> productList = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.productList = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductCategory.setText(product.getCategory());
        holder.tvProductPrice.setText(String.format(Locale.US, "KES %.2f", product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(400, 400)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.ivProductImage);

        if (product.getStockQuantity() <= 0) {
            holder.tvOutOfStockBadge.setVisibility(View.VISIBLE);
            holder.btnAddToCart.setEnabled(false);
            holder.btnAddToCart.setText("Out of Stock");
        } else {
            holder.tvOutOfStockBadge.setVisibility(View.GONE);
            holder.btnAddToCart.setEnabled(true);
            holder.btnAddToCart.setText("Add to Cart");
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductCategory, tvProductPrice, tvOutOfStockBadge;
        MaterialButton btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOutOfStockBadge = itemView.findViewById(R.id.tvOutOfStockBadge);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}