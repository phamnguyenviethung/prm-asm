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

            // Set single selection mode for chip group
            chipGroupValues.setSingleSelection(true);

            // Add chips for each value
            for (VariantValue value : option.getValues()) {
                Chip chip = new Chip(context);
                chip.setText(value.getValue());
                chip.setCheckable(true);
                chip.setId(View.generateViewId()); // Generate unique ID for each chip

                // Style the chip
                chip.setTextSize(14f);
                chip.setTextColor(context.getResources().getColorStateList(android.R.color.black, null));
                chip.setChipBackgroundColorResource(R.color.gray_300);
                chip.setCheckedIconVisible(false);
                chip.setChipStrokeWidth(2f);
                chip.setChipStrokeColorResource(R.color.gray_400);

                // Set checked state colors
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        chip.setChipBackgroundColorResource(R.color.success);
                        chip.setTextColor(context.getResources().getColor(R.color.white, null));
                        chip.setChipStrokeColorResource(R.color.green_500);
                    } else {
                        chip.setChipBackgroundColorResource(R.color.gray_300);
                        chip.setTextColor(context.getResources().getColor(android.R.color.black, null));
                        chip.setChipStrokeColorResource(R.color.gray_400);
                    }
                });

                chipGroupValues.addView(chip);
            }

            // Set listener for chip group selection
            chipGroupValues.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty() && listener != null) {
                    int checkedId = checkedIds.get(0);
                    Chip selectedChip = group.findViewById(checkedId);
                    if (selectedChip != null) {
                        // Find the corresponding value ID
                        int chipIndex = group.indexOfChild(selectedChip);
                        if (chipIndex >= 0 && chipIndex < option.getValues().size()) {
                            VariantValue selectedValue = option.getValues().get(chipIndex);
                            listener.onOptionSelected(option.getId(), selectedValue.getId());
                        }
                    }
                }
            });
        }
    }
}