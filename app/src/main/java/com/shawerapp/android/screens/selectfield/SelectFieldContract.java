package com.shawerapp.android.screens.selectfield;

import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.Field;

public final class SelectFieldContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        void addItem(Field field);

        void updateItem(Field field);

        void removeItem(Field field);

        void filterList(String keyword);

        void clearFilters();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void onFieldClicked(Field field);

        void onSearchTextChanged(String keyword);
    }
}
