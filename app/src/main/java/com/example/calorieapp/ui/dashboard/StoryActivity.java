package com.example.calorieapp.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.calorieapp.R;

import java.util.Arrays;
import java.util.List;

public class StoryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Integer> storyImages;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        viewPager = findViewById(R.id.viewPager);
        ImageButton btnPrevious = findViewById(R.id.btnPrevious);
        ImageButton btnNext = findViewById(R.id.btnNext);

        storyImages = Arrays.asList(
                R.drawable.ice_cream,
                R.drawable.pizza,
                R.drawable.hamburger
                // добавьте свои изображения сторисов здесь
        );
        StoryPagerAdapter adapter = new StoryPagerAdapter(getSupportFragmentManager(), storyImages);
        viewPager.setAdapter(adapter);

        int position = getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition > 0) {
                    currentPosition--;
                    viewPager.setCurrentItem(currentPosition);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition < storyImages.size() - 1) {
                    currentPosition++;
                    viewPager.setCurrentItem(currentPosition);
                }
            }
        });
    }
}
