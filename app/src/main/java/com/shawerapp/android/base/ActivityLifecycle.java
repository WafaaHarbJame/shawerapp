package com.shawerapp.android.base;

import android.os.Bundle;

/**
 * Created by Ernest on 1/20/2017.
 */

public interface ActivityLifecycle {

    interface View {

    }

    interface ViewModel {

        void onCreate(Bundle savedInstanceState);

        void onBackPressed();

        void onResume();

        void onDestroy();

    }
}
