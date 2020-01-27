package com.shawerapp.android.adapter.item;

import androidx.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import java.util.List;

public class InboundMessageItem
    extends AbstractFlexibleItem<InboundMessageItem.ViewHolder> {

  private final String message;
  private final @DrawableRes int chatTypeResource;

  public static InboundMessageItem forRequest(String message) {
    return new InboundMessageItem(message, R.drawable.icon_initial_request_dark);
  }

  public static InboundMessageItem forAnswer(String message) {
    return new InboundMessageItem(message, R.drawable.icon_initial_answer_dark);
  }

  public static InboundMessageItem forQuestion(String message) {
    return new InboundMessageItem(message, R.drawable.ic_icon_initial_question_dark);
  }

  private InboundMessageItem(String message, int chatTypeResource) {
    this.message = message;
    this.chatTypeResource = chatTypeResource;
  }

  public String getMessage() {
    return message;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_inbound_message;
  }

  @Override
  public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
    return new ViewHolder(view, adapter);
  }

  @Override
  public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
      int position, List<Object> payloads) {
    holder.messageTextView.setText(message);
    holder.chatTypeImageView.setImageResource(chatTypeResource);
  }

  static final class ViewHolder extends FlexibleViewHolder {

    @BindView(R.id.messageTextView) TextView messageTextView;
    @BindView(R.id.chatTypeImageView) ImageView chatTypeImageView;

    public ViewHolder(View view,
        FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
