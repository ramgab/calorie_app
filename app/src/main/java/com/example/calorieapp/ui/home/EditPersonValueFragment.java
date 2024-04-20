package com.example.calorieapp.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

    private EditText editTextName, editTextAge, editTextHeight, editTextWeight;
    private Spinner spinnerGender, spinnerActivityLevel;
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

        // Установка адаптеров для спиннеров
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gender_values, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_level_values, R.layout.spinner_item);
        activityLevelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityLevelAdapter);


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
                String gender = spinnerGender.getSelectedItem().toString();
                String activityLevel = spinnerActivityLevel.getSelectedItem().toString();

                // Проверка наличия всех данных
                if (name.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
                    // Если какое-то поле не заполнено, выведите сообщение об ошибке
                    Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }

                // Проверка возраста, роста и веса на числовой формат
                int age;
                double weight, height;
                try {
                    age = Integer.parseInt(ageStr);
                    height = Double.parseDouble(heightStr.replace(',', '.')); // Заменяем запятую на точку, если она есть
                    weight = Double.parseDouble(weightStr.replace(',', '.')); // Заменяем запятую на точку, если она есть
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


                // Создаем экземпляр класса DatabaseHelper для работы с базой данных
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

                // Вставляем новую запись
                dbHelper.insertData(name, age, height, weight, gender, activityLevel);

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

}
