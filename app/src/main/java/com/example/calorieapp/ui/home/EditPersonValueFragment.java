package com.example.calorieapp.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
                int age, height;
                float weight;
                try {
                    age = Integer.parseInt(ageStr);
                    height = Integer.parseInt(heightStr);
                    weight = Float.parseFloat(weightStr);
                } catch (NumberFormatException e) {
                    // Если возраст, рост или вес не являются числами, выведите сообщение об ошибке
                    Toast.makeText(requireContext(), "Пожалуйста, введите числовые значения для возраста, роста и веса", Toast.LENGTH_SHORT).show();
                    return; // Прерываем выполнение метода, чтобы данные не сохранялись
                }


                // Создаем экземпляр класса DatabaseHelper для работы с базой данных
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

                // Вставляем новую запись
                dbHelper.insertData(name, age, height, weight, gender, activityLevel);

                // Возвращаемся в HomeFragment
                HomeFragment homeFragment = new HomeFragment();

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

                // Скройте BottomNavigationView
                bottomNavigationView.setVisibility(View.VISIBLE);



                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, homeFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .commit(); // Применяем транзакцию
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
}
