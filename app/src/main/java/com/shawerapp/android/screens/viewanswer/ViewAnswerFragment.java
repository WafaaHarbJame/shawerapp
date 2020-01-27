package com.shawerapp.android.screens.viewanswer;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.model.DebugTools;
import com.shawerapp.android.screens.container.ContainerActivity;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import java.util.List;
import javax.inject.Inject;

public class ViewAnswerFragment extends BaseFragment implements ViewAnswerContract.View {

  public static ViewAnswerFragment newInstance() {
    return new ViewAnswerFragment();
  }

  @Inject ViewAnswerContract.ViewModel viewModel;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.lawyerUserNameTextView) TextView lawyerUserNameTextView;
  @BindView(R.id.subSubjectTextView) TextView subSubjectNameTextView;
  @BindView(R.id.onlineIndicatorImageView) ImageView onlineIndicatorImageView;

  private FlexibleAdapter<AbstractFlexibleItem> adapter;

  @Override protected FragmentLifecycle.ViewModel getViewModel() {
    return viewModel;
  }

  @Override
  public void onAttach(Context context) {
    DaggerViewAnswerComponent.builder()
        .containerComponent(((ContainerActivity) context).getContainerComponent())
        .viewAnswerModule(new ViewAnswerModule(this, this))
        .build()
        .inject(this);
    super.onAttach(context);
  }

  @Override public void initBindings() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_view_answer, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

   //Debug
    recyclerView.setOnClickListener(v -> {
      adapter.clear();
      adapter.addItems(0, DebugTools.questionCase());
    });

    final List<AbstractFlexibleItem> items = DebugTools.questionCase();
    lawyerUserNameTextView.setText("{Lawyer Username}");
    subSubjectNameTextView.setText("{Sub-Subject Name}");
    adapter = new FlexibleAdapter<>(items, this);

    recyclerView.setAdapter(adapter);
  }
}
