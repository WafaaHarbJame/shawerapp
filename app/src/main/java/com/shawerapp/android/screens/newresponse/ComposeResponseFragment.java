package com.shawerapp.android.screens.newresponse;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.ComposeRequestAdapter;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import javax.inject.Inject;
import me.relex.circleindicator.CircleIndicator;

public final class ComposeResponseFragment extends BaseFragment
    implements ComposeResponseContract.View {

  public static ComposeResponseFragment newInstance() {
    return new ComposeResponseFragment();
  }

  @Inject ComposeResponseContract.ViewModel viewModel;

  @BindView(R.id.viewPager) ViewPager viewPager;
  @BindView(R.id.indicator) CircleIndicator circleIndicator;
  @BindView(R.id.secondaryInstructionTextView) TextView secondaryInstructionTextView;
  @BindView(R.id.compose_step_3) View composeView3;
  @Override protected FragmentLifecycle.ViewModel getViewModel() {
    return viewModel;
  }

  @Override
  public void onAttach(Context context) {
    DaggerComposeResponseComponent.builder()
        .containerComponent(((ContainerActivity) context).getContainerComponent())
        .composeResponseModule(new ComposeResponseModule(this, this))
        .build()
        .inject(this);
    super.onAttach(context);
  }

  @Override public void initBindings() {
    int[] ids = {R.id.compose_step_1,R.id.compose_step_2,R.id.compose_step_3};
    ComposeRequestAdapter adapter = new ComposeRequestAdapter(ids);
    viewPager.setOffscreenPageLimit(2);
    viewPager.setAdapter(adapter);
    circleIndicator.setViewPager(viewPager);

    composeView3.findViewById(R.id.submitCompositionView).setOnClickListener(v -> viewModel.onSubmitComposition());
  }

  @Override public void setSecondaryInstructionText(String text) {
    secondaryInstructionTextView.setText(text);
  }

  @Override public void changeViewPagerPage(int page) {
    viewPager.setCurrentItem(page);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_compose_response, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    return view;
  }

  @OnPageChange(value = R.id.viewPager)
  public void onPageStateChanged(int position) {
    viewModel.onPageStateChanged(position);
  }
}
