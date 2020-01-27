package com.shawerapp.android.screens.tutorial;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;

import javax.inject.Inject;

public final class TutorialFragment extends BaseFragment implements TutorialContract.View {

    private int[] ids = new int[]{R.id.onboarding1, R.id.onboarding2, R.id.onboarding3, R.id.onboarding4};

    public static TutorialFragment newInstance() {
        return new TutorialFragment();
    }

    @Inject
    TutorialContract.ViewModel viewModel;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerTutorialComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .tutorialModule(new TutorialModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup collection, int position) {
                return viewPager.findViewById(ids[position]);
            }

            @Override
            public void destroyItem(ViewGroup collection, int position, Object view) {

            }

            @Override
            public int getCount() {
                return ids.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
        indicator.setViewPager(viewPager);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }
}
