package com.shawerapp.android.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shawerapp.android.R;
import com.shawerapp.android.utils.CommonUtils;
import com.zhuinden.simplestack.StateChange;

/**
 * Created by john.ernest on 02/08/2017.
 */
public class FragmentStateChanger {
    private FragmentManager fragmentManager;

    private int containerId;

    public FragmentStateChanger(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleStateChange(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();

        if (stateChange.getDirection() == StateChange.FORWARD) {
            if (!CommonUtils.isRTL()) {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        } else if (stateChange.getDirection() == StateChange.BACKWARD) {
            if (!CommonUtils.isRTL()) {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        } else {
            fragmentTransaction.setCustomAnimations(-1, -1);
        }

        for (BaseKey oldKey : stateChange.<BaseKey>getPreviousState()) {
            Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if (fragment != null) {
                if (oldKey instanceof HidableKey) {
                    fragmentTransaction.hide(fragment);
                } else {
                    fragmentTransaction.remove(fragment);
                }
            }
        }
        for (BaseKey newKey : stateChange.<BaseKey>getNewState()) {
            if (newKey.equals(stateChange.topNewState())) {
                if (newKey instanceof HidableKey) {
                    Fragment hidableFragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
                    if (hidableFragment != null && !hidableFragment.isVisible()) {
                        fragmentTransaction.show(hidableFragment);
                    } else {
                        hidableFragment = newKey.newFragment();
                        fragmentTransaction.add(containerId, hidableFragment, newKey.getFragmentTag());
                    }
                } else {
                    Fragment newFragment = newKey.newFragment();
                    fragmentTransaction.add(containerId, newFragment, newKey.getFragmentTag());
                }
            }
        }
        fragmentTransaction.commitNowAllowingStateLoss();
    }
}