package com.example.calorieapp.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.calorieapp.R;
import com.example.calorieapp.ui.dashboard.DashboardFragment;
import com.example.calorieapp.ui.dashboard.ProductListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditPersonValueFragment extends Fragment {

    private EditText editTextName, editTextAge, editTextHeight, editTextWeight, editTextFatPercent;
    private EditText editTextCal, editTextProtein, editTextFat, editTextCarbohydrate;
    private TextView CMTValue, BMRValue, TEFValue, CalDefMinValue, CalDefMaxValue, CalProfMinValue, CalProfMaxValue, ProteinDefValue, FatDefValue, CarbohydrateDefMinValue, CarbohydrateDefMaxValue, CarbohydrateProfMinValue, CarbohydrateProfMaxValue, ProteinNormValue, FatNormValue, CarbohydrateNormValue, ProteinProfValue, FatProfValue, CarbohydrateProfValue,  CalNormValue, PFCNormValue, CalProfValue, PFCProfValue, WaterValue, FiberValue, SaltValue, CoffeeNormValue, CoffeeMaxValue;
    private Spinner spinnerGender, spinnerActivityLevel;
    private Spinner spinnerGoals;
    private Button buttonSave;
    private BottomNavigationView bottomNavigationView; // Добавляем BottomNavigationView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_person_value, container, false);

        // Установка цвета статус-бара
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Для API 30 и выше
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
        } else {
            // Для API ниже 30
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
            // Убедитесь, что ваш стиль активности не устанавливает прозрачный статус-бар (android:windowTranslucentStatus)


        }

        // Удаляем свечение при прокрутке
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) NestedScrollView nestedScrollView = view.findViewById(R.id.nestedscrollview_edit_person);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        // Скрываем BottomNavigationView
        bottomNavigationView.setVisibility(View.GONE);


        // Инициализация views
        editTextName = view.findViewById(R.id.editTextName);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        buttonSave = view.findViewById(R.id.buttonSave);
        editTextFatPercent = view.findViewById(R.id.editTextFatPercent);
        spinnerGoals = view.findViewById(R.id.spinnerGoals);

        CMTValue = view.findViewById(R.id.CMTValue);
        BMRValue = view.findViewById(R.id.BMRValue);
        TEFValue = view.findViewById(R.id.TEFValue);
        CalDefMinValue = view.findViewById(R.id.CalDefMinValue);
        CalDefMaxValue = view.findViewById(R.id.CalDefMaxValue);

        ProteinDefValue = view.findViewById(R.id.ProteinDefValue);
        FatDefValue = view.findViewById(R.id.FatDefValue);
        CarbohydrateDefMinValue= view.findViewById(R.id.CarbohydrateDefMinValue);
        CarbohydrateDefMaxValue= view.findViewById(R.id.CarbohydrateDefMaxValue);

        ProteinNormValue = view.findViewById(R.id.ProteinNormValue);
        FatNormValue = view.findViewById(R.id.FatNormValue);
        CarbohydrateNormValue= view.findViewById(R.id.CarbohydrateNormValue);

        ProteinProfValue = view.findViewById(R.id.ProteinProfValue);
        FatProfValue = view.findViewById(R.id.FatProfValue);
        CarbohydrateProfMinValue= view.findViewById(R.id.CarbohydrateProfMinValue);
        CarbohydrateProfMaxValue= view.findViewById(R.id.CarbohydrateProfMaxValue);




        CalNormValue = view.findViewById(R.id.CalNormValue);
        CalProfMinValue = view.findViewById(R.id.CalProfMinValue);
        CalProfMaxValue = view.findViewById(R.id.CalProfMaxValue);
        WaterValue = view.findViewById(R.id.WaterValue);
        FiberValue = view.findViewById(R.id.FiberValue);
        SaltValue = view.findViewById(R.id.SaltValue);
        CoffeeNormValue = view.findViewById(R.id.CoffeeNormValue);
        CoffeeMaxValue = view.findViewById(R.id.CoffeeMaxValue);






        // Установка адаптеров для спиннеров
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gender_values, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_level_values, R.layout.spinner_item);
        activityLevelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityLevelAdapter);

        ArrayAdapter<CharSequence> goalsAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.goals_values, R.layout.spinner_item);
        goalsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGoals.setAdapter(goalsAdapter);


        editTextFatPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int beжfore, int count) {
                // Ничего не делаем при изменении текста
            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = s.toString();
                if (text.startsWith("0") || text.startsWith(".") || text.startsWith(",")) {
                    // Если текст начинается с 0, точки или запятой, удаляем первый символ
                    s.delete(0, 1);
                } else if (text.contains(".") && text.contains(",")) {
                    // Если текст содержит и точку, и запятую, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(".") && hasMultiplePoints(text)) {
                    // Если текст содержит более одной точки, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(",") && hasMultipleCommas(text)) {
                    // Если текст содержит более одной запятой, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                }

                updateDisplayedValues();

            }
        });


        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Вызываем метод updateDisplayedValues после выбора значения в спиннере
                updateDisplayedValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем, если не выбрано ничего
            }
        });

        spinnerActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Вызываем метод updateDisplayedValues после выбора значения в спиннере
                updateDisplayedValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем, если не выбрано ничего
            }
        });


        editTextAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int beжfore, int count) {
                // Ничего не делаем при изменении текста
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.startsWith("0") || text.startsWith(".") || text.startsWith(",")) {
                    // Если текст начинается с 0, точки или запятой, удаляем первый символ
                    s.delete(0, 1);
                } else if (text.contains(".") && text.contains(",")) {
                    // Если текст содержит и точку, и запятую, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(".") && hasMultiplePoints(text)) {
                    // Если текст содержит более одной точки, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(",") && hasMultipleCommas(text)) {
                    // Если текст содержит более одной запятой, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                }

                updateDisplayedValues();
            }
        });

        editTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int beжfore, int count) {
                // Ничего не делаем при изменении текста
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.startsWith("0") || text.startsWith(".") || text.startsWith(",")) {
                    // Если текст начинается с 0, точки или запятой, удаляем первый символ
                    s.delete(0, 1);
                } else if (text.contains(".") && text.contains(",")) {
                    // Если текст содержит и точку, и запятую, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(".") && hasMultiplePoints(text)) {
                    // Если текст содержит более одной точки, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(",") && hasMultipleCommas(text)) {
                    // Если текст содержит более одной запятой, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                }
                updateDisplayedValues();
            }
        });

        editTextHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int beжfore, int count) {
                // Ничего не делаем при изменении текста
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.startsWith("0") || text.startsWith(".") || text.startsWith(",")) {
                    // Если текст начинается с 0, точки или запятой, удаляем первый символ
                    s.delete(0, 1);
                } else if (text.contains(".") && text.contains(",")) {
                    // Если текст содержит и точку, и запятую, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(".") && hasMultiplePoints(text)) {
                    // Если текст содержит более одной точки, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                } else if (text.contains(",") && hasMultipleCommas(text)) {
                    // Если текст содержит более одной запятой, удаляем последний введенный символ
                    s.delete(s.length() - 1, s.length());
                }

                updateDisplayedValues();
            }
        });


        // Установка обработчика нажатия на кнопку Сохранить
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем введенные пользователем данные
                String name = editTextName.getText().toString();
                String ageStr = editTextAge.getText().toString();
                String heightStr = editTextHeight.getText().toString();
                String weightStr = editTextWeight.getText().toString();
                String fatPercentStr = editTextFatPercent.getText().toString();
                String gender = spinnerGender.getSelectedItem().toString();
                String activityLevel = spinnerActivityLevel.getSelectedItem().toString();

                String goals = spinnerGoals.getSelectedItem().toString();



                // Получение новых значений для дополнительных полей
                String calMinDeficitStr = CalDefMinValue.getText().toString().trim().replace(',', '.');
                String calMaxDeficitStr = CalDefMaxValue.getText().toString().trim().replace(',', '.');
                String calNormStr = CalNormValue.getText().toString().trim().replace(',', '.');
                String calMinSurplusStr = CalProfMinValue.getText().toString().trim().replace(',', '.');
                String calMaxSurplusStr = CalProfMaxValue.getText().toString().trim().replace(',', '.');

                String proteinDeficitStr = ProteinDefValue.getText().toString().trim().replace(',', '.');
                String proteinNormStr = ProteinNormValue.getText().toString().trim().replace(',', '.');
                String proteinSurplusStr = ProteinProfValue.getText().toString().trim().replace(',', '.');

                String fatDeficitStr = FatDefValue.getText().toString().trim().replace(',', '.');
                String fatNormStr = FatNormValue.getText().toString().trim().replace(',', '.');
                String fatSurplusStr = FatProfValue.getText().toString().trim().replace(',', '.');

                String carbMinDeficitStr = CarbohydrateDefMinValue.getText().toString().trim().replace(',', '.');
                String carbMaxDeficitStr = CarbohydrateDefMaxValue.getText().toString().trim().replace(',', '.');
                String carbNormStr = CarbohydrateNormValue.getText().toString().trim().replace(',', '.');
                String carbMinSurplusStr = CarbohydrateProfMinValue.getText().toString().trim().replace(',', '.');
                String carbMaxSurplusStr = CarbohydrateProfMaxValue.getText().toString().trim().replace(',', '.');

                String waterStr = WaterValue.getText().toString().trim().replace(',', '.');
                String fiberStr = FiberValue.getText().toString().trim().replace(',', '.');
                String saltStr = SaltValue.getText().toString().trim().replace(',', '.');

                String caffeineNormStr = CoffeeNormValue.getText().toString().trim().replace(',', '.');
                String caffeineMaxStr = CoffeeMaxValue.getText().toString().trim().replace(',', '.');

                // Преобразование строк в тип double
                double calMinDeficit = Double.parseDouble(calMinDeficitStr);
                double calMaxDeficit = Double.parseDouble(calMaxDeficitStr);
                double calNorm = Double.parseDouble(calNormStr);
                double calMinSurplus = Double.parseDouble(calMinSurplusStr);
                double calMaxSurplus = Double.parseDouble(calMaxSurplusStr);

                double proteinDeficit = Double.parseDouble(proteinDeficitStr);
                double proteinNorm = Double.parseDouble(proteinNormStr);
                double proteinSurplus = Double.parseDouble(proteinSurplusStr);

                double fatDeficit = Double.parseDouble(fatDeficitStr);
                double fatNorm = Double.parseDouble(fatNormStr);
                double fatSurplus = Double.parseDouble(fatSurplusStr);

                double carbMinDeficit = Double.parseDouble(carbMinDeficitStr);
                double carbMaxDeficit = Double.parseDouble(carbMaxDeficitStr);
                double carbNorm = Double.parseDouble(carbNormStr);
                double carbMinSurplus = Double.parseDouble(carbMinSurplusStr);
                double carbMaxSurplus = Double.parseDouble(carbMaxSurplusStr);

                double water = Double.parseDouble(waterStr);
                double fiber = Double.parseDouble(fiberStr);
                double salt = Double.parseDouble(saltStr);

                double caffeineNorm = Double.parseDouble(caffeineNormStr);
                double caffeineMax = Double.parseDouble(caffeineMaxStr);



                // Проверка наличия всех данных
                if (name.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() || fatPercentStr.isEmpty()) {
                    // Если какое-то поле не заполнено, выведите сообщение об ошибке
                    Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }

                // Проверка возраста, роста и веса на числовой формат
                int age;
                double weight, height, fatPercent;
                try {
                    age = Integer.parseInt(ageStr);
                    height = Double.parseDouble(heightStr.replace(',', '.')); // Заменяем запятую на точку, если она есть
                    weight = Double.parseDouble(weightStr.replace(',', '.')); // Заменяем запятую на точку, если она есть
                    fatPercent = Double.parseDouble(fatPercentStr.replace(',', '.')); // Заменяем запятую на точку, если она есть

                } catch (NumberFormatException e) {
                    // Если возраст, рост или вес не являются числами, выведите сообщение об ошибке
                    Toast.makeText(requireContext(), "Пожалуйста, введите числовые значения для возраста, роста и веса", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }

                // Проверка значения роста
                if (height > 250 || height < 100) {
                    // Если рост выходит за допустимый диапазон, отобразить сообщение об ошибке
                    Toast.makeText(requireContext(), "Неверный ввод: рост должен быть в диапазоне от 100 до 220 см", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }

                // Проверка значения веса
                if (weight > 250 || weight < 30) {
                    // Если вес выходит за допустимый диапазон, отобразить сообщение об ошибке
                    Toast.makeText(requireContext(), "Неверный ввод: вес должен быть в диапазоне от 30 до 200 кг", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }

                // Проверка значения возраста
                if (age > 100 || age < 10) {
                    // Если возраст выходит за допустимый диапазон, отобразить сообщение об ошибке
                    Toast.makeText(requireContext(), "Неверный ввод: возраст должен быть в диапазоне от 10 до 100 лет", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }

                // Проверка значения возраста
                if (fatPercent > 60 || age < 1) {
                    Toast.makeText(requireContext(), "Неверный ввод: процент жира не может быть таким", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }




                // Создаем экземпляр класса DatabaseHelper для работы с базой данных
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

                // Вставляем новую запись
                //dbHelper.insertData(name, age, height, weight, gender, activityLevel, goals);



                dbHelper.insertData(name, age, height, weight, gender, activityLevel, fatPercent, goals,
                        calMinDeficit, calMaxDeficit, calNorm, calMinSurplus, calMaxSurplus, proteinDeficit,
                        proteinNorm, proteinSurplus, fatDeficit, fatNorm, fatSurplus, carbMinDeficit,
                        carbMaxDeficit, carbNorm, carbMinSurplus, carbMaxSurplus, water, fiber, salt,
                        caffeineNorm, caffeineMax

                );


                // Возвращаемся в HomeFragment
                HomeFragment homeFragment = new HomeFragment();

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);





                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, homeFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .commit(); // Применяем транзакцию

                // Скройте BottomNavigationView
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView closeButton = view.findViewById(R.id.buttonCloseEditPersonFragment);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                HomeFragment homeFragment = new HomeFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, homeFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .commit(); // Применяем транзакцию

                // Отображаем BottomNavigationView
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        return view;
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


    private void updateDisplayedValues() {
        // Получаем значения из EditText
        String weightStr = editTextWeight.getText().toString();
        String fatPercentStr = editTextFatPercent.getText().toString();
        String ageStr = editTextAge.getText().toString();
        String heightStr = editTextHeight.getText().toString();

        // Проверяем, что все необходимые данные заполнены
        if (!weightStr.isEmpty() && !fatPercentStr.isEmpty() && !ageStr.isEmpty() && !heightStr.isEmpty()) {
            // Преобразуем строки в числа
            double weight = Double.parseDouble(weightStr);
            double fatPercent = Double.parseDouble(fatPercentStr);
            int age = Integer.parseInt(ageStr);
            double height = Double.parseDouble(heightStr);

            // Вычисляем сухую массу тела (СМТ)
            double leanBodyMass = weight - (weight * (fatPercent / 100));
            CMTValue.setText(String.format("%.2f", leanBodyMass) + "кг.");

            // Определяем пол и вычисляем BMR
            String gender = spinnerGender.getSelectedItem().toString();
            double BMR;
            if (gender.equals("Мужской")) {
                BMR = 88.362 + (13.397 * leanBodyMass) + (4.799 * height) - (5.677 * age);
            } else {
                BMR = 447.593 + (9.247 * leanBodyMass) + (3.098 * height) - (4.330 * age);
            }
            BMRValue.setText(String.format("%.2f", BMR) + "ккал");

            // Вычисляем ТЭП (термический эффект пищи)
            double TEF = 0.1 * BMR;
            TEFValue.setText(String.format("%.2f", TEF) + "ккал");

            // Вычисляем AMR (коэффициент активности) в зависимости от выбранного уровня активности
            double AMR = 1.0; // По умолчанию для сидячего образа жизни
            String activityLevel = spinnerActivityLevel.getSelectedItem().toString();
            switch (activityLevel) {
                case "1.2":
                    AMR = 1.2;
                    break;
                case "1.375":
                    AMR = 1.375;
                    break;
                case "1.55":
                    AMR = 1.55;
                    break;
                case "1.725":
                    AMR = 1.725;
                    break;
                case "1.9 ":
                    AMR = 1.9;
                    break;
            }

            // Вычисляем калорийность на дефицит, поддержку и профицит
            double calMinDeficit = (BMR * AMR + TEF) * 0.8;
            double calMaxDeficit = (BMR * AMR + TEF) * 0.9;

            double calNorm = BMR * AMR + TEF;
            double calMinSurplus = (BMR * AMR + TEF) * 1.1;
            double calMaxSurplus = (BMR * AMR + TEF) * 1.2;

            // Отображаем результаты
            CalDefMinValue.setText(String.format("%.2f", calMinDeficit));
            CalDefMaxValue.setText(String.format("%.2f", calMaxDeficit));
            CalNormValue.setText(String.format("%.2f", calNorm));
            CalProfMinValue.setText(String.format("%.2f", calMinSurplus));
            CalProfMaxValue.setText(String.format("%.2f", calMaxSurplus));


            // Вычисляем БЖУ на дефицит, поддержку и профицит
            double proteinDeficit = leanBodyMass * 2.5;
            double proteinNorm = leanBodyMass * 1.5;
            double proteinSurplus = leanBodyMass * 1.5;

            double fatDeficit = leanBodyMass * 1;
            double fatNorm = leanBodyMass * 1;
            double fatSurplus = leanBodyMass * 1;

            double carbMinDeficit = (calMinDeficit - (proteinDeficit * 4 + fatDeficit * 9)) / 4;
            double carbMaxDeficit = (calMaxDeficit - (proteinDeficit * 4 + fatDeficit * 9)) / 4;
            double carbNorm = (calNorm - (proteinNorm * 4 + fatNorm * 9)) / 4;
            double carbMinSurplus = (calMinSurplus - (proteinSurplus * 4 + fatSurplus * 9)) / 4;
            double carbMaxSurplus = (calMaxSurplus - (proteinSurplus * 4 + fatSurplus * 9)) / 4;

            // Отображаем результаты

            ProteinDefValue.setText(String.format("%.2f", proteinDeficit));
            FatDefValue.setText(String.format("%.2f", fatDeficit));
            CarbohydrateDefMinValue.setText(String.format("%.2f", carbMinDeficit));
            CarbohydrateDefMaxValue.setText(String.format("%.2f", carbMaxDeficit));

            ProteinNormValue.setText(String.format("%.2f", proteinNorm));
            FatNormValue.setText(String.format("%.2f", fatNorm));
            CarbohydrateNormValue.setText(String.format("%.2f", carbNorm));

            ProteinProfValue.setText(String.format("%.2f", proteinSurplus));
            FatProfValue.setText(String.format("%.2f", fatSurplus));
            CarbohydrateProfMinValue.setText(String.format("%.2f", carbMinSurplus));
            CarbohydrateProfMaxValue.setText(String.format("%.2f", carbMaxSurplus));

            // Вычисляем необходимое количество воды, клетчатки и соли
            double water = leanBodyMass / 20; // 1л на 20кг СМТ
            double fiber = calNorm / 1000 * 10; // 10г на 1000 ккал
            double salt = leanBodyMass / 10; // 1г на 10кг СМТ

            // Отображаем результаты
            WaterValue.setText(String.format("%.2f", water));
            FiberValue.setText(String.format("%.2f", fiber));
            SaltValue.setText(String.format("%.2f", salt));

            // Вычисляем количество кофеина
            double caffeineNorm = weight * 2.5; // 2.5мг на 1кг ОМТ
            double caffeineMax = weight * 5; // 5мг на 1кг ОМТ

            // Отображаем результаты
            CoffeeNormValue.setText(String.format("%.2f", caffeineNorm));
            CoffeeMaxValue.setText(String.format("%.2f", caffeineMax));
        }
    }

}
