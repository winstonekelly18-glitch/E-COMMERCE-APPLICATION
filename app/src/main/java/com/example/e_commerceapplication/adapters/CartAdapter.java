package com.example.e_commerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.database.LocalCartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying shopping cart items with optimized Glide caching.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartChangeListener {
        void onQuantityChange(LocalCartItem item, int newQuantity);
        void onItemRemove(LocalCartItem item);
    }

    private final Context context;
    private List<LocalCartItem> cartItems = new ArrayList<>();
    private final OnCartChangeListener listener;

    public CartAdapter(Context context, OnCartChangeListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCartItems(List<LocalCartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        LocalCartItem item = cartItems.get(position);

        holder.tvCartItemName.setText(item.getName());
        holder.tvCartUnitPrice.setText(String.format(Locale.US, "Unit: KES %.2f", item.getPrice()));
        holder.tvCartSubtotal.setText(String.format(Locale.US, "Subtotal: KES %.2f", item.getSubtotal()));
        holder.tvCartQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(context)
                .load(item.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(200, 200)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.ivCartItemImage);

        holder.btnIncreaseQty.setOnClickListener(v -> listener.onQuantityChange(item, item.getQuantity() + 1));
        holder.btnDecreaseQty.setOnClickListener(v -> listener.onQuantityChange(item, item.getQuantity() - 1));
        holder.btnRemoveItem.setOnClickListener(v -> listener.onItemRemove(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCartItemImage;
        TextView tvCartItemName, tvCartUnitPrice, tvCartSubtotal, tvCartQuantity;
        ImageButton btnDecreaseQty, btnIncreaseQty, btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartItemImage = itemView.findViewById(R.id.ivCartItemImage);
            tvCartItemName = itemView.findViewById(R.id.tvCartItemName);
            tvCartUnitPrice = itemView.findViewById(R.id.tvCartUnitPrice);
            tvCartSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnDecreaseQty = itemView.findViewById(R.id.btnDecreaseQty);
            btnIncreaseQty = itemView.findViewById(R.id.btnIncreaseQty);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}