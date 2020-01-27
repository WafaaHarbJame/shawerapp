package com.shawerapp.android.screens.payment;

import android.content.Intent;

import androidx.collection.ArraySet;

import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.base.FragmentLifecycle;

import java.io.File;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public final class PaymentContract {

    public interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {

        void initBindings();

        void addItem(Invoice invoice);

        void showSuccessDialog(String successMessage, Action onConfirm);

        void showMessage(String message, boolean isError);

        BaseFragment getCurrentFragmentInFrame();

    }

    public interface ViewModel extends FragmentLifecycle.ViewModel {

        void retrieveData();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        Completable newTop(BaseKey baseKey);

        CompletableObserver navigationObserver();

        Consumer<? super Throwable> catchErrorThrowable();

        void onSubmitComposition(int mRequestType, File mRecordedAudioFile, ArraySet<String> mSelectedFilesPaths, CharSequence mComposition);

//        Completable goBack();

    }
}
