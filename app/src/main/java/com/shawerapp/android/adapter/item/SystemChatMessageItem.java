package com.shawerapp.android.adapter.item;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import java.util.List;

public class SystemChatMessageItem extends AbstractFlexibleItem<SystemChatMessageItem.ViewHolder> {

  private final String timeStampMessage;

  public SystemChatMessageItem(String timeStampMessage) {
    this.timeStampMessage = timeStampMessage;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_system_chat_message;
  }

  @Override
  public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
    return new ViewHolder(view, adapter);
  }

  @Override public void bindViewHolder(FlexibleAdapter<IFlexible> adapter,
      ViewHolder holder, int position, List<Object> payloads) {
    holder.messageTextView.setText(timeStampMessage);
  }

  static final class ViewHolder extends FlexibleViewHolder {

    @BindView(R.id.messageTextView) TextView messageTextView;

    public ViewHolder(View view,
        FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
