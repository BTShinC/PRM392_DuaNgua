package com.example.group1_duangua;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class CarouselEffect implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.7f;  // nhỏ khi ở rìa
    private static final float MAX_SCALE = 1.2f;  // to khi ở giữa

    @Override
    public void transformPage(@NonNull View page, float position) {
        float scale = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setAlpha(0.5f + (1 - Math.abs(position)) * 0.5f);
    }
}

