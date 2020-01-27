package com.shawerapp.android.screens.newrequest.step2;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.NewRequestFeeAdapter;
import com.shawerapp.android.adapter.item.NewRequestFeeItem;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.model.RequestFee;
import com.shawerapp.android.screens.container.ContainerActivity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public final class SelectRequestSubSubjectFragment extends BaseFragment
    implements SelectRequestSubSubjectContract.View {

  public static SelectRequestSubSubjectFragment newInstance() {
    return new SelectRequestSubSubjectFragment();
  }

  @Inject SelectRequestSubSubjectContract.ViewModel viewModel;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;

  private NewRequestFeeAdapter adapter;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_select_lawyer, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    return view;
  }

  @Override
  public void onAttach(Context context) {
    DaggerSelectRequestSubSubjectComponent.builder()
        .containerComponent(((ContainerActivity) context).getContainerComponent())
        .selectRequestSubSubjectModule(new SelectRequestSubSubjectModule(this, this))
        .build()
        .inject(this);
    super.onAttach(context);
  }

  @Override protected FragmentLifecycle.ViewModel getViewModel() {
    return viewModel;
  }

  @Override public void initBindings() {

  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final List<NewRequestFeeItem> newRequestFeeItems = new ArrayList<>();
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY1));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY2));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY3));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY1));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY2));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY3));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY1));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY2));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY3));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY1));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY2));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY3));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY1));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY2));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY3));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY1));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY2));
    newRequestFeeItems.add(new NewRequestFeeItem(RequestFee.DUMMY3));

    adapter = new NewRequestFeeAdapter(newRequestFeeItems, this);

    recyclerView.setAdapter(adapter);
  }

  @Override public boolean onItemClick(View view, int position) {
    final NewRequestFeeItem item = adapter.getItem(position);
    if (item != null) {
      viewModel.onRequestFeeClicked(item.getNewRequestFee());
    }

    return false;
  }
}
