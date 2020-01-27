package com.shawerapp.android.screens.selectlawyer;

import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.base.FragmentLifecycle;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public final class SelectLawyerContract {

    interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {

        void initBindings();

        void addItem(LawyerUser lawyerUser, String userRole, String uid, String userID);

        void updateItem(LawyerUser lawyerUser, String userRole, String uid, String userID);

        void removeItem(LawyerUser lawyerUser, String userRole, String uid, String userID);

        void filterList(String keyword);

        void clearFilters();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        boolean onLawyerClicked(LawyerUser lawyerUser);

        void onSearchTextChanged(String keyword);
    }
}
