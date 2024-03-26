package com.example.calorieapp.ui.home;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.calorieapp.R;
import com.example.calorieapp.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    // Внутри класса HomeFragment
    private LineChart weightChart;
    private FragmentHomeBinding binding;
    private TextView personNameTextView, calorieNormTextView, genderValueTextView, ageValueTextView, heightValueTextView, weightValueTextView, activityValueTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        personNameTextView = root.findViewById(R.id.person_name);
        calorieNormTextView = root.findViewById(R.id.calorie_norm);
        genderValueTextView = root.findViewById(R.id.genderValue);
        ageValueTextView = root.findViewById(R.id.ageValue);
        heightValueTextView = root.findViewById(R.id.heightValue);
        weightValueTextView = root.findViewById(R.id.weightValue);
        activityValueTextView = root.findViewById(R.id.activityValue);

        loadPersonData();

        ImageView imageView = root.findViewById(R.id.image_draw);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание и отображение фрагмента для редактирования значений персоны
                EditPersonValueFragment editFragment = new EditPersonValueFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, editFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .commit(); // Применяем транзакцию
            }
        });
        //loadWeightChartData(); // Загрузка данных для графика веса
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadWeightChartData(); // Загрузка данных для графика веса
    }

    public void loadPersonData() {
        // Получаем данные из базы данных и устанавливаем их в TextView'ы
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToLast()) {
            // Получаем данные
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
            @SuppressLint("Range") float height = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_HEIGHT));
            @SuppressLint("Range") float weight = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT));
            @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER));
            @SuppressLint("Range") float activityLevel = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACTIVITY_LEVEL));

            // Устанавливаем данные в TextView'ы
            personNameTextView.setText(name);
            calorieNormTextView.setText(String.format(Locale.getDefault(), "%.2f калорий", calculateCalorieNorm(weight, height, age, gender, activityLevel)));
            genderValueTextView.setText(gender);

            // Определяем, какое окончание использовать для слова "год"
            String ageSuffix = "лет";
            if (age % 10 == 1 && age % 100 != 11) {
                ageSuffix = "год";
            } else if ((age % 10 == 2 || age % 10 == 3 || age % 10 == 4) && (age % 100 < 10 || age % 100 >= 20)) {
                ageSuffix = "года";
            }

            // Устанавливаем данные в TextView для возраста с учетом окончания
            ageValueTextView.setText(String.format(Locale.getDefault(), "%d %s", age, ageSuffix));

            heightValueTextView.setText(String.format(Locale.getDefault(), "%.1f см.", height)); // Форматируем до одного знака после запятой
            weightValueTextView.setText(String.format(Locale.getDefault(), "%.1f кг.", weight)); // Форматируем до одного знака после запятой
            // Заменяем точку на запятую в значении переменной activityLevel
            // Заменяем точку на запятую в значении переменной activityLevel
            // Преобразовываем значение activityLevel в строку и заменяем точку на запятую
            String activityLevelString = String.valueOf(activityLevel).replace(".", ",");

            // Устанавливаем данные в TextView для уровня активности с измененной точкой на запятую
            activityValueTextView.setText(activityLevelString);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    private void loadWeightChartData() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Запрос для получения данных о весе, отсортированных по дате в убывающем порядке
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_WEIGHT, DatabaseHelper.COLUMN_DATE},
                null, null, null, null, DatabaseHelper.COLUMN_DATE + " ASC");

        ArrayList<Entry> weightEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") float weight = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));

                // Convert the date string to the desired format (dd.MM )
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
                    Date dateObject = inputFormat.parse(date);
                    date = outputFormat.format(dateObject);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Добавляем вес и дату в соответствующие списки
                weightEntries.add(new Entry(weightEntries.size(), weight));
                dates.add(date);

                LineDataSet weightDataSet = new LineDataSet(weightEntries, "Вес");

                weightDataSet.setColor(Color.BLUE);
                weightDataSet.setCircleColor(Color.BLUE);

            } while (cursor.moveToNext());

        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        LineDataSet weightDataSet = new LineDataSet(weightEntries, "Вес, кг");
        weightDataSet.setColor(Color.BLUE);
        weightDataSet.setCircleColor(Color.BLUE);

        LineData lineData = new LineData(weightDataSet);

        LineChart weightChart = requireView().findViewById(R.id.weightChart);
        weightChart.setData(lineData);
        weightChart.getDescription().setEnabled(false);

        XAxis xAxis = weightChart.getXAxis();
        // Настройка цвета текста оси X
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(14f); // Замените 12f на желаемый размер
        // Настройка цвета текста легенды (если она отображается)
        Legend legend = weightChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(14f); // Замените 12f на желаемый размер

// Настройка цвета текста значений на графике
        weightDataSet.setValueTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // устанавливаем минимальный интервал между метками по оси X
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size()); // устанавливаем количество меток на оси X
        xAxis.setAvoidFirstLastClipping(true); // предотвращаем обрезку первой и последней меток

        // Получение объекта оси Y
        YAxis yAxis = weightChart.getAxisLeft();
        YAxis yAxisR = weightChart.getAxisRight();
        // Скрытие значений по оси Y
        yAxis.setDrawLabels(false);
        yAxisR.setDrawLabels(false);

// Настройка размера текста значений на графике
        weightDataSet.setValueTextSize(12f); // Замените 12f на желаемый размер
        weightChart.invalidate(); // Обновляем график
    }


    private float calculateCalorieNorm(float weight, float height, int age, String gender, float activityLevel) {
        float bmr; // Basal Metabolic Rate (основной обмен веществ)

        // Рассчитываем основной обмен веществ в зависимости от пола
        if (gender.equalsIgnoreCase("Мужской")) {
            bmr = (10 * weight + 6.25f * height - 5 * age + 5) * activityLevel;
        } else { // Для женщин
            bmr = (10 * weight + 6.25f * height - 5 * age - 161) * activityLevel;
        }

        return bmr;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
