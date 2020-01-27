package com.shawerapp.android.base;

import android.os.Bundle;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.shawerapp.android.R;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ernest on 1/20/2017.
 */
public abstract class BaseFragment extends Fragment implements LifecycleProvider<FragmentEvent> {

    protected static final long ANIM_DURATION = 250;

    protected static final long FADE_DURATION = 150;

    protected abstract FragmentLifecycle.ViewModel getViewModel();

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    protected Unbinder mUnbinder;

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
//        Timber.i("onAttach %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Timber.i("onCreate %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.CREATE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Timber.i("onViewCreated %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        getViewModel().onViewCreated();
        getViewModel().setupToolbar();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Timber.i("onStart %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.START);
        getViewModel().onStart();
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
//        Timber.i("onCreateAnimation %s", this.getClass().getSimpleName());

        if (nextAnim <= 0) {
            nextAnim = enter ? R.anim.fragment_fade_in : R.anim.fragment_fade_out;
        }

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Timber.i("onAnimationStart %s", BaseFragment.this.getClass().getSimpleName());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Timber.i("onAnimationEnd %s", BaseFragment.this.getClass().getSimpleName());

                if (enter) {
                    getViewModel().onAfterEnterAnimation();
                }
            }
        });

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(anim);

        return enter ? anim : animSet;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Timber.i("onResume %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
//        Timber.i("onPause %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
//        Timber.i("onStop %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.STOP);
        getViewModel().onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
//        Timber.i("onDestroyView %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
//        Timber.i("onDestroy %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        getViewModel().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
//        Timber.i("onDetach %s", this.getClass().getSimpleName());
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        getViewModel().onDetach();
        super.onDetach();
    }

    public void onBackButtonClicked() {
        getViewModel().onBackButtonClicked();
    }

    public void onLeftToolbarButtonClicked() {
        getViewModel().onLeftToolbarButtonClicked();
    }

    public void onRightToolbarButtonClicked() {
        getViewModel().onRightToolbarButtonClicked();
    }
}
