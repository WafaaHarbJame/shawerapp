package com.shawerapp.android.screens.selectsubsubject;

import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.SubSubject;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public final class SelectSubSubjectContract {

    interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {
        void initBindings();

        void addItem(SubSubject subSubject, Long practiceRequestCost);

        void updateItem(SubSubject subSubject, Long practiceRequestCost);

        void removeItem(SubSubject subSubject, Long practiceRequestCost);

        void filterList(String keyword);

        void clearFilters();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        boolean onSubSubjectClicked(SubSubject subSubject);

        void onSearchTextChanged(String keyword);
    }
}
