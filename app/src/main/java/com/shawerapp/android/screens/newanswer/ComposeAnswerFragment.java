package com.shawerapp.android.screens.newanswer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.github.barteksc.pdfviewer.PDFView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.ComposeRequestAdapter;
import com.shawerapp.android.adapter.LawyerFileAdapter;
import com.shawerapp.android.adapter.item.AttachmentFlexible;
import com.shawerapp.android.adapter.item.LawyerFileFlexible;
import com.shawerapp.android.autovalue.LawyerFile;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.custom.views.AudioRecordVisualizer;
import com.shawerapp.android.custom.views.CustomLineBarVisualizer;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.SelectableAdapter;
import io.reactivex.functions.Action;
import me.relex.circleindicator.CircleIndicator;
import timber.log.Timber;

public final class ComposeAnswerFragment extends BaseFragment implements ComposeAnswerContract.View {

    public static final String ARG_QUESTION_TO_ANSWER = "questionToAnswer";

    public static final int COMPOSE_ANSWER = 0;

    public static final int SELECT_FILE = 1;

    public static final int VIEW_FILE = 2;

    public static int status = 0;

    public static ComposeAnswerFragment newInstance(Question questionToAnswer) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_QUESTION_TO_ANSWER, questionToAnswer);
        ComposeAnswerFragment fragment = new ComposeAnswerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ComposeAnswerContract.ViewModel mViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.indicator)
    CircleIndicator mCircleIndicator;

    @BindView(R.id.primaryInstruction)
    TextView mPrimaryInstruction;

    @BindView(R.id.secondaryInstructionTextView)
    TextView secondaryInstructionTextView;

    @BindView(R.id.compose_step_1)
    View mComposeView1;

    @BindView(R.id.compose_step_2)
    View mComposeView2;

    @BindView(R.id.compose_step_3)
    View mComposeView3;

    @BindView(R.id.compose_step_4)
    View mComposeView4;

    @BindView(R.id.visualizer)
    AudioRecordVisualizer mVisualizer;

    @BindView(R.id.btnRecord)
    ImageButton mBtnRecord;

    @BindView(R.id.btnStopRecord)
    ImageButton mBtnStop;

    @BindView(R.id.btnAttach)
    View mBtnAttachFile;

    @BindView(R.id.btnPlay)
    ImageButton mBtnPlay;

    @BindView(R.id.recordingProgress)
    TextView mRecordingProgress;

    @BindView(R.id.confirmRecordProgress)
    TextView mReviewProgress;

    @BindView(R.id.voiceProgressBar)
    CustomLineBarVisualizer mVoiceProgressBar;

    @BindView(R.id.attachmentList)
    RecyclerView mAttachmentsList;

    @BindView(R.id.questionEditText)
    EditText mQuestionEditText;

    @BindView(R.id.wordCount)
    TextView mWordCount;

    @BindView(R.id.btnConfirmPlay)
    ImageButton mBtnConfirmPlay;

    @BindView(R.id.btnConfirmStop)
    ImageButton mBtnConfirmStop;

    @BindView(R.id.confirmQuestionDescription)
    TextView mConfirmQuestionDescription;

    @BindView(R.id.confirmAttachmentList)
    RecyclerView mConfirmAttachmentsList;

    @BindView(R.id.serviceFeeTextView)
    TextView mServiceFee;

    @BindView(R.id.submitCompositionView)
    View mBtnPay;

    @BindView(R.id.viewFlipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.lawyerFileList)
    RecyclerView mLawyerFileList;

    @BindView(R.id.pdfViewer)
    PDFView mPDFView;

    @BindView(R.id.closeCase)
    View mCloseCase;

    @BindView(R.id.openForDetails)
    View mOpenForDetails;

    @BindView(R.id.openForFeedback)
    View mOpenForFeedback;

    @BindView(R.id.imageViewer)
    ImageView imageViewer;
//    @BindView(R.id.image_Viewer)
//    ImageView image_Viewer;

    @BindView(R.id.imageViewerLayout)
    RelativeLayout imageViewerLayout;
//    @BindView(R.id.imageViewer_Layout)
//    RelativeLayout imageViewer_Layout;

    @BindView(R.id.closeViewer)
    Button closeViewer;
//    @BindView(R.id.close_Viewer)
//    Button close_Viewer;

    static ImageView imageViewer_;//, imageViewer__;
    static RelativeLayout imageViewerLayout_;//, imageViewerLayout__;


    private FlexibleAdapter<AttachmentFlexible> mAttachmentAdapter;

    private LawyerFileAdapter mLawyerFileAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerComposeAnswerComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .composeAnswerModule(new ComposeAnswerModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        int[] ids = {R.id.compose_step_1, R.id.compose_step_2, R.id.compose_step_3, R.id.compose_step_4};
        ComposeRequestAdapter adapter = new ComposeRequestAdapter(ids);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);
        mCircleIndicator.setViewPager(mViewPager);

        mVoiceProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.wheat));
        mVoiceProgressBar.setDensity(45f);

        RxView.clicks(mBtnPay)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onSubmitComposition());

        RxView.clicks(mBtnRecord)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onRecordButtonClicked());

        RxView.clicks(mBtnStop)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onStopPlayButtonClicked());

        RxView.clicks(mBtnAttachFile)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onAttachButtonClicked());

        RxView.clicks(mBtnPlay)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onPlayButtonClicked());

        RxView.clicks(mBtnConfirmPlay)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onPlayButtonClicked());

        RxView.clicks(mBtnConfirmStop)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onStopPlayButtonClicked());

        mAttachmentAdapter = new FlexibleAdapter<>(new ArrayList<>());
        mAttachmentsList.setItemAnimator(new DefaultItemAnimator());
        mAttachmentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAttachmentsList.setHasFixedSize(false);
        mAttachmentsList.setAdapter(mAttachmentAdapter);

        mConfirmAttachmentsList.setHasFixedSize(false);
        mConfirmAttachmentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mConfirmAttachmentsList.setAdapter(mAttachmentAdapter);

        RxTextView.afterTextChangeEvents(mQuestionEditText)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(textChangeEvent -> textChangeEvent.editable().length() <= 300)
                .subscribe(textChangeEvent -> {
                    mConfirmQuestionDescription.setText(textChangeEvent.editable());
                    mViewModel.onAnswerEditTextChange(textChangeEvent.editable());
                });

        mLawyerFileAdapter = new LawyerFileAdapter(new ArrayList<>(), v -> {
            LawyerFile item = (LawyerFile) v.getTag();
            switch (v.getId()) {
                case R.id.btnView:
                    mViewModel.onViewButtonClicked(item);
                    break;
                case R.id.clickableView:
                    mViewModel.onItemClicked(item);
                    break;
            }
        });
        mLawyerFileAdapter.addListener((FlexibleAdapter.OnItemClickListener) (view, position) -> {
            LawyerFile item = mLawyerFileAdapter.getItem(position).getLawyerFile();
            return mViewModel.onItemClicked(item);
        });
        mLawyerFileAdapter.setMode(SelectableAdapter.Mode.MULTI);
        mLawyerFileList.setItemAnimator(new DefaultItemAnimator());
        mLawyerFileList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLawyerFileList.setHasFixedSize(true);
        mLawyerFileList.setAdapter(mLawyerFileAdapter);

        RxView.clicks(mCloseCase)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onCloseCaseClicked());

        RxView.clicks(mOpenForDetails)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onOpenForDetailsClicked());

        RxView.clicks(mOpenForFeedback)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onOpenForFeedbackClicked());

        mContainerView.ShowRightToolbarButton();

        closeViewer.setOnClickListener(v -> {
            imageViewerLayout.setVisibility(View.GONE);
        });

//        close_Viewer.setOnClickListener(v -> {
//            imageViewer_Layout.setVisibility(View.GONE);
//        });

        imageViewerLayout_ = imageViewerLayout;
        imageViewer_ = imageViewer;

//        imageViewerLayout__ = imageViewer_Layout;
//        imageViewer__ = image_Viewer;

    }



    @Override
    public void setPrimaryInstruction(int primaryInstructionRes) {
        mPrimaryInstruction.setText(primaryInstructionRes);
    }

    @Override
    public void setSecondaryInstructionText(String text) {
        secondaryInstructionTextView.setText(text);
    }

    @Override
    public void changeViewPagerPage(int page) {
        status = page;
        mViewPager.setCurrentItem(page);
        mAttachmentAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(page);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_answer, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
                        mContainerView.ShowRight__ToolbarButton();
                        mContainerView.setRightToolbarTextResource(R.string.next);
                    });
                } catch (Exception x) {

                }

            }

        }, 0, 500);

        return view;
    }

    private Timer myTimer;



    @Override
    public void updateRecordProgress(String progress) {
        mRecordingProgress.setText(progress);
    }

    @Override
    public void updateReviewProgress(String progress) {
        mReviewProgress.setText(progress);
    }

    @Override
    public void updateSelectedFiles(Iterator<String> selectedFilesPaths) {
        mAttachmentAdapter.clear();
        while (selectedFilesPaths.hasNext()) {
            File file = new File(selectedFilesPaths.next());
            AttachmentFlexible item = new AttachmentFlexible(file, 1);
            mAttachmentAdapter.addItem(item);
        }
    }

    @Override
    public void updateSelectedFiles_(Iterator<LawyerFile> selectedFilesPaths) {
        mAttachmentAdapter.clear();
        while (selectedFilesPaths.hasNext()) {
            LawyerFile file = selectedFilesPaths.next();
            AttachmentFlexible item = new AttachmentFlexible(file);
            mAttachmentAdapter.addItem(item);
        }
    }

    @Override
    public void updateWordCount(int length) {
        mWordCount.setText(getString(R.string.format_word_count, length));
    }

    @Override
    public void updateRecordButtonDrawable(int resId) {
        mBtnRecord.setImageResource(resId);
    }

    @Override
    public void updatePlayButtonDrawable(int resId) {
        mBtnPlay.setImageResource(resId);
    }

    @Override
    public void showPlayButton() {
        mBtnPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStopButton() {
        mBtnStop.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCoinsToCollect(long coinsRequired) {
        mServiceFee.setText(getString(R.string.format_coins, String.valueOf(coinsRequired)));
    }

    @Override
    public void showSuccessDialog(String successMessage, Action onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setMessage(successMessage);
        builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> {
            dialog.dismiss();
            try {
                onConfirm.run();
            } catch (Exception e) {
                Timber.e(CommonUtils.getExceptionString(e));
            }
        });
        builder.show();
    }

    @Override
    public void addLawyerFile(LawyerFile lawyerFile) {
        LawyerFileFlexible item = new LawyerFileFlexible(lawyerFile);
        if (!mLawyerFileAdapter.contains(item)) {
            mLawyerFileAdapter.addItem(item);
        }
    }

    @Override
    public void updateLawyerFile(LawyerFile lawyerFile) {
        LawyerFileFlexible item = new LawyerFileFlexible(lawyerFile);
        if (mLawyerFileAdapter.contains(item)) {
            mLawyerFileAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeLawyerFile(LawyerFile lawyerFile) {
        LawyerFileFlexible item = new LawyerFileFlexible(lawyerFile);
        int position = mLawyerFileAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mLawyerFileAdapter.removeItem(position);
        }
    }

    @Override
    public boolean isSelected(LawyerFile lawyerFile) {
        LawyerFileFlexible item = new LawyerFileFlexible(lawyerFile);
        int position = mLawyerFileAdapter.getGlobalPositionOf(item);
        return mLawyerFileAdapter.isSelected(position);
    }

    @Override
    public void toggleSelection(LawyerFile lawyerFile) {
        LawyerFileFlexible item = new LawyerFileFlexible(lawyerFile);
        int position = mLawyerFileAdapter.getGlobalPositionOf(item);
        mLawyerFileAdapter.toggleSelection(position);
        mLawyerFileAdapter.notifyItemChanged(position);
    }

    @Override
    public int getDisplayedScreen() {
        return mViewFlipper.getDisplayedChild();
    }

    @Override
    public void showComposeAnswer() {
        mViewFlipper.setInAnimation(getContext(), R.anim.slide_in_from_left);
        mViewFlipper.setOutAnimation(getContext(), R.anim.slide_out_to_right);
        mViewFlipper.setDisplayedChild(COMPOSE_ANSWER);
    }

    @Override
    public void showSelectFiles(boolean isForward) {
        mViewFlipper.setInAnimation(getContext(), isForward ? R.anim.slide_in_from_right : R.anim.slide_in_from_left);
        mViewFlipper.setOutAnimation(getContext(), isForward ? R.anim.slide_out_to_left : R.anim.slide_out_to_right);
        mViewFlipper.setDisplayedChild(SELECT_FILE);
    }

    @Override
    public void showViewFile(File downloadedFile) {
        mPDFView.fromFile(downloadedFile)
                .enableSwipe(true)
                .enableDoubletap(true)
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .load();
        mViewFlipper.setInAnimation(getContext(), R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(getContext(), R.anim.slide_out_to_left);
        mViewFlipper.setDisplayedChild(VIEW_FILE);
    }

    @Override
    public void updateAudioRecordVisualizer(int amplitude) {
        mVisualizer.receive(amplitude); // update the VisualizeView
        mVisualizer.invalidate(); // refresh the VisualizerView
    }

    @Override
    public void startPlaybackVisualizer(int audioSessionId) {
        mVoiceProgressBar.setPlayer(audioSessionId);
    }

    @Override
    public void stopPlaybackVisualizer() {
        mVoiceProgressBar.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        mContainerView.ShowRightToolbarButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        mContainerView.hideRightText_();
        mContainerView.hideRightToolbarTextButton();
    }

    private ArraySet<String> mSelectedFilesPaths = new ArraySet<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
    }

    public static void showImage(String imagePath) {//, int status
//        if (status == 0) {
            imageViewerLayout_.setVisibility(View.VISIBLE);
            imageViewer_.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//        } else {
//            imageViewerLayout__.setVisibility(View.VISIBLE);
//            imageViewer__.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//        }

    }

}
