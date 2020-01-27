package com.shawerapp.android.screens.requestlist;

import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.PracticeRequest;

import java.util.Comparator;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RequestListContract {

  interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {
    void initBindings();

    void addItem(PracticeRequest question);

    void updateItem(PracticeRequest question);

    void removeItem(PracticeRequest question);

    void filterList(String keyword);

    void clearFilters();

    void sortList(Comparator<IFlexible> comparator);
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {
    void onRequestClicked(PracticeRequest practiceRequest);

    void onSearchTextChanged(String keyword);

    void onFilterButtonClicked();

    Comparator<IFlexible> getComparator(int filter);

    int getSelectedFilter();
  }
}
