package com.shawerapp.android.adapter.item;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerFile;
import com.shawerapp.android.screens.composer.ComposerFragment;
import com.shawerapp.android.screens.newanswer.ComposeAnswerFragment;
import com.shawerapp.android.screens.newanswer.ComposeAnswerViewModel;
import com.shawerapp.android.screens.newrequest.step3.ComposeRequestViewModel;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class AttachmentFlexible extends AbstractFlexibleItem<AttachmentFlexible.ViewHolder> {

    private static final DecimalFormat format = new DecimalFormat("#.##");

    private static final long MiB = 1024 * 1024;

    private static final long KiB = 1024;

    private LawyerFile mLawyerFile;

    private File mAttachment;
    private int type;



    public AttachmentFlexible(File attachment, int type) {
        mAttachment = attachment;
        this.type = type;

    }

    public AttachmentFlexible(LawyerFile lawyerFile) {
        mLawyerFile = lawyerFile;
    }

    public File getAttachment() {
        return mAttachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachmentFlexible that = (AttachmentFlexible) o;
        return Objects.equals(getAttachment(), that.getAttachment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAttachment());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_attachments;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        if (mAttachment != null) {
            holder.fileName.setText(mAttachment.getName());
            String filePath = mAttachment.getPath();
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
            holder.fileInfo.setText(context.getString(R.string.format_file_info, extension.toUpperCase(), getFileSize(new File(filePath))));
            holder.fileName.setTextColor(ContextCompat.getColor(context, R.color.yankeesBlue));
            holder.fileInfo.setTextColor(ContextCompat.getColor(context, R.color.yankeesBlue));

            if (this.type == 0) {
                if (ComposeRequestViewModel.step == 0) {
                    holder.deleteFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                    holder.deleteFileBtn.setOnClickListener(v -> {
                        adapter.removeItem(holder.getAdapterPosition());
                        adapter.notifyDataSetChanged();
                    });
                    holder.showFileBtn.setOnClickListener(v -> {
                        ComposerFragment.showImage(filePath, ComposerFragment.status);
                    });
                } else {
                    holder.deleteFileBtn.setVisibility(View.GONE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setOnClickListener(v -> {
                        ComposerFragment.showImage(filePath, ComposerFragment.status);
                    });
                }

                if (ComposerFragment.status == 0) {
                    holder.deleteFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteFileBtn.setVisibility(View.GONE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                }
            } else {
                holder.showFileBtn.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.satinSheenGold));
                holder.fileName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.satinSheenGold));
                if (ComposeAnswerViewModel.step == 0) {
                    holder.deleteFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                    holder.deleteFileBtn.setOnClickListener(v -> {
                        adapter.removeItem(holder.getAdapterPosition());
                        adapter.notifyDataSetChanged();
                    });
                    holder.showFileBtn.setOnClickListener(v -> {
                        ComposeAnswerFragment.showImage(filePath);
                    });
                } else {
                    holder.deleteFileBtn.setVisibility(View.GONE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setOnClickListener(v -> {
                        ComposeAnswerFragment.showImage(filePath);
                    });
                }
                if (ComposeAnswerFragment.status == 0) {
                    holder.deleteFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteFileBtn.setVisibility(View.GONE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                }
            }

        } else if (mLawyerFile != null) {
            holder.fileName.setText(mLawyerFile.fileName());
            String extension = mLawyerFile.fileType();
            holder.fileInfo.setText(context.getString(R.string.format_file_info, extension.toUpperCase(), getFileSize(mLawyerFile)));
            holder.fileName.setTextColor(ContextCompat.getColor(context, R.color.wheat));
            holder.fileInfo.setTextColor(ContextCompat.getColor(context, R.color.wheat));
            if (ComposeRequestViewModel.step == 0) {
                holder.deleteFileBtn.setVisibility(View.VISIBLE);
                holder.showFileBtn.setVisibility(View.VISIBLE);
                holder.deleteFileBtn.setOnClickListener(v -> {
                    adapter.removeItem(holder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                });
                holder.showFileBtn.setOnClickListener(v -> {
                    if (type == 0) {
                        ComposerFragment.showImage(mAttachment.getPath(), ComposerFragment.status);
                    } else {
                        ComposeAnswerFragment.showImage(mAttachment.getPath());
                    }
//                    ComposerFragment.showImage(mAttachment.getPath(), ComposerFragment.status);
                });
            } else {
                holder.deleteFileBtn.setVisibility(View.GONE);
                holder.showFileBtn.setVisibility(View.VISIBLE);
                holder.showFileBtn.setOnClickListener(v -> {
                    if (type == 0) {
                        ComposerFragment.showImage(mAttachment.getPath(), ComposerFragment.status);
                    } else {
                        ComposeAnswerFragment.showImage(mAttachment.getPath());
                    }
//                    ComposerFragment.showImage(mAttachment.getPath(), ComposerFragment.status);
                });
            }

//            if (ComposerFragment.status == 0) {
//                holder.deleteFileBtn.setVisibility(View.VISIBLE);
//                holder.showFileBtn.setVisibility(View.VISIBLE);
//            } else {
//                holder.deleteFileBtn.setVisibility(View.GONE);
//                holder.showFileBtn.setVisibility(View.VISIBLE);
//            }
            if (type == 0) {
                if (ComposerFragment.status == 0) {
                    holder.deleteFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteFileBtn.setVisibility(View.GONE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                }
            } else {
                holder.showFileBtn.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.satinSheenGold));
                holder.fileName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.satinSheenGold));
                if (ComposeAnswerFragment.status == 0) {
                    holder.deleteFileBtn.setVisibility(View.VISIBLE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteFileBtn.setVisibility(View.GONE);
                    holder.showFileBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public String getFileSize(File file) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }
        final double length = file.length();

        if (length > MiB) {
            return format.format(length / MiB) + " MiB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KiB";
        }
        return format.format(length) + " B";
    }

    public String getFileSize(LawyerFile file) {

        final double length = file.fileSize().doubleValue();

        if (length > MiB) {
            return format.format(length / MiB) + " MiB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KiB";
        }
        return format.format(length) + " B";
    }

    public class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.fileNameTextView)
        TextView fileName;

        @BindView(R.id.fileInfoTextView)
        TextView fileInfo;

        @BindView(R.id.showFileBtn)
        ImageButton showFileBtn;

        @BindView(R.id.deleteFileBtn)
        ImageButton deleteFileBtn;
        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
