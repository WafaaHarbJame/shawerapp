package com.shawerapp.android.screens.discover;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.ListView;

import com.shawerapp.android.R;
import com.shawerapp.android.adapter.item.DiscoverLawyerFlexible;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.LawyerUserEvent;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.model.Country;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.profile.lawyer.view.LawyerViewKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

public class DiscoverLawyerViewModel implements DiscoverLawyerContract.ViewModel {

    private static final int FILTER_LAWYER_NAME_ASCENDING = 0;

    private static final int FILTER_LAWYER_NAME_DESCENDING = 1;

    private static final int FILTER_LIKES_ASCENDING = 2;

    private static final int FILTER_LIKES_DESCENDING = 3;

    private static final int FILTER_YEARS_OF_EXP_ASCENDING = 4;

    private static final int FILTER_YEARS_OF_EXP_DESCENDING = 5;

    private BaseFragment mFragment;

    private DiscoverLawyerContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramwork;

    @Inject
    LoginUtil mLoginUtil;

    private BehaviorProcessor<LawyerUserEvent> mLawyerUserProcessor = BehaviorProcessor.create();

    private int mSelectedFilter = FILTER_LAWYER_NAME_ASCENDING;

    private int mSelectedFilterByCountry = 0;

    private boolean mIsShowFavorites;

    @Inject
    public DiscoverLawyerViewModel(BaseFragment fragment,
                                   DiscoverLawyerContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mLawyerUserProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(lawyerUserEvent -> !lawyerUserEvent.lawyerUser().uid().equalsIgnoreCase(mLoginUtil.getUserID()))
                .subscribe(lawyserUserEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super LawyerUserEvent> lawyserUserEventConsumer() {
        return lawyerUserEvent -> {
            switch (lawyerUserEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addItem(lawyerUserEvent.lawyerUser(), mLoginUtil.getUserID());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateItem(lawyerUserEvent.lawyerUser(), mLoginUtil.getUserID());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeItem(lawyerUserEvent.lawyerUser(), mLoginUtil.getUserID());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramwork.retrieveLawyers()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mLawyerUserProcessor::onNext, mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onBackButtonClicked() {

    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_lawyers));
        mContainerView.setLeftToolbarButtonImageResource(-1);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_unset);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        if (mIsShowFavorites) {
            mIsShowFavorites = false;

            mView.clearFilters();
            mContainerView.setLeftToolbarButtonImageResource(-1);
            mContainerView.setToolbarTitle(mFragment.getString(R.string.label_lawyers));
            mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_unset);
        }
    }

    @Override
    public void onRightToolbarButtonClicked() {
        if (!mIsShowFavorites) {
            mIsShowFavorites = true;

            mView.showFavorites();
        }
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_favorite_list));
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_set);
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
    }

    @Override
    public boolean onLawyerClicked(LawyerUser profile) {
        mContainerViewModel.goTo(LawyerViewKey.create(profile))
                .subscribe(mContainerViewModel.navigationObserver());
        return false;
    }

    @Override
    public void onLawyerFavorited(LawyerUser lawyerUser) {
        boolean isFavorited = false;
        if (lawyerUser.favoritedBy() != null) {
            isFavorited = lawyerUser.favoritedBy().containsKey(mLoginUtil.getUserID()) &&
                    lawyerUser.favoritedBy().get(mLoginUtil.getUserID());
        }

        mRTDataFramwork
                .favoriteLawyer(lawyerUser, !isFavorited)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(user -> mLawyerUserProcessor.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_UPDATED, user)));
    }

    @Override
    public void onSearchTextChanged(String keyword) {
        if (CommonUtils.isNotEmpty(keyword)) {
            mView.filterList(keyword);
        } else {
            mView.clearFilters();
        }
    }

    @Override
    public void onFilterButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setTitle(R.string.sort_by);
        builder.setSingleChoiceItems(new CharSequence[]{
                        mFragment.getString(R.string.lawyer_username_ascending),
                        mFragment.getString(R.string.lawyer_username_descending),
                        mFragment.getString(R.string.likes_descending),
                        mFragment.getString(R.string.likes_ascending),
                        mFragment.getString(R.string.experience_descending),
                        mFragment.getString(R.string.experience_ascending)},
                mSelectedFilter, null);
        builder.setPositiveButton(mFragment.getString(R.string.ok_caps), (dialog, which) -> {
            AlertDialog dialog1 = (AlertDialog) dialog;
            ListView listView = dialog1.getListView();
            int selectedPosition = listView.getCheckedItemPosition();
            mSelectedFilter = selectedPosition;
            mView.sortList(getComparator(selectedPosition));
        });
        builder.show();
    }

    @Override
    public void onFilterByCountryButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setTitle(R.string.filter_by_country);
        List<String> countryList = new ArrayList<>(Arrays.asList(mFragment.getResources().getStringArray(R.array.countries)));
        countryList.remove(0);
        countryList.add(0, mFragment.getString(R.string.no_filter));
        builder.setSingleChoiceItems(countryList.toArray(new CharSequence[countryList.size()]), mSelectedFilterByCountry, null);
        builder.setPositiveButton(mFragment.getString(R.string.ok_caps), (dialog, which) -> {
            AlertDialog dialog1 = (AlertDialog) dialog;
            ListView listView = dialog1.getListView();
            int selectedPosition = listView.getCheckedItemPosition();
            mSelectedFilterByCountry = selectedPosition;
            String country = countryList.get(selectedPosition);

            Country selectedCountry = new Country(country);
            if (country.equalsIgnoreCase(mFragment.getString(R.string.no_filter))) {
                selectedCountry = new Country("");
            }
            mView.filterListByCountry(selectedCountry);
        });
        builder.show();
    }

    @Override
    public Comparator<IFlexible> getComparator(int filter) {
        switch (filter) {
            case FILTER_LAWYER_NAME_ASCENDING:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    if (CommonUtils.isNotEmpty(lawyer1.username()) && CommonUtils.isNotEmpty(lawyer2.username())) {
                        return Objects.requireNonNull(lawyer1.username()).compareToIgnoreCase(lawyer2.username());
                    } else {
                        return -1;
                    }
                };
            case FILTER_LAWYER_NAME_DESCENDING:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    if (CommonUtils.isNotEmpty(lawyer1.username()) && CommonUtils.isNotEmpty(lawyer2.username())) {
                        return Objects.requireNonNull(lawyer2.username()).compareToIgnoreCase(lawyer1.username());
                    } else {
                        return -1;
                    }
                };
            case FILTER_LIKES_ASCENDING:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    if (lawyer1.likes() != null && lawyer2.likes() != null) {
                        return Integer.compare(Objects.requireNonNull(lawyer1.likes()).size(), Objects.requireNonNull(lawyer2.likes()).size());
                    } else {
                        return -1;
                    }
                };
            case FILTER_LIKES_DESCENDING:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    if (lawyer1.likes() != null && lawyer2.likes() != null) {
                        return Integer.compare(Objects.requireNonNull(lawyer2.likes()).size(), Objects.requireNonNull(lawyer1.likes()).size());
                    } else {
                        return -1;
                    }
                };
            case FILTER_YEARS_OF_EXP_ASCENDING:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    if (CommonUtils.isNotEmpty(lawyer1.yearsOfExperience()) && CommonUtils.isNotEmpty(lawyer2.yearsOfExperience())) {
                        return Objects.requireNonNull(lawyer1.yearsOfExperience()).compareToIgnoreCase(lawyer2.yearsOfExperience());
                    } else {
                        return -1;
                    }
                };
            case FILTER_YEARS_OF_EXP_DESCENDING:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    if (CommonUtils.isNotEmpty(lawyer1.yearsOfExperience()) && CommonUtils.isNotEmpty(lawyer2.yearsOfExperience())) {
                        return Objects.requireNonNull(lawyer2.yearsOfExperience()).compareToIgnoreCase(lawyer1.yearsOfExperience());
                    } else {
                        return -1;
                    }
                };
            default:
                return (o1, o2) -> {
                    DiscoverLawyerFlexible item1 = (DiscoverLawyerFlexible) o1;
                    DiscoverLawyerFlexible item2 = (DiscoverLawyerFlexible) o2;

                    LawyerUser lawyer1 = item1.getLawyerUser();
                    LawyerUser lawyer2 = item2.getLawyerUser();

                    return Objects.requireNonNull(lawyer1.username()).compareToIgnoreCase(lawyer2.username());
                };
        }
    }

    @Override
    public int getSelectedFilter() {
        return mSelectedFilter;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {

    }
}
