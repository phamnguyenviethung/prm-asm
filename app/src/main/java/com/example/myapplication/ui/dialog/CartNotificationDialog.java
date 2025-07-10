package com.example.myapplication.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class CartNotificationDialog extends DialogFragment {

    public interface CartNotificationListener {
        void onGoToCart();
        void onGoToHome();
    }

    private CartNotificationListener listener;
    private int itemCount;

    public static CartNotificationDialog newInstance(int itemCount) {
        CartNotificationDialog dialog = new CartNotificationDialog();
        Bundle args = new Bundle();
        args.putInt("itemCount", itemCount);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemCount = getArguments().getInt("itemCount", 0);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cart_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvMessage = view.findViewById(R.id.tvMessage);
        Button btnGoHome = view.findViewById(R.id.btnGoHome);
        Button btnGoToCart = view.findViewById(R.id.btnGoToCart);

        // Set message based on item count
        String message = itemCount == 1 
                ? "Bạn đang có 1 sản phẩm trong giỏ hàng" 
                : "Bạn đang có " + itemCount + " sản phẩm trong giỏ hàng";
        tvMessage.setText(message);

        // Set click listeners
        btnGoHome.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGoToHome();
            }
            dismiss();
        });

        btnGoToCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGoToCart();
            }
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CartNotificationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartNotificationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setCartNotificationListener(CartNotificationListener listener) {
        this.listener = listener;
    }
}
