package com.shawerapp.android.screens.answerlist;

import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.FragmentLifecycle;

import java.util.Comparator;

import eu.davidea.flexibleadapter.items.IFlexible;

public class AnswerListContract {

  interface View extends FragmentLifecycle.View {
      void initBindings();

      void addItem(Question question);

      void updateItem(Question question);

      void removeItem(Question question);

      void filterList(String keyword);

      void clearFilters();

      void sortList(Comparator<IFlexible> comparator);
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {

    boolean onQuestionClicked(Question subSubjectCase);

      void onSearchTextChanged(String keyword);

      void onFilterButtonClicked();

      Comparator<IFlexible> getComparator(int filter);

      int getSelectedFilter();
  }
}
