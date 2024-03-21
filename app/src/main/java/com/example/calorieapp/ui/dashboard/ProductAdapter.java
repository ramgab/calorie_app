package com.example.calorieapp.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<String> productNames;
    private List<String> filteredProductNames;  // Добавлен список для хранения отфильтрованных продуктов
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(List<String> productNames, OnItemClickListener onItemClickListener) {
        this.productNames = productNames;
        this.filteredProductNames = new ArrayList<>(productNames);  // Инициализируем фильтрованный список
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        String productName = filteredProductNames.get(position);  // Используем отфильтрованный список

        // Привязка данных к ViewHolder
        holder.bind(productName);

        // Установите слушатель щелчка для элемента списка
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(productName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredProductNames.size();  // Используем размер отфильтрованного списка
    }

    // ViewHolder для элемента списка
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView productNameTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
        }

        public void bind(String productName) {
            // Установка данных в элементы интерфейса
            productNameTextView.setText(productName);
            // Добавьте другие элементы, если необходимо
        }
    }

    // Интерфейс для обработки события щелчка
    public interface OnItemClickListener {
        void onItemClick(String productName);
    }

    // Добавлен метод для фильтрации списка продуктов
    public void filterList(List<String> filteredList) {
        filteredProductNames = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }
}
