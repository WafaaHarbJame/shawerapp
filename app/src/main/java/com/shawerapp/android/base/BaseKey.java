package com.shawerapp.android.base;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by john.ernest on 02/08/2017.
 */
public abstract class BaseKey implements Parcelable {

    public String getFragmentTag() {
        return getClass().getName();
    }

    public final BaseFragment newFragment() {
        BaseFragment fragment = createFragment();
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable("KEY", this);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected abstract BaseFragment createFragment();

    private WeakHashMap<String, View> sharedElements;

    public BaseKey addSharedElements(Map<String, View> sharedElements) {
        this.sharedElements = new WeakHashMap<>(sharedElements);
        return this;
    }

    public Map<String, View> getSharedElements() {
        return sharedElements;
    }
}
