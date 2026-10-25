package com.example.e_commerceapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final Context context;
    private List<Order> orderList = new ArrayList<>();
    private final OnOrderClickListener listener;

    public OrderAdapter(Context context, OnOrderClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderRef.setText(order.getOrderReference());
        holder.tvOrderAmount.setText(String.format(Locale.US, "KES %.2f", order.getTotalAmount()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
        holder.tvOrderDate.setText("Date: " + sdf.format(new Date(order.getOrderDate())));

        String status = order.getOrderStatus() != null ? order.getOrderStatus() : "PENDING";
        holder.tvOrderStatusChip.setText(status);

        if ("PAID".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
            holder.tvOrderStatusChip.setBackgroundColor(Color.parseColor("#2E7D32"));
        } else if ("PENDING_PAYMENT".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) {
            holder.tvOrderStatusChip.setBackgroundColor(Color.parseColor("#ED6C02"));
        } else if ("CANCELLED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
            holder.tvOrderStatusChip.setBackgroundColor(Color.parseColor("#D32F2F"));
        } else {
            holder.tvOrderStatusChip.setBackgroundColor(Color.parseColor("#0288D1"));
        }

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderRef, tvOrderStatusChip, tvOrderDate, tvOrderAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderRef = itemView.findViewById(R.id.tvOrderRef);
            tvOrderStatusChip = itemView.findViewById(R.id.tvOrderStatusChip);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
        }
    }
}