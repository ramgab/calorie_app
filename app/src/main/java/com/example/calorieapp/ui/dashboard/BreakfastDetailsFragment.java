package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
import com.example.calorieapp.ui.dashboard.BreakfastDetailsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class BreakfastDetailsFragment extends Fragment {

    private String selectedDate;
    private RecyclerView recyclerView;
    private BreakfastDetailsAdapter adapter;
    private TextView calBreakfast; // Добавляем TextView
    private BottomNavigationView bottomNavigationView; // Добавляем BottomNavigationView
    public BreakfastDetailsFragment() {
        // Обязательный пустой конструктор
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтинг макета для этого фрагмента
        View rootView = inflater.inflate(R.layout.fragment_breakfast_details, container, false);

        // Установка цвета статус-бара только для BreakfastDetailsFragment
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Для API 30 и выше
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.blocks));
        } else {
            // Для API ниже 30
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.blocks));
            // Убедитесь, что ваш стиль активности не устанавливает прозрачный статус-бар (android:windowTranslucentStatus)
        }

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        // Скрываем BottomNavigationView
        bottomNavigationView.setVisibility(View.GONE);

        // Удаляем свечение при прокрутке
        RecyclerView recyclerViewBreakfast = rootView.findViewById(R.id.recyclerViewBreakfastDetails);
        recyclerViewBreakfast.setOverScrollMode(View.OVER_SCROLL_NEVER);

        NestedScrollView nestedScrollView = rootView.findViewById(R.id.nestedscrollview_breakfast_details);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Найти кнопку для закрытия фрагмента
        ImageView buttonCloseFragment = rootView.findViewById(R.id.buttonCloseBreakfastFragment);



        // Установить слушатель нажатия для кнопки
        buttonCloseFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardFragment dashboardFragment = new DashboardFragment();


                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, dashboardFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .addToBackStack(null)
                        .commit(); // Применяем транзакцию


            }
        });

        // Получите RecyclerView из макета
        recyclerView = rootView.findViewById(R.id.recyclerViewBreakfastDetails);

        // Создайте LinearLayoutManager для управления макетом RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // Создайте адаптер и установите его для RecyclerView
        adapter = new BreakfastDetailsAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Получите выбранную дату из аргументов
        selectedDate = getArguments().getString("selectedDate");

        // Загрузите данные из базы данных и установите их в адаптер
        loadBreakfastDetailsFromDatabase();

        // Найдем TextView по его id
        calBreakfast = rootView.findViewById(R.id.calBreakfast);

        // Загружаем суммарное количество калорий и устанавливаем его в TextView
        loadCaloriesSummaryFromDatabase(selectedDate);

        return rootView;
    }

    private void loadCaloriesSummaryFromDatabase(String selectedDate) {
        BreakfastDatabaseHelper dbHelper = new BreakfastDatabaseHelper(requireContext());
        double totalCalories = dbHelper.getTotalCaloriesSummary(selectedDate);
        // Устанавливаем значение в TextView
        calBreakfast.setText(String.format("%s ккал", totalCalories));

    }

    private void loadBreakfastDetailsFromDatabase() {
        // Создайте объект BreakfastDatabaseHelper
        BreakfastDatabaseHelper dbHelper = new BreakfastDatabaseHelper(requireContext());

        // Получите базу данных в режиме для чтения
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Выполните запрос для выборки всех записей, где дата равна выбранной дате
        Cursor cursor = db.query(
                BreakfastDatabaseHelper.TABLE_BREAKFAST,
                null,
                BreakfastDatabaseHelper.COLUMN_DATE + " = ?",
                new String[]{selectedDate},
                null,
                null,
                null
        );

        // Передайте курсору адаптеру для отображения данных
        adapter.swapCursor(cursor);

    }
}
