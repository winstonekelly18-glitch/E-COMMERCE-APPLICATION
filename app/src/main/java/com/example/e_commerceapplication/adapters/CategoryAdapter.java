package com.example.e_commerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * Adapter for horizontal category filter chips on the main product catalogue screen.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategorySelectListener {
        void onCategorySelected(String category);
    }

    private final Context context;
    private final List<String> categories;
    private final OnCategorySelectListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<String> categories, OnCategorySelectListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.chipCategory.setText(category);
        holder.chipCategory.setChecked(position == selectedPosition);

        holder.chipCategory.setOnClickListener(v -> {
            int previousPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPos);
            notifyItemChanged(selectedPosition);
            listener.onCategorySelected(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Chip chipCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            chipCategory = itemView.findViewById(R.id.chipCategory);
        }
    }
}