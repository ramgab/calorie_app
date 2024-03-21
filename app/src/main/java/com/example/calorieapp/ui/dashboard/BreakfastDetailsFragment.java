package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
import com.example.calorieapp.ui.dashboard.BreakfastDetailsAdapter;

public class BreakfastDetailsFragment extends Fragment {

    private String selectedDate;
    private RecyclerView recyclerView;
    private BreakfastDetailsAdapter adapter;

    public BreakfastDetailsFragment() {
        // Обязательный пустой конструктор
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтинг макета для этого фрагмента
        View rootView = inflater.inflate(R.layout.fragment_breakfast_details, container, false);

        // Найти кнопку для закрытия фрагмента
        Button buttonCloseFragment = rootView.findViewById(R.id.buttonCloseBreakfastFragment);

        // Установить слушатель нажатия для кнопки
        buttonCloseFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Закрыть текущий фрагмент и вернуться на предыдущий
                requireActivity().getSupportFragmentManager().popBackStack();
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

        return rootView;
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
