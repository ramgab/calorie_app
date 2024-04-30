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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
import com.example.calorieapp.ui.dashboard.LunchDetailsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LunchDetailsFragment extends Fragment {

    private String selectedDate;
    private RecyclerView recyclerView;
    private LunchDetailsAdapter adapter;

    private TextView calLunch; // Добавляем TextView

    private BottomNavigationView bottomNavigationView; // Добавляем BottomNavigationView

    public LunchDetailsFragment() {
        // Обязательный пустой конструктор
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтинг макета для этого фрагмента
        View rootView = inflater.inflate(R.layout.fragment_lunch_details, container, false);

        // Установка цвета статус-бара
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
        RecyclerView recyclerViewLunch = rootView.findViewById(R.id.recyclerViewLunchDetails);
        recyclerViewLunch.setOverScrollMode(View.OVER_SCROLL_NEVER);

        NestedScrollView nestedScrollView = rootView.findViewById(R.id.nestedscrollview_lunch_details);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Найти кнопку для закрытия фрагмента
        ImageView buttonCloseFragment = rootView.findViewById(R.id.buttonCloseLunchFragment);

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
        recyclerView = rootView.findViewById(R.id.recyclerViewLunchDetails);

        // Создайте LinearLayoutManager для управления макетом RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // Создайте адаптер и установите его для RecyclerView
        adapter = new LunchDetailsAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Получите выбранную дату из аргументов
        selectedDate = getArguments().getString("selectedDate");

        // Загрузите данные из базы данных и установите их в адаптер
        loadLunchDetailsFromDatabase();

        // Найдем TextView по его id
        calLunch = rootView.findViewById(R.id.calLunch);

        // Загружаем суммарное количество калорий и устанавливаем его в TextView
        loadCaloriesSummaryFromDatabase(selectedDate);

        return rootView;
    }

    private void loadCaloriesSummaryFromDatabase(String selectedDate) {
        LunchDatabaseHelper dbHelper = new LunchDatabaseHelper(requireContext());
        double totalCalories = dbHelper.getTotalCaloriesSummaryLunch(selectedDate);
        // Устанавливаем значение в TextView
        calLunch.setText(String.format("%s ккал", totalCalories));

    }

    private void loadLunchDetailsFromDatabase() {
        // Создайте объект LunchDatabaseHelper
        LunchDatabaseHelper dbHelper = new LunchDatabaseHelper(requireContext());

        // Получите базу данных в режиме для чтения
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Выполните запрос для выборки всех записей, где дата равна выбранной дате
        Cursor cursor = db.query(
                LunchDatabaseHelper.TABLE_LUNCH,
                null,
                LunchDatabaseHelper.COLUMN_DATE + " = ?",
                new String[]{selectedDate},
                null,
                null,
                null
        );

        // Передайте курсору адаптеру для отображения данных
        adapter.swapCursor(cursor);
    }
}
