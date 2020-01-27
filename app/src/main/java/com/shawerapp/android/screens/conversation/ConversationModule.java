package com.shawerapp.android.screens.conversation;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.discover.DiscoverLawyerContract;
import com.shawerapp.android.screens.discover.DiscoverLawyerViewModel;
import dagger.Module;
import dagger.Provides;

@Module
public class ConversationModule {

  private BaseFragment mFragment;
  private ConversationContract.View mView;

  public ConversationModule(BaseFragment fragment, ConversationContract.View view) {
    mFragment = fragment;
    mView = view;
  }

  @FragmentScope
  @Provides
  public BaseFragment providesFragment() {
    return mFragment;
  }

  @FragmentScope
  @Provides
  public ConversationContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ConversationContract.ViewModel providesViewModel(
      ConversationViewModel viewModel) {
    return viewModel;
  }
}
