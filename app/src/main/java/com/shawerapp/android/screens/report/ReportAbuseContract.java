package com.shawerapp.android.screens.report;

import com.shawerapp.android.base.FragmentLifecycle;

import java.util.List;

public final class ReportAbuseContract {

    interface View extends FragmentLifecycle.View {
        void initBindings(boolean isShowLawyers);

        void addItem(Object user, String currentUserUid);

        void updateItem(Object user, String currentUserUid);

        void removeItem(Object user, String currentUserUid);

        void filterList(String keyword);

        void clearFilters();

        List<Object> getSelectedUsers();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void onSearchTextChanged(String keyword);
    }
}
