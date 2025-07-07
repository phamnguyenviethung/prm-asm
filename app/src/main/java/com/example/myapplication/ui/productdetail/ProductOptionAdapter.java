package com.example.myapplication.ui.productdetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.VariantOption;
import com.example.myapplication.model.VariantValue;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ProductOptionAdapter extends RecyclerView.Adapter<ProductOptionAdapter.OptionViewHolder> {

    private List<VariantOption> options = new ArrayList<>();
    private Context context;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String optionId, String valueId);
    }

    public ProductOptionAdapter(Context context, OnOptionSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setOptions(List<VariantOption> options) {
        this.options = options;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        VariantOption option = options.get(position);
        holder.bind(option);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvOptionName;
        ChipGroup chipGroupValues;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOptionName = itemView.findViewById(R.id.tvOptionName);
            chipGroupValues = itemView.findViewById(R.id.chipGroupValues);
        }

        public void bind(VariantOption option) {
            tvOptionName.setText(option.getName() + ":");
            
            // Clear previous chips
            chipGroupValues.removeAllViews();
            
            // Add chips for each value
            for (VariantValue value : option.getValues()) {
                Chip chip = new Chip(context);
                chip.setText(value.getValue());
                chip.setCheckable(true);

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && listener != null) {
                        listener.onOptionSelected(option.getId(), value.getId());
                    }
                });

                chipGroupValues.addView(chip);
            }
        }
    }
}