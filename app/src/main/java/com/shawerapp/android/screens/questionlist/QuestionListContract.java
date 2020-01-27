package com.shawerapp.android.screens.questionlist;

import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.FragmentLifecycle;

import java.util.Comparator;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class QuestionListContract {

  interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {
    void initBindings();

    void addItem(Question question);

    void updateItem(Question question);

    void removeItem(Question question);

    void filterList(String keyword);

    void clearFilters();

    void sortList(Comparator<IFlexible> comparator);
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {

    void onTaskClicked(Question question);

    void onSearchTextChanged(String keyword);

    void onFilterButtonClicked();

    Comparator<IFlexible> getComparator(int filter);

    int getSelectedFilter();
  }
}
