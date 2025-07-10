package com.example.myapplication.ui.orderhistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.dto.response.OrderHistoryItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<OrderHistoryItem> orders = new ArrayList<>();
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderHistoryItem order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    public void setOrders(List<OrderHistoryItem> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistoryItem order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderCode;
        private TextView tvStatus;
        private TextView tvTotalAmount;
        private ImageView ivViewDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            ivViewDetail = itemView.findViewById(R.id.ivViewDetail);
        }

        public void bind(OrderHistoryItem order) {
            // Set order code
            tvOrderCode.setText(order.getOrderCode());

            // Set status
            tvStatus.setText(order.getStatus());

            // Set status color based on status
            switch (order.getStatus().toLowerCase()) {
                case "pending":
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.orange_500));
                    break;
                case "completed":
                case "delivered":
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.green_500));
                    break;
                case "cancelled":
                    tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.gray_600));
                    break;
            }

            // Set total amount
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvTotalAmount.setText(formatter.format(order.getTotalAmount()) + "đ");

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (onOrderClickListener != null) {
                    onOrderClickListener.onOrderClick(order);
                }
            });

            ivViewDetail.setOnClickListener(v -> {
                if (onOrderClickListener != null) {
                    onOrderClickListener.onOrderClick(order);
                }
            });
        }
    }
}