package com.example.calorieapp.ui.dashboard;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.calorieapp.R;

import java.text.DecimalFormat;

public class CreateProductFragment extends Fragment {

    private EditText editTextName, editTextProtein, editTextFat, editTextCarbohydrate;
    private TextView textViewCalories;

    public CreateProductFragment() {
        // Пустой обязательный публичный конструктор
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Надуваем макет фрагмента для отображения пользовательского интерфейса
        return inflater.inflate(R.layout.fragment_create_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим все необходимые элементы управления
        editTextName = view.findViewById(R.id.editTextCreateProductName);
        editTextProtein = view.findViewById(R.id.editTextCreateProtein);
        editTextFat = view.findViewById(R.id.editTextCreateFat);
        editTextCarbohydrate = view.findViewById(R.id.editTextCreateCarbohydrate);
        textViewCalories = view.findViewById(R.id.textViewCreateCalories);
        Button saveButton = view.findViewById(R.id.saveCreateProduct);

        // Устанавливаем слушатель изменения текста для автоматического рассчета калорий
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateCalories();
            }
        };

        editTextProtein.addTextChangedListener(textWatcher);
        editTextFat.addTextChangedListener(textWatcher);
        editTextCarbohydrate.addTextChangedListener(textWatcher);

        // Устанавливаем обработчик нажатия на кнопку сохранения
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    private void calculateCalories() {
        // Получаем значения из EditText и рассчитываем калории
        String proteinStr = editTextProtein.getText().toString();
        String fatStr = editTextFat.getText().toString();
        String carbohydrateStr = editTextCarbohydrate.getText().toString();

        // Проверяем, что первый символ не является точкой
        if (startsWithPoint(proteinStr) || startsWithPoint(fatStr) || startsWithPoint(carbohydrateStr) || startsWithComma(proteinStr) || startsWithComma(fatStr) || startsWithComma(carbohydrateStr)) {
            Toast.makeText(requireContext(), "Неверный ввод: первый символ не может быть точкой/запятой", Toast.LENGTH_SHORT).show();
            return;
        }



        // Проверяем, что введенные значения содержат только одну точку
        if (hasMultiplePoints(proteinStr) || hasMultiplePoints(fatStr) || hasMultiplePoints(carbohydrateStr) || hasMultipleCommas(proteinStr) || hasMultipleCommas(fatStr) || hasMultipleCommas(carbohydrateStr)) {
            Toast.makeText(requireContext(), "Неверный ввод: только одна точка допускается в каждом поле", Toast.LENGTH_SHORT).show();
            return;
        }

        proteinStr = proteinStr.replace(",", ".");
        fatStr = fatStr.replace(",", ".");
        carbohydrateStr = carbohydrateStr.replace(",", ".");
        double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
        double fat = fatStr.isEmpty() ? 0 : Double.parseDouble(fatStr);
        double carbohydrate = carbohydrateStr.isEmpty() ? 0 : Double.parseDouble(carbohydrateStr);

        double calories = (protein * 4.1) + (fat * 9.3) + (carbohydrate * 4.1);

        // Форматируем значение калорий с двумя знаками после запятой
        DecimalFormat df = new DecimalFormat("#.00");
        String formattedCalories = df.format(calories);

        // Заменяем запятую на точку в строке калорий
        formattedCalories = formattedCalories.replace(",", ".");

        // Устанавливаем рассчитанные калории в TextView
        textViewCalories.setText(formattedCalories);
    }

    private boolean startsWithPoint(String str) {
        // Проверяем, является ли первый символ точкой
        return !str.isEmpty() && str.charAt(0) == '.';
    }

    private boolean hasMultiplePoints(String str) {
        // Проверяем, содержит ли строка более одной точки
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') {
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean startsWithComma(String str) {
        // Проверяем, является ли первый символ запятой
        return !str.isEmpty() && str.charAt(0) == ',';
    }

    private boolean hasMultipleCommas(String str) {
        // Проверяем, содержит ли строка более одной запятой
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ',') {
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInputValid() {
        // Проверяем, что все поля заполнены
        String name = editTextName.getText().toString().trim();
        String proteinStr = editTextProtein.getText().toString().trim().replace(",", ".");
        String fatStr = editTextFat.getText().toString().trim().replace(",", ".");
        String carbohydrateStr = editTextCarbohydrate.getText().toString().trim().replace(",", ".");

        if (name.isEmpty() || proteinStr.isEmpty() || fatStr.isEmpty() || carbohydrateStr.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверяем первый символ и количество точек и запятых в вводе белка, жира и углеводов
        if (startsWithPoint(proteinStr) || startsWithPoint(fatStr) || startsWithPoint(carbohydrateStr) ||
                startsWithComma(proteinStr) || startsWithComma(fatStr) || startsWithComma(carbohydrateStr) ||
                hasMultiplePoints(proteinStr) || hasMultiplePoints(fatStr) || hasMultiplePoints(carbohydrateStr) ||
                hasMultipleCommas(proteinStr) || hasMultipleCommas(fatStr) || hasMultipleCommas(carbohydrateStr)) {
            Toast.makeText(requireContext(), "Неверный ввод: проверьте значения белка, жира и углеводов", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверяем, что значение калорий больше нуля
        double calories = Double.parseDouble(textViewCalories.getText().toString());
        if (calories <= 0) {
            Toast.makeText(requireContext(), "Неверный ввод: значение калорий должно быть больше нуля", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveProduct() {
        if (!isInputValid()) {
            return;
        }

        // Получаем значения из EditText
        String name = editTextName.getText().toString().trim();
        double calories = Double.parseDouble(textViewCalories.getText().toString());
        double protein = Double.parseDouble(editTextProtein.getText().toString().trim().replace(",", "."));
        double fat = Double.parseDouble(editTextFat.getText().toString().trim().replace(",", "."));
        double carbohydrate = Double.parseDouble(editTextCarbohydrate.getText().toString().trim().replace(",", "."));

        // Создаем объект Product
        Product product = new Product(name, calories, protein, fat, carbohydrate, null, null, null);

        // Добавляем продукт в базу данных
        addProductToDatabase(product);

        // Создаем новый экземпляр фрагмента ProductListFragment
        ProductListFragment productListFragment = new ProductListFragment();

        // Получаем FragmentManager и начинаем транзакцию
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, productListFragment) // Заменяем текущий фрагмент на ProductListFragment
                .commit(); // Применяем транзакцию
    }

    private void addProductToDatabase(Product product) {
        // Открываем базу данных для записи
        SQLiteDatabase db = new ProductDatabaseHelper(requireContext()).getWritableDatabase();

        // Значения для вставки
        ContentValues values = new ContentValues();
        values.put(ProductDatabaseHelper.COLUMN_NAME, product.getName());
        values.put(ProductDatabaseHelper.COLUMN_CALORIES, product.getCalories());
        values.put(ProductDatabaseHelper.COLUMN_PROTEIN, product.getProteins());
        values.put(ProductDatabaseHelper.COLUMN_FAT, product.getFats());
        values.put(ProductDatabaseHelper.COLUMN_CARBOHYDRATE, product.getCarbohydrates());

        // Вставляем данные в таблицу продуктов
        db.insert(ProductDatabaseHelper.TABLE_PRODUCTS, null, values);

        // Закрываем базу данных
        db.close();
    }
}
