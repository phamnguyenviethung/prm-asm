package com.example.myapplication.ui.orderdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentOrderDetailBinding;
import com.example.myapplication.dto.response.OrderDetailResponse;
import com.example.myapplication.dto.response.OrderItem;
import com.example.myapplication.util.DateUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {

    private FragmentOrderDetailBinding binding;
    private OrderDetailResponse orderDetail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get order detail from arguments
        if (getArguments() != null) {
            orderDetail = (OrderDetailResponse) getArguments().getSerializable("orderDetail");
            if (orderDetail != null) {
                populateOrderDetail();
            }
        }

        // Setup home button
        binding.btnGoHome.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });

        return root;
    }

    private void populateOrderDetail() {
        // Order info
        binding.tvOrderCode.setText(orderDetail.getOrderCode());
        binding.tvOrderStatus.setText(orderDetail.getStatus());
        binding.tvOrderDate.setText(DateUtils.formatToVietnamTime(orderDetail.getCreatedAt()));

        // Customer info
        binding.tvRecipientName.setText(orderDetail.getRecipientName());
        binding.tvPhone.setText(orderDetail.getPhone());
        binding.tvEmail.setText(orderDetail.getEmail());
        
        // Address
        String fullAddress = orderDetail.getStreetAddress() + ", " + 
                           orderDetail.getWard() + ", " + 
                           orderDetail.getDistrict() + ", " + 
                           orderDetail.getProvince();
        binding.tvAddress.setText(fullAddress);

        // Note
        if (orderDetail.getNote() != null && !orderDetail.getNote().trim().isEmpty()) {
            binding.tvNote.setText(orderDetail.getNote());
            binding.tvNote.setVisibility(View.VISIBLE);
            binding.tvNoteLabel.setVisibility(View.VISIBLE);
        } else {
            binding.tvNote.setVisibility(View.GONE);
            binding.tvNoteLabel.setVisibility(View.GONE);
        }

        // Payment method
        binding.tvPaymentMethod.setText(orderDetail.getPaymentMethod());

        // Order items
        populateOrderItems();

        // Order summary
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        binding.tvSubtotal.setText(formatter.format(orderDetail.getSubtotal()) + "đ");
        binding.tvShippingFee.setText(formatter.format(orderDetail.getShippingFee()) + "đ");
        binding.tvTotal.setText(formatter.format(orderDetail.getTotalAmount()) + "đ");
    }

    private void populateOrderItems() {
        LinearLayout itemsContainer = binding.layoutOrderItems;
        itemsContainer.removeAllViews();

        if (orderDetail.getItems() != null) {
            for (OrderItem item : orderDetail.getItems()) {
                View itemView = createOrderItemView(item);
                itemsContainer.addView(itemView);
            }
        }
    }

    private View createOrderItemView(OrderItem item) {
        View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_order_detail, binding.layoutOrderItems, false);

        ImageView ivProductImage = itemView.findViewById(R.id.ivProductImage);
        TextView tvProductName = itemView.findViewById(R.id.tvProductName);
        TextView tvVariantTitle = itemView.findViewById(R.id.tvVariantTitle);
        TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
        TextView tvPrice = itemView.findViewById(R.id.tvPrice);
        TextView tvLineTotal = itemView.findViewById(R.id.tvLineTotal);

        // Set product name
        tvProductName.setText(item.getProductName());

        // Set variant title
        if (item.getVariantTitle() != null && !item.getVariantTitle().trim().isEmpty()) {
            tvVariantTitle.setText(item.getVariantTitle());
            tvVariantTitle.setVisibility(View.VISIBLE);
        } else {
            tvVariantTitle.setVisibility(View.GONE);
        }

        // Set quantity
        tvQuantity.setText("x" + item.getQuantity());

        // Set price
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvPrice.setText(formatter.format(item.getPriceAtTime()) + "đ");
        tvLineTotal.setText(formatter.format(item.getLineTotal()) + "đ");

        // Load product image
        if (item.getThumbnail() != null && !item.getThumbnail().isEmpty()) {
            Glide.with(requireContext())
                    .load(item.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.placeholder_image);
        }

        return itemView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
