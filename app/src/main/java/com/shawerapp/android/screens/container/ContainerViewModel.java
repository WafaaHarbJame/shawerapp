package com.shawerapp.android.screens.container;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shawerapp.android.R;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.base.FragmentStateChanger;
import com.shawerapp.android.screens.answerlist.AnswerListKey;
import com.shawerapp.android.screens.composer.ComposerFragment;
import com.shawerapp.android.screens.composer.ComposerViewModel;
import com.shawerapp.android.screens.discover.DiscoverLawyerKey;
import com.shawerapp.android.screens.newanswer.ComposeAnswerFragment;
import com.shawerapp.android.screens.profile.lawyer.personal.PrivateLawyerViewKey;
import com.shawerapp.android.screens.requestlist.RequestListKey;
import com.shawerapp.android.screens.profile.user.view.ProfileViewKey;
import com.shawerapp.android.screens.selectfield.SelectFieldFragment;
import com.shawerapp.android.screens.selectlawyer.SelectLawyerFragment;
import com.shawerapp.android.screens.selectsubsubject.SelectSubSubjectFragment;
import com.shawerapp.android.screens.settings.SettingsKey;
import com.shawerapp.android.screens.questionlist.QuestionListKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;

import java.util.concurrent.CancellationException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.completable.CompletableCreate;
import io.reactivex.internal.operators.completable.CompletableDefer;
import timber.log.Timber;

import static com.shawerapp.android.screens.container.ContainerActivity.EXTRA_TYPE;
import static com.shawerapp.android.screens.container.ContainerActivity.TYPE_LAWYER;

/**
 * Created by john.ernest on 2/16/18.
 */

public class ContainerViewModel implements ContainerContract.ViewModel {

    private BaseActivity mActivity;

    private ContainerContract.View mView;

    private BackstackDelegate mBackstackDelegate;

    private FragmentStateChanger mFragmentStateChanger;

    private boolean mIsExit;

    private String mType;

    @Inject
    LoginUtil mLoginUtil;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    private Intent starterIntent;

    @Inject
    public ContainerViewModel(BaseActivity activity, ContainerContract.View view) {
        mActivity = activity;
        mView = view;

        mType = activity.getIntent().getStringExtra(EXTRA_TYPE);

        starterIntent = activity.getIntent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mView.initBindings(mType);
        initializeBackstack(savedInstanceState);

        String token = FirebaseInstanceId.getInstance().getToken();

        if (mLoginUtil.isLoggedIn() && CommonUtils.isNotEmpty(token)) {
            mRTDataFramework
                    .saveToken(token)
                    .subscribe();

            mRTDataFramework.setupPresence();
        }
    }

    @Override
    public void onBackPressed() {
        backPress().subscribe();
    }

    @Override
    public void initializeBackstack(Bundle savedInstanceState) {
        BaseKey initialKey;
        if (mType.equals(TYPE_LAWYER)) {
            initialKey = PrivateLawyerViewKey.create();
        } else {
            initialKey = ProfileViewKey.create(mType);
        }

        mBackstackDelegate = new BackstackDelegate(null);
        mBackstackDelegate.onCreate(savedInstanceState,
                mActivity.getLastCustomNonConfigurationInstance(), History.single(initialKey));
        mBackstackDelegate.registerForLifecycleCallbacks(mActivity);

        mFragmentStateChanger = new FragmentStateChanger(mActivity.getSupportFragmentManager(), R.id.container);
        mBackstackDelegate.setStateChanger((stateChange, completionCallback) -> {
            if (stateChange.topNewState().equals(stateChange.topPreviousState())) {
                completionCallback.stateChangeComplete();
                return;
            }
            mFragmentStateChanger.handleStateChange(stateChange);
            completionCallback.stateChangeComplete();
        });
    }

    @Override
    public void onGlobalLayoutChange(ViewGroup activityView) {
        if (CommonUtils.isKeyboardShown(activityView)) {
            mView.hideTabs();
        } else {
            mView.showTabs();
        }
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        BaseFragment fragment = mView.getCurrentFragmentInFrame();
        if (fragment != null) {
            fragment.onLeftToolbarButtonClicked();
        }
    }

    @Override
    public void onRightToolbarButtonClicked() {
        BaseFragment fragment = mView.getCurrentFragmentInFrame();
        if (fragment != null) {
            fragment.onRightToolbarButtonClicked();
        }
    }

    @Override
    public void hideRightToolbarButton() {
        mView.hideRightToolbarButton();
    }

    @Override
    public void ShowRightToolbarButton() {
        mView.ShowRightToolbarButton();
    }

    @Override
    public void onTabClicked(int tabId) {
        if (mView.getCurrentFragmentInFrame() instanceof SelectFieldFragment ||
                mView.getCurrentFragmentInFrame() instanceof SelectSubSubjectFragment ||
                mView.getCurrentFragmentInFrame() instanceof SelectLawyerFragment ||
                mView.getCurrentFragmentInFrame() instanceof ComposerFragment ||
                mView.getCurrentFragmentInFrame() instanceof ComposeAnswerFragment) {
//            mView.hideRightText_();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.DialogTheme);
            builder.setTitle(R.string.title_confirm);
            builder.setMessage(R.string.message_exit_compose);
            builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> {
                dialog.dismiss();
                selectTab(tabId);
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            selectTab(tabId);
        }
    }

    @Override
    public void selectTab(int tabId) {
        switch (tabId) {
            case R.id.tabProfile:
                // Check if lawyer or normal user
                ShowRightToolbarButton();
                if (mType.equals(TYPE_LAWYER)) {
                    newTop(PrivateLawyerViewKey.create()).subscribe(navigationObserver());
                } else {
                    newTop(ProfileViewKey.create(mType)).subscribe(navigationObserver());
                }
                break;
            case R.id.tabLawyers:
                ShowRightToolbarButton();
                newTop(DiscoverLawyerKey.create()).subscribe(navigationObserver());
                break;
            case R.id.tabShawer:
                hideRightToolbarButton();
                if (mType.equals(TYPE_LAWYER)) {
                    newTop(QuestionListKey.create()).subscribe(navigationObserver());
                } else {
                    newTop(AnswerListKey.create()).subscribe(navigationObserver());
                }
                break;
            case R.id.tabPractice:
                hideRightToolbarButton();
                newTop(RequestListKey.create()).subscribe(navigationObserver());
                break;
            case R.id.tabMore:
                ShowRightToolbarButton();
                newTop(SettingsKey.create()).subscribe(navigationObserver());
                break;
        }
        mView.selectTab(tabId);
    }

    @Override
    public void onResume() {
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mBackstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public Consumer<? super Throwable> catchErrorThrowable() {
        return throwable -> {
            Timber.e(CommonUtils.getExceptionString(throwable));
            mView.showMessage(throwable.getMessage(), true);
        };
    }

    @Override
    public Completable goTo(BaseKey key) {
        return new CompletableDefer(() ->
                new CompletableCreate(
                        emitter -> {
                            Backstack backstack = mBackstackDelegate.getBackstack(); // lifecycle integration
                            backstack.goTo(key);
                            emitter.onComplete();
                        }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    public Completable goBack() {
        return new CompletableDefer(() ->
                new CompletableCreate(
                        emitter -> {
                            Backstack backstack = mBackstackDelegate.getBackstack(); // lifecycle integration
                            if (backstack.goBack()) {
                                emitter.onComplete();
                            } else {
                                mActivity.finish();
                            }
                        }))
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable backPress() {
        return new CompletableDefer(() ->
                new CompletableCreate(
                        emitter -> {
                            BaseFragment fragment = mView.getCurrentFragmentInFrame();
                            if (fragment != null) {
                                fragment.onBackButtonClicked();
                            }
                            emitter.onComplete();
                        }))
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable replace(BaseKey key) {
        return new CompletableDefer(() ->
                new CompletableCreate(
                        emitter -> {
                            Backstack backstack = mBackstackDelegate.getBackstack();
                            if (!HistoryBuilder.from(backstack).isEmpty()) {
                                backstack.setHistory(
                                        HistoryBuilder
                                                .from(backstack)
                                                .removeLast()
                                                .add(key)
                                                .build(),
                                        StateChange.REPLACE);
                            } else {
                                backstack.setHistory(
                                        HistoryBuilder
                                                .from(backstack)
                                                .add(key)
                                                .build(),
                                        StateChange.REPLACE);
                            }
                            emitter.onComplete();
                        }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    public Completable newTop(BaseKey baseKey) {
        return new CompletableDefer(() ->
                new CompletableCreate(
                        emitter -> {
                            Backstack backstack = mBackstackDelegate.getBackstack(); // lifecycle integration
                            backstack.setHistory(HistoryBuilder.single(baseKey), StateChange.REPLACE);
                            emitter.onComplete();
                        }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    public CompletableObserver navigationObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                BaseFragment top = mView.getCurrentFragmentInFrame();
            }

            @Override
            public void onError(Throwable throwable) {
                if (!(throwable instanceof CancellationException)) {
                    Timber.e(CommonUtils.getExceptionString(throwable));
                    mView.showMessage(mActivity.getString(R.string.error_default), true);
                }
            }
        };
    }

    @Override
    public void recreateActivity() {
        mActivity.finish();
        mActivity.startActivity(starterIntent);
    }

    @Override
    public void onDestroy() {

    }
}
