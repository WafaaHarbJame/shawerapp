package com.shawerapp.android.screens.onboarding;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.shawerapp.android.R;

public class WizardPagerAdapter extends PagerAdapter {

    public Object instantiateItem(View collection, int position) {

        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.onboarding1;
                break;
            case 1:
                resId = R.id.onboarding2;
                break;
            case 2:
                resId = R.id.onboarding3;
                break;
        }
        return collection.findViewById(resId);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup parent, int position, Object object) {
        View view = (View) object;
        parent.removeView(view);
    }
}
