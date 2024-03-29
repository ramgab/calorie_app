package com.example.calorieapp.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class StoryPagerAdapter extends FragmentStatePagerAdapter {

    private List<Integer> storyImages;

    public StoryPagerAdapter(@NonNull FragmentManager fm, List<Integer> storyImages) {
        super(fm);
        this.storyImages = storyImages;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return StoryFragment.newInstance(storyImages.get(position));
    }

    @Override
    public int getCount() {
        return storyImages.size();
    }
}
