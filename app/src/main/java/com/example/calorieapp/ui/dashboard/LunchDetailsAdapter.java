package com.example.calorieapp.ui.dashboard;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.calorieapp.ui.dashboard.LunchDatabaseHelper.COLUMN_CALORIES;
import static com.example.calorieapp.ui.dashboard.LunchDatabaseHelper.COLUMN_DATE;
import static com.example.calorieapp.ui.dashboard.LunchDatabaseHelper.TABLE_LUNCH;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;

import java.util.Locale;

public class LunchDetailsAdapter extends RecyclerView.Adapter<LunchDetailsAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private double deletedCalories; // Переменная для хранения значения калорий удаляемой карточки
    private String deletedDate; // Переменная для хранения значения даты удаляемой карточки

    public LunchDetailsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lunch_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // Получите данные из курсора и установите их в представления элемента списка
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_PRODUCT_NAME));
            @SuppressLint("Range") double grams = cursor.getDouble(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_GRAMS));
            @SuppressLint("Range") double calories = cursor.getDouble(cursor.getColumnIndex(COLUMN_CALORIES));
            @SuppressLint("Range") double protein = cursor.getDouble(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_PROTEIN));
            @SuppressLint("Range") double fat = cursor.getDouble(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_FAT));
            @SuppressLint("Range") double carbohydrate = cursor.getDouble(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_CARBOHYDRATE));

            holder.textViewProductName.setText(productName);
            holder.textViewGrams.setText(String.format(Locale.getDefault(), "Grams: %.2f г.", grams));
            holder.textViewCalories.setText(String.format(Locale.getDefault(), "Calories: %.2f ккал", calories));
            holder.textViewProtein.setText(String.format(Locale.getDefault(), "Protein: %.2f г.", protein));
            holder.textViewFat.setText(String.format(Locale.getDefault(), "Fat: %.2f г.", fat));
            holder.textViewCarbohydrate.setText(String.format(Locale.getDefault(), "Carbohydrate: %.2f г.", carbohydrate));

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
                        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_ID));
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
            deletedCalories = cursor.getDouble(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_CALORIES));
            deletedDate = cursor.getString(cursor.getColumnIndex(LunchDatabaseHelper.COLUMN_DATE));
            // Создаем объект dbHelper
            LunchDatabaseHelper dbHelper = new LunchDatabaseHelper(context);
            // Получаем базу данных в режиме для записи
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Выполняем удаление записи по id
            db.delete(LunchDatabaseHelper.TABLE_LUNCH,
                    LunchDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});



            // Закрываем базу данных
            db.close();
            // Обновляем сумму калорий в таблице calories_summary
            dbHelper.updateCaloriesSummaryLunch(deletedDate);

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
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductNameLunch);
            textViewGrams = itemView.findViewById(R.id.textViewGramsLunch);
            textViewCalories = itemView.findViewById(R.id.textViewCaloriesLunch);
            textViewProtein = itemView.findViewById(R.id.textViewProteinLunch);
            textViewFat = itemView.findViewById(R.id.textViewFatLunch);
            textViewCarbohydrate = itemView.findViewById(R.id.textViewCarbohydrateLunch);
            deleteButton = itemView.findViewById(R.id.deleteButtonLunch);
        }
    }



}
