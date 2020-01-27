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

public class InboundAttachmentItem
    extends AbstractFlexibleItem<InboundAttachmentItem.ViewHolder> {

  private final String url;
  private final String fileInfo;
  private final @DrawableRes int chatTypeResource;

  public static InboundAttachmentItem forRequest(String url, String fileInfo) {
    return new InboundAttachmentItem(url, fileInfo, R.drawable.icon_initial_request_dark);
  }

  public static InboundAttachmentItem forQuestion(String url, String fileInfo) {
    return new InboundAttachmentItem(url, fileInfo, R.drawable.icon_initial_question_light);
  }

  public static InboundAttachmentItem forAnswer(String url, String fileInfo) {
    return new InboundAttachmentItem(url, fileInfo, R.drawable.icon_initial_answer_dark);
  }

  private InboundAttachmentItem(String url, String fileInfo, int chatTypeResource) {
    this.url = url;
    this.fileInfo = fileInfo;
    this.chatTypeResource = chatTypeResource;
  }

  public String getUrl() {
    return url;
  }

  public String getFileInfo() {
    return fileInfo;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_inbound_question_attachment;
  }

  @Override
  public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
    return new ViewHolder(view, adapter);
  }

  @Override
  public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
      int position, List<Object> payloads) {
    holder.attachmentInfoTextView.setText(fileInfo);
    holder.chatTypeImageView.setImageResource(chatTypeResource);
  }

  static final class ViewHolder extends FlexibleViewHolder {

    @BindView(R.id.downloadImageView) ImageView downloadImageView;
    @BindView(R.id.showImageView) ImageView showImageView;
    @BindView(R.id.attachmentInfoTextView) TextView attachmentInfoTextView;
    @BindView(R.id.chatTypeImageView) ImageView chatTypeImageView;

    public ViewHolder(View view,
        FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
