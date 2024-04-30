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
import com.example.calorieapp.ui.dashboard.DinnerDetailsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DinnerDetailsFragment extends Fragment {

    private String selectedDate;
    private RecyclerView recyclerView;
    private DinnerDetailsAdapter adapter;
    private TextView calDinner; // Добавляем TextView
    private BottomNavigationView bottomNavigationView; // Добавляем BottomNavigationView

    public DinnerDetailsFragment() {
        // Обязательный пустой конструктор
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтинг макета для этого фрагмента
        View rootView = inflater.inflate(R.layout.fragment_dinner_details, container, false);

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
        RecyclerView recyclerViewDinner = rootView.findViewById(R.id.recyclerViewDinnerDetails);
        recyclerViewDinner.setOverScrollMode(View.OVER_SCROLL_NEVER);

        NestedScrollView nestedScrollView = rootView.findViewById(R.id.nestedscrollview_dinner_details);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // Найти кнопку для закрытия фрагмента
        ImageView buttonCloseFragment = rootView.findViewById(R.id.buttonCloseDinnerFragment);

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
        recyclerView = rootView.findViewById(R.id.recyclerViewDinnerDetails);

        // Создайте LinearLayoutManager для управления макетом RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // Создайте адаптер и установите его для RecyclerView
        adapter = new DinnerDetailsAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Получите выбранную дату из аргументов
        selectedDate = getArguments().getString("selectedDate");

        // Загрузите данные из базы данных и установите их в адаптер
        loadDinnerDetailsFromDatabase();

        // Найдем TextView по его id
        calDinner = rootView.findViewById(R.id.calDinner);

        // Загружаем суммарное количество калорий и устанавливаем его в TextView
        loadCaloriesSummaryFromDatabase(selectedDate);

        return rootView;
    }

    private void loadCaloriesSummaryFromDatabase(String selectedDate) {
        DinnerDatabaseHelper dbHelper = new DinnerDatabaseHelper(requireContext());
        double totalCalories = dbHelper.getTotalCaloriesSummaryDinner(selectedDate);
        // Устанавливаем значение в TextView
        calDinner.setText(String.format("%s ккал", totalCalories));

    }

    private void loadDinnerDetailsFromDatabase() {
        // Создайте объект DinnerDatabaseHelper
        DinnerDatabaseHelper dbHelper = new DinnerDatabaseHelper(requireContext());

        // Получите базу данных в режиме для чтения
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Выполните запрос для выборки всех записей, где дата равна выбранной дате
        Cursor cursor = db.query(
                DinnerDatabaseHelper.TABLE_DINNER,
                null,
                DinnerDatabaseHelper.COLUMN_DATE + " = ?",
                new String[]{selectedDate},
                null,
                null,
                null
        );

        // Передайте курсору адаптеру для отображения данных
        adapter.swapCursor(cursor);
    }
}
