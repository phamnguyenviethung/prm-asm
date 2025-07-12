package com.example.myapplication.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.dto.response.Cart.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvVariantTitle;
        private TextView tvPrice;
        private TextView tvQuantity;
        private TextView tvSubtotal;
        private ImageButton btnDecrease;
        private ImageButton btnIncrease;
        private ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvVariantTitle = itemView.findViewById(R.id.tvVariantTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(CartItem item) {
            // Set product name
            tvProductName.setText(item.getProductName());
            
            // Set variant title
            if (item.getVariantTitle() != null && !item.getVariantTitle().isEmpty()) {
                tvVariantTitle.setText(item.getVariantTitle());
                tvVariantTitle.setVisibility(View.VISIBLE);
            } else {
                tvVariantTitle.setVisibility(View.GONE);
            }

            // Set price
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String priceText = formatter.format(item.getFinalPrice()) + " VND";
            tvPrice.setText(priceText);

            // Set quantity
            tvQuantity.setText(String.valueOf(item.getQuantity()));

            // Set subtotal (price * quantity)
            int subtotal = item.getFinalPrice() * item.getQuantity();
            String subtotalText = formatter.format(subtotal) + " VND";
            tvSubtotal.setText(subtotalText);

            // Load product image
            String imageUrl = item.getThumbnail();
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_dashboard_black_24dp)
                        .error(R.drawable.ic_dashboard_black_24dp)
                        .centerCrop()
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_dashboard_black_24dp);
            }

            // Set click listeners
            btnDecrease.setOnClickListener(v -> {
                int currentQuantity = item.getQuantity();
                if (currentQuantity > 1 && listener != null) {
                    listener.onQuantityChanged(item, currentQuantity - 1);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                int currentQuantity = item.getQuantity();
                if (currentQuantity < item.getAvailableQuantity() && listener != null) {
                    listener.onQuantityChanged(item, currentQuantity + 1);
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(item);
                }
            });

            // Disable decrease button if quantity is 1
            btnDecrease.setEnabled(item.getQuantity() > 1);
            
            // Disable increase button if at max available quantity
            btnIncrease.setEnabled(item.getQuantity() < item.getAvailableQuantity());
        }
    }
}
