package com.shawerapp.android.screens.conversation;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import javax.inject.Inject;

public class ConversationFragment extends BaseFragment implements ConversationContract.View {

  public static ConversationFragment newInstance() {
    return new ConversationFragment();
  }

  @Inject ConversationContract.ViewModel viewModel;

  @Override protected FragmentLifecycle.ViewModel getViewModel() {
    return viewModel;
  }

  @Override
  public void onAttach(Context context) {
    DaggerConversationComponent.builder()
        .containerComponent(((ContainerActivity) context).getContainerComponent())
        .conversationModule(new ConversationModule(this, this))
        .build()
        .inject(this);
    super.onAttach(context);
  }

  @Override public void initBindings() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_conversation, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    return view;
  }
}
