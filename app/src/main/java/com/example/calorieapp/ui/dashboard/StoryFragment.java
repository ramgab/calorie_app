package com.example.calorieapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.calorieapp.R;

public class StoryFragment extends Fragment {

    private static final String ARG_IMAGE_RESOURCE = "imageResource";
    private int imageResource;

    public StoryFragment() {
        // Required empty public constructor
    }

    public static StoryFragment newInstance(int imageResource) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RESOURCE, imageResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageResource = getArguments().getInt(ARG_IMAGE_RESOURCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView storyImage = view.findViewById(R.id.storyImage);
        storyImage.setImageResource(imageResource);
    }
}
