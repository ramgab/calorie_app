package com.example.calorieapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.calorieapp.R;

public class StoriesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Замените fragment_stories на ваш макет для фрагмента StoriesFragment
        return inflater.inflate(R.layout.fragment_stories, container, false);
    }
}