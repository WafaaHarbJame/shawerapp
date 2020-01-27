package com.shawerapp.android.screens.profile.lawyer.personal;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import io.reactivex.android.schedulers.AndroidSchedulers;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.jakewharton.rxbinding2.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.MemoAdapter;
import com.shawerapp.android.adapter.item.MemoFlexible;
import com.shawerapp.android.adapter.item.QuestionAssignedHorizontalFlexible;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.backend.glide.GlideApp;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.Memo;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.transitionseverywhere.AutoTransition;
import com.transitionseverywhere.TransitionManager;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class PrivateLawyerViewFragment extends BaseFragment implements PrivateLawyerViewContract.View {

    public static PrivateLawyerViewFragment newInstance() {
        return new PrivateLawyerViewFragment();
    }

    @Inject
    PrivateLawyerViewContract.ViewModel mViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.privateLawyerView)
    ViewGroup mFragmentView;

    @BindView(R.id.iconImageView)
    ImageView mProfilePicture;

    @BindView(R.id.username)
    TextView mUsername;

    @BindView(R.id.lawyerFullName)
    TextView mLawyerFullName;

    @BindView(R.id.profileBio)
    TextView mProfileBio;

    @BindView(R.id.btnAddNewMemo)
    Button mBtnAddMemo;

    @BindView(R.id.memoContentInput)
    EditText mMemoContentInput;

    @BindView(R.id.btnPost)
    Button mBtnPost;

    @BindView(R.id.memoComposerLayout)
    View mMemoComposerLayout;

    @BindView(R.id.recyclerViewMemo)
    RecyclerView mMemoList;

    @BindView(R.id.numOfQuestionsTextView)
    TextView numOfQuestionsTextView;

    @BindView(R.id.numOfAnswersCount)
    TextView numOfAnswersTextView;

    @BindView(R.id.numOfLikesTextView)
    TextView numOfLikesTextView;

    @BindView(R.id.coinBalanceTextView)
    TextView coinBalanceTextView;

    @BindView(R.id.yearsOfExperienceTextView)
    TextView yearsOfExperienceTextView;

    @BindView(R.id.onlineIndicatorImageView)
    ImageView onlineIndicatorImageView;

    @BindView(R.id.questionsAssignedList)
    RecyclerView mQuestionsAssignedList;

    @BindView(R.id.viewPagerLeft)
    ImageButton mLeftArrow;

    @BindView(R.id.viewPagerRight)
    ImageButton mRightArrow;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.profilePictureCollapsed)
    ImageView mProfilePictureCollapsed;

    @BindView(R.id.usernameCollapsed)
    TextView mUsernameCollapsed;

    @BindView(R.id.collapsedToolbar)
    Toolbar mCollapsedToolbar;

    private MemoAdapter mMemoAdapter;

    private FlexibleAdapter<QuestionAssignedHorizontalFlexible> mQuestionsAssignedAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerPrivateLawyerViewComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .privateLawyerViewModule(new PrivateLawyerViewModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        ViewGroup.LayoutParams toolbarParams = mContainerView.getToolbar().getLayoutParams();
        toolbarParams.height = getResources().getDimensionPixelSize(R.dimen.size_55);
        mContainerView.getToolbar().setLayoutParams(toolbarParams);
        super.onDetach();
    }

    @Override
    public void initBindings() {
        mMemoAdapter = new MemoAdapter(new ArrayList<>());
        mMemoList.setItemAnimator(new DefaultItemAnimator());
        mMemoList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMemoList.setAdapter(mMemoAdapter);

        mQuestionsAssignedAdapter = new FlexibleAdapter<>(new ArrayList<>());
        mQuestionsAssignedAdapter.addListener((FlexibleAdapter.OnItemClickListener)
                (view, position) -> mViewModel.onQuestionAssignedItemClicked(mQuestionsAssignedAdapter.getItem(position).getQuestion()));
        mQuestionsAssignedList.setItemAnimator(new DefaultItemAnimator());
        mQuestionsAssignedList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mQuestionsAssignedList.setAdapter(mQuestionsAssignedAdapter);

        RxView.globalLayouts(mFragmentView)
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onGlobalLayoutChange(mFragmentView));

        RxView.clicks(mBtnAddMemo)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onAddMemoClicked());

        RxView.clicks(mBtnPost)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onPostButtonClicked(mMemoContentInput.getText()));

        RxView.clicks(mLeftArrow)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> mViewModel.onLeftArrowClicked());

        RxView.clicks(mRightArrow)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> mViewModel.onRightArrowClicked());

        RxAppBarLayout.offsetChanges(mAppBarLayout)
                .throttleLatest(100, TimeUnit.MILLISECONDS)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(offset -> {
                    AutoTransition transition = new AutoTransition();
                    transition.setDuration(100);
                    transition.excludeTarget(mFragmentView, true);
                    TransitionManager.beginDelayedTransition(mContainerView.getActivityView(), transition);
                    ViewGroup.LayoutParams toolbarParams = mContainerView.getToolbar().getLayoutParams();
                    if (offset == 0) {
                        mCollapsedToolbar.setVisibility(View.INVISIBLE);
                        toolbarParams.height = getResources().getDimensionPixelSize(R.dimen.size_55);
                    } else {
                        if (Math.abs(offset) == mAppBarLayout.getTotalScrollRange()) {
                            mCollapsedToolbar.setVisibility(View.VISIBLE);
                            toolbarParams.height = 0;
                        } else {
                            mCollapsedToolbar.setVisibility(View.INVISIBLE);
                        }
                    }
                    mContainerView.getToolbar().setLayoutParams(toolbarParams);
                });
    }

    @Override
    public void addMemo(Memo memo) {
        MemoFlexible item = new MemoFlexible(memo);
        if (!mMemoAdapter.contains(item)) {
            mMemoAdapter.addItem(mMemoAdapter.calculatePositionFor(item, new MemoFlexible.Comparator()), item);
        }
    }

    @Override
    public void updateMemo(Memo memo) {
        MemoFlexible item = new MemoFlexible(memo);
        if (mMemoAdapter.contains(item)) {
            mMemoAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeMemo(Memo memo) {
        MemoFlexible item = new MemoFlexible(memo);
        int position = mMemoAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mMemoAdapter.removeItem(position);
        }
    }

    @Override
    public void addQuestion(Question question) {
        QuestionAssignedHorizontalFlexible item = new QuestionAssignedHorizontalFlexible(question);
        if (!mQuestionsAssignedAdapter.contains(item)) {
            mQuestionsAssignedAdapter.addItem(item);
        }
    }

    @Override
    public void updateQuestion(Question question) {
        QuestionAssignedHorizontalFlexible item = new QuestionAssignedHorizontalFlexible(question);
        if (mQuestionsAssignedAdapter.contains(item)) {
            mQuestionsAssignedAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeQuestion(Question question) {
        QuestionAssignedHorizontalFlexible item = new QuestionAssignedHorizontalFlexible(question);
        int position = mQuestionsAssignedAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mQuestionsAssignedAdapter.removeItem(position);
        }
    }

    @Override
    public void setUserImage(String imageUrl) {
        if (CommonUtils.isNotEmpty(imageUrl)) {
            GlideApp.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mProfilePicture);

            GlideApp.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mProfilePictureCollapsed);
        }
    }

    @Override
    public void setUsername(CharSequence username) {
        mUsername.setText(username);
        mUsernameCollapsed.setText(username);
    }

    @Override
    public void setLawyerFullName(CharSequence fullName) {
        mLawyerFullName.setText(fullName);
    }

    @Override
    public void setProfileBio(CharSequence profileBio) {
        mProfileBio.setText(profileBio);
    }

    @Override
    public void setQuestionsReceived(String questionsReceived) {
        numOfQuestionsTextView.setText(questionsReceived);
    }

    @Override
    public void setAnswersSent(String answersSent) {
        numOfAnswersTextView.setText(answersSent);
    }

    @Override
    public void setLikes(String likes) {
        numOfLikesTextView.setText(likes);
    }

    @Override
    public void setCoins(String coins) {
        coinBalanceTextView.setText(coins);
    }

    @Override
    public void setYearsOfExperience(String yearsOfExperience) {
        yearsOfExperienceTextView.setText(yearsOfExperience);
    }

    @Override
    public void hideMemoInput() {
        mMemoComposerLayout.setVisibility(View.GONE);
        mMemoContentInput.setText("");
        CommonUtils.hideKeyBoard(getActivity());
    }

    @Override
    public void showMemoInput() {
        mMemoComposerLayout.setVisibility(View.VISIBLE);
        mMemoContentInput.requestFocus();
        CommonUtils.showKeyboard(mMemoContentInput, getActivity());
    }

    @Override
    public void scrollHorizontalQuestions(int pixelOffset) {
        mQuestionsAssignedList.smoothScrollBy(pixelOffset, 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lawyer_profile_private_view, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
//                        mContainerView.ShowRight__ToolbarButton();
//                        mContainerView.setRightToolbarTextResource(R.string.empty);
                    });
                } catch (Exception x) {

                }

            }

        }, 0, 500);

        return view;
    }

    private Timer myTimer;


}
