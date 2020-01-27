package com.shawerapp.android.adapter.item;

import androidx.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shawerapp.android.R;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import java.util.List;

public class OutboundVoiceItem
    extends AbstractFlexibleItem<OutboundVoiceItem.ViewHolder> {

  private final String url;
  private final String duration;
  private final @DrawableRes int chatTypeResource;

  public static OutboundVoiceItem forRequest(String url, String duration) {
    return new OutboundVoiceItem(url, duration, R.drawable.icon_initial_request_light);
  }

  public static OutboundVoiceItem forQuestion(String url, String duration) {
    return new OutboundVoiceItem(url, duration, R.drawable.icon_initial_question_light);
  }

  public static OutboundVoiceItem forAnswer(String url, String duration) {
    return new OutboundVoiceItem(url, duration, R.drawable.icon_initial_answer_light);
  }

  private OutboundVoiceItem(String url, String duration, int chatTypeResource) {
    this.url = url;
    this.duration = duration;
    this.chatTypeResource = chatTypeResource;
  }

  public String getUrl() {
    return url;
  }

  public String getDuration() {
    return duration;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_outbound_voice;
  }

  @Override
  public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
    return new ViewHolder(view, adapter);
  }

  @Override
  public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
      int position, List<Object> payloads) {
    holder.voiceDurationTextView.setText(duration);
    holder.chatTypeImageView.setImageResource(chatTypeResource);
  }

  static final class ViewHolder extends FlexibleViewHolder {

    @BindView(R.id.voiceDurationTextView) TextView voiceDurationTextView;
    @BindView(R.id.voiceProgressBar) ProgressBar voiceProgressBar;
    @BindView(R.id.playPauseImageView) ImageView playPauseImageView;
    @BindView(R.id.chatTypeImageView) ImageView chatTypeImageView;

    public ViewHolder(View view,
        FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
