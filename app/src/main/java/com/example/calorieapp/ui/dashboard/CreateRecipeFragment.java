package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;

import java.text.DecimalFormat;

public class CreateRecipeFragment extends Fragment {

    private String selectedDate;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтинг макета для этого фрагмента
        View rootView = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        // Установка цвета статус-бара только для BreakfastDetailsFragment



        // Удаляем свечение при прокрутке
        RecyclerView recyclerViewRecipe = rootView.findViewById(R.id.recyclerViewRecipe);
        recyclerViewRecipe.setOverScrollMode(View.OVER_SCROLL_NEVER);

        NestedScrollView nestedScrollView = rootView.findViewById(R.id.nestedscrollview_recipe);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Найти кнопку для закрытия фрагмента
        ImageView buttonCloseRecipeFragment = rootView.findViewById(R.id.buttonCloseRecipeFragment);



        // Установить слушатель нажатия для кнопки
        buttonCloseRecipeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductListFragment productListFragment = new ProductListFragment();
                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, productListFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .addToBackStack(null)
                        .commit(); // Применяем транзакцию
            }
        });

        Button buttonAddProduct = rootView.findViewById(R.id.addProductInRecipe);
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of the ProductListFragment
                ProductListForRecipeFragment productListForRecipeFragment = new ProductListForRecipeFragment();

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Start a FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment (DashboardFragment) with the new one (ProductListFragment)
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, productListForRecipeFragment);

                // Add the transaction to the back stack, so you can return to the previous fragment
                fragmentTransaction.addToBackStack("create_recipe_fragment");

                // Apply the transaction
                fragmentTransaction.commit();
            }
        });

        // Получаем RecyclerView из макета
        recyclerView = rootView.findViewById(R.id.recyclerViewRecipe);

        // Создаем LinearLayoutManager для управления макетом RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // Создайте адаптер и установите его для RecyclerView
        adapter = new RecipeAdapter(requireContext(), this, rootView);
        recyclerView.setAdapter(adapter);

        // Загрузите данные из базы данных и установите их в адаптер
        loadLunchDetailsFromDatabase();

        // Найти кнопку createRecipe по ее id и установить обработчик нажатия
        Button createRecipeButton = rootView.findViewById(R.id.createRecipe);
        createRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получить текст из EditText с id editTextCreateRecipeName
                EditText editTextName = rootView.findViewById(R.id.editTextCreateRecipeName);
                String name = editTextName.getText().toString();

                // Получить данные из TextView и EditText
                TextView textViewCalories = rootView.findViewById(R.id.CaloriesRecipe);
                TextView textViewProtein = rootView.findViewById(R.id.ProteinRecipe);
                TextView textViewFat = rootView.findViewById(R.id.FatRecipe);
                TextView textViewCarbohydrate = rootView.findViewById(R.id.CarbohydrateRecipe);
                TextView textViewComposition = rootView.findViewById(R.id.CompositionRecipe);
                TextView textViewGrams = rootView.findViewById(R.id.GramsRecipe);


                // Пересчитать калории, белки, жиры и углеводы на 100 грамм
                double gramsValue = Double.parseDouble(textViewGrams.getText().toString().replace(",", "."));
                double caloriesValue = Double.parseDouble(textViewCalories.getText().toString().replace(",", ".")) / gramsValue * 100;
                double proteinValue = Double.parseDouble(textViewProtein.getText().toString().replace(",", ".")) / gramsValue * 100;
                double fatValue = Double.parseDouble(textViewFat.getText().toString().replace(",", ".")) / gramsValue * 100;
                double carbohydrateValue = Double.parseDouble(textViewCarbohydrate.getText().toString().replace(",", ".")) / gramsValue * 100;

                // Округляем значения до двух знаков после запятой
                DecimalFormat df = new DecimalFormat("#.##");
                caloriesValue = Double.parseDouble(df.format(caloriesValue).replace(",", "."));
                proteinValue = Double.parseDouble(df.format(proteinValue).replace(",", "."));
                fatValue = Double.parseDouble(df.format(fatValue).replace(",", "."));
                carbohydrateValue = Double.parseDouble(df.format(carbohydrateValue).replace(",", "."));

                // Проверить, является ли таблица TABLE_RECIPE пустой
                RecipeDatabaseHelper recipeDbHelper = new RecipeDatabaseHelper(requireContext());
                SQLiteDatabase recipeDb = recipeDbHelper.getReadableDatabase();
                Cursor cursor = recipeDb.rawQuery("SELECT COUNT(*) FROM " + RecipeDatabaseHelper.TABLE_RECIPE, null);
                cursor.moveToFirst();
                int count = cursor.getInt(0);
                cursor.close();
                recipeDb.close();

                // Если таблица не пустая и заполнен EditText с id editTextCreateRecipeName
                if (count > 0 && !name.isEmpty()) {
                    // Записать данные в базу данных ProductDatabaseHelper
                    ProductDatabaseHelper productDbHelper = new ProductDatabaseHelper(requireContext());
                    SQLiteDatabase productDb = productDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(ProductDatabaseHelper.COLUMN_NAME, name);
                    values.put(ProductDatabaseHelper.COLUMN_CALORIES, caloriesValue);
                    values.put(ProductDatabaseHelper.COLUMN_PROTEIN, proteinValue);
                    values.put(ProductDatabaseHelper.COLUMN_FAT, fatValue);
                    values.put(ProductDatabaseHelper.COLUMN_CARBOHYDRATE, carbohydrateValue);
                    values.put(ProductDatabaseHelper.COLUMN_COMPOSITION, textViewComposition.getText().toString());
                    values.put(ProductDatabaseHelper.COLUMN_CATEGORY, "Ваши рецепты");

                    // Вставить данные в таблицу TABLE_PRODUCTS
                    long newRowId = productDb.insert(ProductDatabaseHelper.TABLE_PRODUCTS, null, values);
                    if (newRowId != -1) {
                        // Данные успешно вставлены
                        Toast.makeText(requireContext(), "Рецепт успешно сохранен", Toast.LENGTH_SHORT).show();
                    } else {
                        // Произошла ошибка при вставке данных
                        Toast.makeText(requireContext(), "Ошибка сохранения рецепта", Toast.LENGTH_SHORT).show();
                    }

                    // Закрыть базу данных
                    productDb.close();

                    // Перейти во фрагмент ProductListFragment
                    ProductListFragment productListFragment = new ProductListFragment();
                    // Получаем FragmentManager и начинаем транзакцию
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment_activity_main, productListFragment) // Заменяем текущий фрагмент на ProductListFragment
                            .addToBackStack(null)
                            .commit(); // Применяем транзакцию

                    // Очистить таблицу TABLE_RECIPE
                    SQLiteDatabase clearDb = recipeDbHelper.getWritableDatabase();
                    clearDb.delete(RecipeDatabaseHelper.TABLE_RECIPE, null, null);
                    clearDb.close();
                } else {
                    // Вывести сообщение об ошибке
                    Toast.makeText(requireContext(), "Необходимо заполнить название рецепта и добавить ингредиенты", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }

    private void loadLunchDetailsFromDatabase() {
        // Создайте объект LunchDatabaseHelper
        RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(requireContext());

        // Получите базу данных в режиме для чтения
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Выполните запрос для выборки всех записей
        Cursor cursor = db.query(
                RecipeDatabaseHelper.TABLE_RECIPE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Передайте курсору адаптеру для отображения данных
        adapter.swapCursor(cursor);
    }




}
