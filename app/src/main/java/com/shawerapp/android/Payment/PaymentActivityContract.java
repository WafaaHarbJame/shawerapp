package com.shawerapp.android.Payment;

import android.content.Intent;

import androidx.collection.ArraySet;

import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseKey;

import java.io.File;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public interface PaymentActivityContract {

    public interface View extends ActivityLifecycle.View {

       void initBindings();

        void addItem(Invoice invoice);

       void showSuccessDialog(String successMessage, Action onConfirm);

        void showMessage(String message, boolean isError);


    }

    public interface ViewModel extends ActivityLifecycle.ViewModel {


        void onActivityResult(int requestCode, int resultCode, Intent data);
        Completable newTop(BaseKey baseKey);

        CompletableObserver navigationObserver();

        Consumer<? super Throwable> catchErrorThrowable();

        void onSubmitComposition(int mRequestType, File mRecordedAudioFile, ArraySet<String> mSelectedFilesPaths, CharSequence mComposition);

        Completable goBack();

    }
}
