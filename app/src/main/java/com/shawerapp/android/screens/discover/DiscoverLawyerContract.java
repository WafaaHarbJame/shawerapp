package com.shawerapp.android.screens.discover;

import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.model.Country;

import java.util.Comparator;

import eu.davidea.flexibleadapter.items.IFlexible;

public class DiscoverLawyerContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        void addItem(LawyerUser lawyerUser, String userID);

        void updateItem(LawyerUser lawyerUser, String userID);

        void removeItem(LawyerUser lawyerUser, String userID);

        void filterList(String keyword);

        void clearFilters();

        void sortList(Comparator<IFlexible> comparator);

        void showFavorites();

        void filterListByCountry(Country countrySelected);
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        boolean onLawyerClicked(LawyerUser profile);

        void onLawyerFavorited(LawyerUser lawyerUser);

        void onSearchTextChanged(String keyword);

        void onFilterButtonClicked();

        void onFilterByCountryButtonClicked();

        Comparator<IFlexible> getComparator(int filter);

        int getSelectedFilter();
    }
}
