package com.shawerapp.android.adapter.item;

import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import java.util.List;

public class SystemRateLawyerItem extends AbstractFlexibleItem<SystemRateLawyerItem.ViewHolder> {

  private final boolean showPositive;
  private final boolean showNegative;

  public SystemRateLawyerItem() {
    this(true, true);
  }

  public SystemRateLawyerItem(boolean showPositive, boolean showNegative) {
    this.showPositive = showPositive;
    this.showNegative = showNegative;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_system_chat_rate_lawyer;
  }

  @Override
  public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
    return new ViewHolder(view, adapter);
  }

  @Override
  public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
      int position, List<Object> payloads) {
    if (showPositive) {
      holder.ratePositiveImageView.setVisibility(View.VISIBLE);
    } else {
      holder.ratePositiveImageView.setVisibility(View.GONE);
    }

    if (showNegative) {
      holder.rateNegativeImageView.setVisibility(View.VISIBLE);
    } else {
      holder.rateNegativeImageView.setVisibility(View.GONE);
    }
  }

  static final class ViewHolder extends FlexibleViewHolder {

    @BindView(R.id.rateNegativeImageView) ImageView rateNegativeImageView;
    @BindView(R.id.ratePositiveImageView) ImageView ratePositiveImageView;

    public ViewHolder(View view,
        FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);

    //  ratePositiveImageView.setOnClickListener(
    //      v -> adapter.mItemClickListener.onItemClick(v, getAdapterPosition()));
    //
    //  rateNegativeImageView.setOnClickListener(
    //      v -> adapter.mItemClickListener.onItemClick(v, getAdapterPosition()));
    }
  }
}
