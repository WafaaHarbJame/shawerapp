package com.shawerapp.android.application;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;

import javax.inject.Inject;

/**
 * Created by john.ernest on 1/3/18.
 */

public class AppFirebaseInstanceIDService extends FirebaseMessagingService {

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    LoginUtil mLoginUtil;

    private String mToken;

    @Override
    public void onCreate() {
        ((ApplicationModel) getApplication()).getAppComponent()
                .inject(this);
    }

//    @Override
//    public void onTokenRefresh() {
//
//    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
//        Log.e("NEW_TOKEN",s);
        mToken = s;

        if (mLoginUtil.isLoggedIn() && CommonUtils.isNotEmpty(mToken) && CommonUtils.isNotEmpty(mLoginUtil.getUserID())) {
            mRTDataFramework
                    .saveToken(mToken)
                    .subscribe();
        }
    }

}
