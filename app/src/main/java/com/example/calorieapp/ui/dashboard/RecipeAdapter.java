package com.example.calorieapp.ui.dashboard;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.calorieapp.ui.dashboard.RecipeDatabaseHelper.COLUMN_CALORIES;
import static com.example.calorieapp.ui.dashboard.RecipeDatabaseHelper.TABLE_RECIPE;

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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context context;
    private CreateRecipeFragment fragment;

    private String selectedDate;
    private View rootView; // добавляем поле для хранения rootView

    private Cursor cursor;
    private double deletedCalories; // Переменная для хранения значения калорий удаляемой карточки
    private String deletedDate; // Переменная для хранения значения даты удаляемой карточки

    public RecipeAdapter(Context context, CreateRecipeFragment fragment, View rootView) {
        this.context = context;
        this.fragment = fragment;
        this.rootView = rootView; // инициализируем rootView

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // Получите данные из курсора и установите их в представления элемента списка
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex(RecipeDatabaseHelper.COLUMN_PRODUCT_NAME));
            @SuppressLint("Range") double grams = cursor.getDouble(cursor.getColumnIndex(RecipeDatabaseHelper.COLUMN_GRAMS));
            @SuppressLint("Range") double calories = cursor.getDouble(cursor.getColumnIndex(COLUMN_CALORIES));
            @SuppressLint("Range") double protein = cursor.getDouble(cursor.getColumnIndex(RecipeDatabaseHelper.COLUMN_PROTEIN));
            @SuppressLint("Range") double fat = cursor.getDouble(cursor.getColumnIndex(RecipeDatabaseHelper.COLUMN_FAT));
            @SuppressLint("Range") double carbohydrate = cursor.getDouble(cursor.getColumnIndex(RecipeDatabaseHelper.COLUMN_CARBOHYDRATE));

            holder.textViewProductName.setText(productName);
            holder.textViewGrams.setText(String.format(Locale.getDefault(), "%.2f г", grams));
            holder.textViewCalories.setText(String.format(Locale.getDefault(), "%.2f ккал", calories));
            holder.textViewProtein.setText(String.format(Locale.getDefault(), "Б: %.2f г", protein));
            holder.textViewFat.setText(String.format(Locale.getDefault(), "Ж: %.2f г", fat));
            holder.textViewCarbohydrate.setText(String.format(Locale.getDefault(), "У: %.2f г", carbohydrate));

            setSummaryValuesFromDatabase();

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
                        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(RecipeDatabaseHelper.COLUMN_ID));
                        // Вызываем метод для удаления элемента из базы данных
                        deleteItem(id, holder);
                        setSummaryValuesFromDatabase();
                    }
                }
            });
        }
    }

    @SuppressLint("Range")
    private void deleteItem(long id, ViewHolder holder) {
        if (holder != null) {
            // Создаем объект dbHelper
            RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(context);
            // Получаем базу данных в режиме для записи
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Выполняем удаление записи по id
            db.delete(RecipeDatabaseHelper.TABLE_RECIPE,
                    RecipeDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            // Закрываем базу данных
            db.close();
            // Обновляем сумму калорий в таблице calories_summary
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

    private void setSummaryValuesFromDatabase() {
        // Создайте объект RecipeDatabaseHelper
        RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(fragment.requireContext());
        // Получите базу данных в режиме для чтения
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Выполните запрос для выборки суммы всех значений из каждого столбца
        Cursor cursor = db.rawQuery("SELECT " +
                "SUM(" + RecipeDatabaseHelper.COLUMN_CALORIES + ") AS total_calories, " +
                "SUM(" + RecipeDatabaseHelper.COLUMN_GRAMS + ") AS total_grams, " +
                "SUM(" + RecipeDatabaseHelper.COLUMN_PROTEIN + ") AS total_protein, " +
                "SUM(" + RecipeDatabaseHelper.COLUMN_FAT + ") AS total_fat, " +
                "SUM(" + RecipeDatabaseHelper.COLUMN_CARBOHYDRATE + ") AS total_carbohydrate, " +
                "GROUP_CONCAT(" + RecipeDatabaseHelper.COLUMN_PRODUCT_NAME + ", ', ') AS composition " +
                "FROM " + RecipeDatabaseHelper.TABLE_RECIPE, null);

        // Переместите курсор на первую строку (если она есть)
        if (cursor.moveToFirst()) {
            // Получите суммы значений из курсора
            @SuppressLint("Range") double totalCalories = cursor.getDouble(cursor.getColumnIndex("total_calories"));
            @SuppressLint("Range") double totalGrams = cursor.getDouble(cursor.getColumnIndex("total_grams"));
            @SuppressLint("Range") double totalProtein = cursor.getDouble(cursor.getColumnIndex("total_protein"));
            @SuppressLint("Range") double totalFat = cursor.getDouble(cursor.getColumnIndex("total_fat"));
            @SuppressLint("Range") double totalCarbohydrate = cursor.getDouble(cursor.getColumnIndex("total_carbohydrate"));
            @SuppressLint("Range") String composition = cursor.getString(cursor.getColumnIndex("composition"));

            // Установите суммы значений в соответствующие TextView
            TextView textViewCalories = rootView.findViewById(R.id.CaloriesRecipe);
            TextView textViewGrams = rootView.findViewById(R.id.GramsRecipe);
            TextView textViewProtein = rootView.findViewById(R.id.ProteinRecipe);
            TextView textViewFat = rootView.findViewById(R.id.FatRecipe);
            TextView textViewCarbohydrate = rootView.findViewById(R.id.CarbohydrateRecipe);
            TextView textViewComposition = rootView.findViewById(R.id.CompositionRecipe);

            textViewCalories.setText(String.format(Locale.getDefault(), "%.2f", totalCalories));
            textViewGrams.setText(String.format(Locale.getDefault(), "%.2f", totalGrams));
            textViewProtein.setText(String.format(Locale.getDefault(), "%.2f", totalProtein));
            textViewFat.setText(String.format(Locale.getDefault(), "%.2f", totalFat));
            textViewCarbohydrate.setText(String.format(Locale.getDefault(), "%.2f", totalCarbohydrate));
            textViewComposition.setText(composition);
        }

        // Закройте курсор и базу данных
        cursor.close();
        db.close();
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
            textViewProductName = itemView.findViewById(R.id.textViewProductNameRecipe);
            textViewGrams = itemView.findViewById(R.id.textViewGramsRecipe);
            textViewCalories = itemView.findViewById(R.id.textViewCaloriesRecipe);
            textViewProtein = itemView.findViewById(R.id.textViewProteinRecipe);
            textViewFat = itemView.findViewById(R.id.textViewFatRecipe);
            textViewCarbohydrate = itemView.findViewById(R.id.textViewCarbohydrateRecipe);
            deleteButton = itemView.findViewById(R.id.deleteButtonRecipe);
        }
    }
}
