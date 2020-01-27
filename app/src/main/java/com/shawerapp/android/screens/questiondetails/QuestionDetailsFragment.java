package com.shawerapp.android.screens.questiondetails;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.backend.glide.GlideApp;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionDetailsFragment extends BaseFragment implements QuestionDetailsContract.View {

    public static final String ARG_QUESTION = "question";

    public static QuestionDetailsFragment newInstance(Question question) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_QUESTION, question);
        QuestionDetailsFragment fragment = new QuestionDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    QuestionDetailsContract.ViewModel mViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.userNameTextView)
    TextView mUsername;

    @BindView(R.id.userTypeTextView)
    TextView mUserType;

    @BindView(R.id.subSubjectTextView)
    TextView mSubSubject;

    @BindView(R.id.iconImageView)
    ImageView mUserImage;

    @BindView(R.id.scrollview)
    ScrollView mScrollView;

    @BindView(R.id.answersContainer)
    LinearLayout mAnswersContainer;

    @BindView(R.id.status)
    TextView mStatus;

    @BindView(R.id.btnLike)
    ImageButton mBtnLike;

    @BindView(R.id.btnDislike)
    ImageButton mBtnDislike;

    @BindView(R.id.imageViewLayout)
    RelativeLayout imageViewLayout;

    @BindView(R.id.loadImageImageView)
    ImageView loadImageImageView;

    @BindView(R.id.closeImgBtn)
    Button closeImgBtn;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerQuestionDetailsComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .questionDetailsModule(new QuestionDetailsModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        RxView.clicks(mBtnLike)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onLikeButtonClicked());

        RxView.clicks(mBtnDislike)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onDislikeButtonClicked());

    }

    @Override
    public ViewGroup getAnswersContainer() {
        mContainerView.hideRightToolbarButton();
        mContainerView.hideRightText_();
        return mAnswersContainer;
    }

    @Override
    public void setUserImage(String imageUrl) {
        if (CommonUtils.isNotEmpty(imageUrl)) {
            GlideApp.with(getContext())
                    .load(imageUrl)
                    .transform(new CircleCrop())
                    .placeholder(R.mipmap.icon_profile_default)
                    .into(mUserImage);
        } else {
            mUserImage.setImageResource(R.mipmap.icon_profile_default);
        }
    }

    @Override
    public void setUsername(String username) {
        mUsername.setText(username);
    }

    @Override
    public void setUserType(String roleValue) {
        mUserType.setVisibility(View.VISIBLE);
        mUserType.setText(roleValue.toUpperCase());
    }

    @Override
    public void setSubSubjectName(String subSubjectName) {
        mSubSubject.setText(subSubjectName);
    }

    @Override
    public void setStatus(CharSequence statusMessage) {
        mStatus.setText(statusMessage);
    }

    @Override
    public void showRatingButtons() {
        mBtnLike.setVisibility(View.VISIBLE);
        mBtnDislike.setVisibility(View.VISIBLE);
    }

    @Override
    public void showImageView(String url) {
        GlideApp.with(getContext())
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .into(loadImageImageView);
        imageViewLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideImageView() {
        imageViewLayout.setVisibility(View.GONE);
    }

    @Override
    public void showGoodRating() {
        mBtnLike.setVisibility(View.VISIBLE);
        mBtnLike.setEnabled(false);
        mBtnLike.setClickable(false);

        mBtnDislike.setVisibility(View.GONE);
    }

    @Override
    public void showBadRating() {
        mBtnDislike.setVisibility(View.VISIBLE);
        mBtnDislike.setEnabled(false);
        mBtnDislike.setClickable(false);

        mBtnLike.setVisibility(View.GONE);
    }

    @Override
    public void scrollToBottom() {
        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_details, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mContainerView.hideRightToolbarButton();
        closeImgBtn.setOnClickListener(v -> hideImageView());

//        myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    getActivity().runOnUiThread(() -> {
//                        mContainerView.ShowRightToolbarButton();
//                        mContainerView.setRightToolbarTextResource(-1);
//                    });
//                } catch (Exception x) {
//
//                }
//
//
//            }
//
//        }, 0, 500);

        return view;
    }

    private Timer myTimer;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContainerView.hideRightToolbarButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        mContainerView.ShowRightToolbarButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        mContainerView.hideRightToolbarButton();
        mContainerView.hideRightText_();
        mContainerView.hideRightToolbarButton();

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
                        mContainerView.ShowRightToolbarButton();
//                        mContainerView.setRightToolbarTextResource(R.string.empty);
                        mContainerView.ShowRight_ToolbarButton();
                    });
                } catch (Exception x) {

                }


            }

        }, 0, 500);
    }
}
