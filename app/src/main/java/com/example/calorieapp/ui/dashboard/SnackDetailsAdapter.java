package com.example.calorieapp.ui.dashboard;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.calorieapp.ui.dashboard.SnackDatabaseHelper.COLUMN_CALORIES;
import static com.example.calorieapp.ui.dashboard.SnackDatabaseHelper.COLUMN_DATE;
import static com.example.calorieapp.ui.dashboard.SnackDatabaseHelper.TABLE_SNACK;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;

import java.util.Locale;

public class SnackDetailsAdapter extends RecyclerView.Adapter<SnackDetailsAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private double deletedCalories; // Переменная для хранения значения калорий удаляемой карточки
    private String deletedDate; // Переменная для хранения значения даты удаляемой карточки

    public SnackDetailsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_snack_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // Получите данные из курсора и установите их в представления элемента списка
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_PRODUCT_NAME));
            @SuppressLint("Range") double grams = cursor.getDouble(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_GRAMS));
            @SuppressLint("Range") double calories = cursor.getDouble(cursor.getColumnIndex(COLUMN_CALORIES));
            @SuppressLint("Range") double protein = cursor.getDouble(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_PROTEIN));
            @SuppressLint("Range") double fat = cursor.getDouble(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_FAT));
            @SuppressLint("Range") double carbohydrate = cursor.getDouble(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_CARBOHYDRATE));

            holder.textViewProductName.setText(productName);
            holder.textViewGrams.setText(String.format(Locale.getDefault(), "%.2fг.", grams));
            holder.textViewCalories.setText(String.format(Locale.getDefault(), "%.2fккал", calories));
            holder.textViewProtein.setText(String.format(Locale.getDefault(), "Б: %.2fг.", protein));
            holder.textViewFat.setText(String.format(Locale.getDefault(), "Ж: %.2fг.", fat));
            holder.textViewCarbohydrate.setText(String.format(Locale.getDefault(), "У: %.2fг.", carbohydrate));

            // Находим кнопку удаления в макете элемента списка
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Получаем позицию элемента, который нужно удалить
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        // Перемещаем курсор на позицию элемента, который нужно удалить
                        cursor.moveToPosition(adapterPosition);
                        // Получаем id записи из курсора
                        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_ID));
                        // Вызываем метод для удаления элемента из базы данных
                        deleteItem(id, holder);
                    }
                }
            });
        }
    }

    @SuppressLint("Range")
    private void deleteItem(long id, ViewHolder holder) {
        if (holder != null) {
            // Получаем калории и дату удаляемой карточки перед удалением
            deletedCalories = cursor.getDouble(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_CALORIES));
            deletedDate = cursor.getString(cursor.getColumnIndex(SnackDatabaseHelper.COLUMN_DATE));
            // Создаем объект dbHelper
            SnackDatabaseHelper dbHelper = new SnackDatabaseHelper(context);
            // Получаем базу данных в режиме для записи
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Выполняем удаление записи по id
            db.delete(SnackDatabaseHelper.TABLE_SNACK,
                    SnackDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});



            // Закрываем базу данных
            db.close();
            // Обновляем сумму калорий в таблице calories_summary
            dbHelper.updateCaloriesSummarySnack(deletedDate);
            dbHelper.updateProteinSummarySnack(deletedDate);
            dbHelper.updateFatSummarySnack(deletedDate);
            dbHelper.updateCarbSummarySnack(deletedDate);

            // Обновите сумму калорий в таблице calories_summary


            // Создаем анимацию удаления
            ObjectAnimator anim = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1f, 0f);
            anim.setDuration(300); // Устанавливаем длительность анимации (в миллисекундах)
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // Уведомляем адаптер об изменении данных после завершения анимации
                    notifyDataSetChanged();
                }
            });
            anim.start(); // Запускаем анимацию
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductName;
        TextView textViewGrams;
        TextView textViewCalories;
        TextView textViewProtein;
        TextView textViewFat;
        TextView textViewCarbohydrate;
        ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductNameSnack);
            textViewGrams = itemView.findViewById(R.id.textViewGramsSnack);
            textViewCalories = itemView.findViewById(R.id.textViewCaloriesSnack);
            textViewProtein = itemView.findViewById(R.id.textViewProteinSnack);
            textViewFat = itemView.findViewById(R.id.textViewFatSnack);
            textViewCarbohydrate = itemView.findViewById(R.id.textViewCarbohydrateSnack);
            deleteButton = itemView.findViewById(R.id.deleteButtonSnack);
        }
    }



}
