package com.shawerapp.android.adapter.item;

import android.content.Context;
import androidx.core.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.AnswerAdapter;
import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.utils.CommonUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class AnswerFlexible extends AbstractFlexibleItem<AnswerFlexible.ViewHolder> {

    private Answer mAnswer;

    private String mCurrentUserRole;

    public AnswerFlexible(Answer answer, String currentUserRole) {
        mAnswer = answer;
        mCurrentUserRole = currentUserRole;
    }

    public Answer getAnswer() {
        return mAnswer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerFlexible that = (AnswerFlexible) o;
        return Objects.equals(getAnswer(), that.getAnswer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnswer());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_answer;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        if (adapter instanceof AnswerAdapter) {
            AnswerAdapter answerAdapter = (AnswerAdapter) adapter;

            if (mCurrentUserRole.equalsIgnoreCase(IndividualUser.ROLE_VALUE) || mCurrentUserRole.equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
                holder.outboundContainer.setVisibility(View.GONE);
                holder.inboundContainer.setVisibility(View.VISIBLE);

                if (CommonUtils.isNotEmpty(mAnswer.audioRecordingUrl())) {
                    holder.outboundVoiceContainer.setVisibility(View.VISIBLE);
                    holder.outboundPlayPauseImageView.setVisibility(View.VISIBLE);
                    holder.outboundAudioDownloading.setVisibility(View.GONE);

                    if (answerAdapter.isPlaying()) {
                        holder.outboundPlayPauseImageView.setVisibility(View.VISIBLE);
                        holder.outboundAudioDownloading.setVisibility(View.GONE);

                        if (answerAdapter.getAudioInPlay().equals(mAnswer.audioRecordingUrl())) {
                            holder.outboundPlayPauseImageView.setEnabled(true);
                            holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_pause);
                            holder.outboundPlayPauseImageView.setOnClickListener(v -> {
                                if (answerAdapter.getAudioInPlay().equals(mAnswer.audioRecordingUrl())) {
                                    holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_voice_play_dark);
                                    answerAdapter.stopPlay();
                                } else {
                                    if (!answerAdapter.isPlaying()) {
                                        holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_pause);
                                        answerAdapter.playAudio(mAnswer.audioRecordingUrl());
                                    }
                                }
                            });
                        } else {
                            audioFileSetup(answerAdapter, holder);
                        }
                    } else {
                        audioFileSetup(answerAdapter, holder);
                    }
                } else {
                    holder.outboundVoiceContainer.setVisibility(View.GONE);
                }

                if (CommonUtils.isNotEmpty(mAnswer.questionDescription())) {
                    holder.outboundMessageContainer.setVisibility(View.VISIBLE);
                    holder.outboundMessage.setText(mAnswer.questionDescription());
                } else {
                    holder.outboundMessageContainer.setVisibility(View.GONE);
                }

                if (mAnswer.fileAttachments() != null && mAnswer.fileAttachments().size() > 0) {
                    for (String attachmentUrl : mAnswer.fileAttachments()) {
                        View attachmentView = LayoutInflater.from(context).inflate(R.layout.item_outbound_question_attachment, holder.outboundAttachmentContainer, false);


                    }
                } else {
                    holder.outboundAttachmentContainer.removeAllViews();
                }
            } else {
                holder.inboundContainer.setVisibility(View.GONE);
                holder.outboundContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void audioFileSetup(AnswerAdapter answerAdapter, ViewHolder holder) {
        if (answerAdapter.isDownloaded(mAnswer.audioRecordingUrl())) {
            holder.outboundPlayPauseImageView.setVisibility(View.VISIBLE);
            holder.outboundAudioDownloading.setVisibility(View.GONE);

            holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_voice_play_dark);
            holder.outboundPlayPauseImageView.setOnClickListener(v -> {
                if (answerAdapter.getAudioInPlay().equals(mAnswer.audioRecordingUrl())) {
                    holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_voice_play_dark);
                    answerAdapter.stopPlay();
                } else {
                    if (!answerAdapter.isPlaying()) {
                        holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_pause);
                        answerAdapter.playAudio(mAnswer.audioRecordingUrl());
                    }
                }
            });
        } else {
            if (answerAdapter.isDownloading(mAnswer.audioRecordingUrl())) {
                holder.outboundPlayPauseImageView.setVisibility(View.GONE);
                holder.outboundAudioDownloading.setVisibility(View.VISIBLE);
            } else {
                holder.outboundPlayPauseImageView.setVisibility(View.VISIBLE);
                holder.outboundAudioDownloading.setVisibility(View.GONE);

                holder.outboundPlayPauseImageView.setImageResource(R.drawable.icon_voice_play_dark);
                holder.outboundPlayPauseImageView.setOnClickListener(v -> {
                    answerAdapter.downloadFile(mAnswer.audioRecordingUrl(), new FileFramework.DownloadProgressSubscriber() {
                        @Override
                        public void onProgress(long progress) {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onFinish() {

                        }
                    });
                });
            }
        }
    }

    @Override
    public void unbindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position) {
        if (adapter instanceof AnswerAdapter) {
            AnswerAdapter answerAdapter = (AnswerAdapter) adapter;
            RxAudioPlayer audioPlayer = answerAdapter.getAudioPlayer();
            audioPlayer.stopPlay();
        }
    }

    public class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.date)
        TextView date;

        @BindView(R.id.outbound)
        ViewGroup outboundContainer;

        @BindView(R.id.outboundMessageContainer)
        ViewGroup outboundMessageContainer;

        @BindView(R.id.outboundMessage)
        TextView outboundMessage;

        @BindView(R.id.outboundVoiceContainer)
        ViewGroup outboundVoiceContainer;

        @BindView(R.id.outboundPlayPauseImageView)
        ImageButton outboundPlayPauseImageView;

        @BindView(R.id.outboundAudioDownloading)
        ContentLoadingProgressBar outboundAudioDownloading;

        @BindView(R.id.outboundVoiceDurationTextView)
        ImageButton outboundVoiceDurationTextView;

        @BindView(R.id.outboundAttachmentContainer)
        LinearLayout outboundAttachmentContainer;

        @BindView(R.id.inbound)
        ViewGroup inboundContainer;

        @BindView(R.id.inboundMessageContainer)
        ViewGroup inboundMessageContainer;

        @BindView(R.id.inboundMessage)
        TextView inboundMessage;

        @BindView(R.id.inboundVoiceContainer)
        ViewGroup inboundVoiceContainer;

        @BindView(R.id.inboundPlayPauseImageView)
        ImageButton inboundPlayPauseImageView;

        @BindView(R.id.inboundAudioDownloading)
        ContentLoadingProgressBar inboundAudioDownloading;

        @BindView(R.id.inboundVoiceDurationTextView)
        ImageButton inboundVoiceDurationTextView;

        @BindView(R.id.inboundAttachmentContainer)
        LinearLayout inboundAttachmentContainer;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
