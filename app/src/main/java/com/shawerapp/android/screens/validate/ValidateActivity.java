package com.shawerapp.android.screens.validate;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ValidateActivity extends BaseActivity implements ValidateContract.View {

    @Inject
    ValidateContract.ViewModel mViewModel;

    @BindView(R.id.codeInput)
    PinEntryEditText mCodeInput;

    @BindViews({R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine, R.id.btnZero})
    List<View> mKeyboardButtons;

    @BindView(R.id.btnDelete)
    ImageButton mBtnDelete;

    @BindView(R.id.btnActivate)
    Button mBtnActivate;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new ValidateModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_validate);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBindings() {
        RxView.clicks(mBtnDelete)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onDeleteButtonClicked());

        RxView.clicks(mBtnActivate)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onActivateButtonClicked());
    }

    @OnClick({R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine, R.id.btnZero})
    public void onKeyboardButtonClicked(View view) {
        mViewModel.onKeyboardButtonClicked(mKeyboardButtons.indexOf(view));
    }

    @Override
    public void appendPinInput(CharSequence input) {
        mCodeInput.append(input);
    }

    @Override
    public void deleteInput() {
        String text = mCodeInput.getText().toString();
        if (text.length() > 0) {
            mCodeInput.setText(text.substring(0, text.length() - 1));
        }
    }
}
