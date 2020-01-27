package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import com.shawerapp.android.model.RequestFee;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import java.util.List;

public class NewRequestFeeItem extends AbstractFlexibleItem<NewRequestFeeItem.ViewHolder> {

  private final RequestFee requestFee;

  public NewRequestFeeItem(RequestFee requestFee) {
    this.requestFee = requestFee;
  }

  public RequestFee getNewRequestFee() {
    return requestFee;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_new_request_fee;
  }

  @Override public ViewHolder createViewHolder(View view,
      FlexibleAdapter<IFlexible> adapter) {
    return new ViewHolder(view, adapter);
  }

  @Override
  public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
      int position, List<Object> payloads) {
    final Context context = holder.itemView.getContext();
    final String fee =
        context.getString(R.string.label_coin_fee, requestFee.getFee());
    holder.titleTextView.setText(requestFee.getName());
    holder.descriptionTextView.setText(requestFee.getDescription());
    holder.feeTextView.setText(fee);
  }

  static final class ViewHolder extends FlexibleViewHolder {

    @BindView(R.id.titleTextView) TextView titleTextView;
    @BindView(R.id.descriptionTextView) TextView descriptionTextView;
    @BindView(R.id.feeTextView) TextView feeTextView;

    ViewHolder(View view,
        FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
      view.setOnClickListener(v -> adapter.mItemClickListener.onItemClick(v, getAdapterPosition()));
    }
  }
}
