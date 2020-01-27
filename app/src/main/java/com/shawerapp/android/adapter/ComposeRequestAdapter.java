package com.shawerapp.android.adapter;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;

public class ComposeRequestAdapter extends PagerAdapter {

  private final int[] ids;

  public ComposeRequestAdapter(int[] ids) {
    this.ids = ids;
  }

  public Object instantiateItem(View collection, int position) {
    return collection.findViewById(ids[position]);
  }

  @Override public int getCount() {
    return ids.length;
  }

  @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
      return view == object;
  }

}
