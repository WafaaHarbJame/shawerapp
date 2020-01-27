package com.shawerapp.android.screens.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;

import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashContract.View {

    @Inject
    SplashContract.ViewModel mViewModel;

    @BindView(R.id.splash_icon_container)
    View mSplash;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new SplashModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        System.out.println("Token => " + FirebaseInstanceId.getInstance().getToken());

    }

    @Override
    public void initBindings() {

    }

    @Override
    public void startSplashAnimation() {
        new Handler().postDelayed(() -> {
            mSplash.setAlpha(0f);
            mSplash.setVisibility(View.VISIBLE);
            mSplash.animate()
                    .alpha(1f)
                    .setDuration(2000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mViewModel.onSplashAnimationEnd();
                        }
                    })
                    .start();
        }, 300);
    }

    @Override
    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
