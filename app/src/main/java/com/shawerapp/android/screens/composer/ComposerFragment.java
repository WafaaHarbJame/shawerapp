package com.shawerapp.android.screens.composer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.Payment.PaymentActivity;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.ComposeRequestAdapter;
import com.shawerapp.android.adapter.item.AttachmentFlexible;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.custom.views.AudioRecordVisualizer;
import com.shawerapp.android.custom.views.CustomLineBarVisualizer;
import com.shawerapp.android.custom.views.UnswipeableViewPager;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.functions.Action;
import me.relex.circleindicator.CircleIndicator;
import timber.log.Timber;

public final class ComposerFragment extends BaseFragment
        implements ComposerContract.View {

    public static int status = 0;

    public static final String ARG_REQUEST_TYPE = "requestType";

    public static final String ARG_SELECTED_FIELD = "selectedField";

    public static final String ARG_SELECTED_SUBSUBJECT = "selectedSubSubject";

    public static final String ARG_SELECTED_LAWYER = "selectedLawyer";

    public static final String ARG_QUESTION_TO_RESPOND_TO = "questionToRespondTo";

    public static final int REQUEST_ATTACH = 1;

    public static ComposerFragment newInstance(int requestType, Field selectedField, SubSubject selectedSubSubject, LawyerUser selectedLawyerUser, Question question) {

        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_TYPE, requestType);
        args.putParcelable(ARG_SELECTED_FIELD, selectedField);
        args.putParcelable(ARG_SELECTED_SUBSUBJECT, selectedSubSubject);
        args.putParcelable(ARG_SELECTED_LAWYER, selectedLawyerUser);
        args.putParcelable(ARG_QUESTION_TO_RESPOND_TO, question);

        ComposerFragment fragment = new ComposerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ComposerContract.ViewModel mViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.viewPager)
    UnswipeableViewPager mViewPager;

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

    @BindView(R.id.lawyerPriceLbl)
    TextView lawyerPriceLbl;

    @BindView(R.id.imageViewer)
    ImageView imageViewer;
    @BindView(R.id.image_Viewer)
    ImageView image_Viewer;

    @BindView(R.id.imageViewerLayout)
    RelativeLayout imageViewerLayout;
    @BindView(R.id.imageViewer_Layout)
    RelativeLayout imageViewer_Layout;

    @BindView(R.id.closeViewer)
    Button closeViewer;
    @BindView(R.id.close_Viewer)
    Button close_Viewer;

    static ImageView imageViewer_, imageViewer__;
    static RelativeLayout imageViewerLayout_, imageViewerLayout__;


    ComposeRequestAdapter adapter;
    String amount;

    public static FlexibleAdapter<AttachmentFlexible> mAttachmentAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerComposerComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .composerModule(new ComposerModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        int[] ids = {R.id.compose_step_1, R.id.compose_step_2, R.id.compose_step_3};
        adapter = new ComposeRequestAdapter(ids);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adapter);
        mCircleIndicator.setViewPager(mViewPager);

        mVoiceProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.yankeesBlue));
        mVoiceProgressBar.setDensity(45f);

       /*RxView.clicks(mBtnPay)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onSubmitComposition());*/

        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("amount",amount);
                getActivity().startActivity(intent);
                getActivity().finish();

            }
        });

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
                .subscribe(o -> mViewModel.onPlayButtonClicked(false));

        RxView.clicks(mBtnConfirmPlay)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onPlayButtonClicked(true));

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
                    mViewModel.onCompositionEditTextChange(textChangeEvent.editable());
                });

        closeViewer.setOnClickListener(v -> {
            imageViewerLayout.setVisibility(View.GONE);
        });

        close_Viewer.setOnClickListener(v -> {
            imageViewer_Layout.setVisibility(View.GONE);
        });

        imageViewerLayout_ = imageViewerLayout;
        imageViewer_ = imageViewer;

        imageViewerLayout__ = imageViewer_Layout;
        imageViewer__ = image_Viewer;

    }

    public static void showImage(String imagePath, int status) {
        if (status == 0) {
            imageViewerLayout_.setVisibility(View.VISIBLE);
            imageViewer_.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            imageViewerLayout__.setVisibility(View.VISIBLE);
            imageViewer__.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
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
        mViewModel.onRecordButtonClicked1();
        status = page;
        mAttachmentAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(page);
        adapter.notifyDataSetChanged();
    }

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
            AttachmentFlexible item = new AttachmentFlexible(file, 0);
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
    public void setCoinsRequired(long coinsRequired) {
        mServiceFee.setText(getString(R.string.format_coins, String.valueOf(coinsRequired)));
        amount= String.valueOf(coinsRequired);
    }

    @Override
    public void setLabelVisibility(int visibility) {
        lawyerPriceLbl.setVisibility(visibility);
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
    public void updateAudioRecordVisualizer(int amplitude) {
        mVisualizer.receive(amplitude); // update the VisualizeView
        mVisualizer.invalidate(); // refresh the VisualizerView
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_question, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
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
    public void startPlaybackVisualizer(int audioSessionId) {
        mVoiceProgressBar.setPlayer(audioSessionId);
    }

    @Override
    public void stopPlaybackVisualizer() {
        mVoiceProgressBar.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContainerView.hideRightToolbarButton();
        mContainerView.hideRightText_();
    }

    @Override
    public void onResume() {
        super.onResume();
        mContainerView.ShowRightToolbarButton();

//        if (mViewModel.getPaidStatus()) {
//            mViewModel.onSubmitComposition();
//        }
    }
}
