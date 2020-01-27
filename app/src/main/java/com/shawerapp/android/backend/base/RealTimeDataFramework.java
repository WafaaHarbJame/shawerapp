package com.shawerapp.android.backend.base;


import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.AnswerEvent;
import com.shawerapp.android.autovalue.CoinPackageEvent;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.FieldEvent;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.Invoice_;
import com.shawerapp.android.autovalue.LawyerFileEvent;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.LawyerUserEvent;
import com.shawerapp.android.autovalue.MemoEvent;
import com.shawerapp.android.autovalue.PracticeRequestEvent;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.QuestionEvent;
import com.shawerapp.android.autovalue.SubSubjectEvent;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.autovalue.UserEvent;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by john.ernest on 05/10/2017.
 */

public interface RealTimeDataFramework {

    public static final int EVENT_ADDED = 0;

    public static final int EVENT_UPDATED = 1;

    public static final int EVENT_REMOVED = 2;

    void setupPresence();

    Single<Object> fetchUser(String uid);

    Flowable<Object> observeUser(String uid);

    Single<Object> saveUser(Object user);

    Completable updateUser(String param, Object updateData);

    Completable saveToken(String token);

    Maybe<Boolean> checkUsernameAvailability(CharSequence username);

    Maybe<Boolean> checkEmailAvailability(CharSequence email);

    Maybe<String> getUserEmail(CharSequence username);

    Flowable<FieldEvent> retrieveFields();

    Flowable<SubSubjectEvent> retrieveSubSubjectForField(Field selectedField);

    Flowable<LawyerUserEvent> retrieveLawyersForSubSubject(SubSubject selectedSubSubject);

    Completable addQuestion(Field selectedField, SubSubject selectedSubSubject, LawyerUser selectedLawyer, Answer questionDetails, String transactionID);

    Maybe<Long> retrieveUserCoins();

    Flowable<LawyerUserEvent> retrieveLawyers();

    Flowable<QuestionEvent> retrieveQuestionsAsked();

    Flowable<QuestionEvent> retrieveQuestionsReceived();

    Completable addPracticeRequest(Field selectedField, SubSubject selectedSubSubject, String recordedAudioUrl, List<String> attachmentFileUrls, String questionDescription, Long practiceRequestCost);

    Maybe<Long> retrievePracticeRequestCost();

    Flowable<PracticeRequestEvent> retrievePracticeRequestsSent();

    Completable addMemo(CharSequence memoContent);

    Completable addInvoice(Invoice invoice);

    Flowable<MemoEvent> retrieveMemos();

    Maybe<List<AnswerEvent>> retrieveAnswers(Question question);

    Completable addAnswer(Question questionToRepondTo, Answer answer);

    Flowable<LawyerFileEvent> retrieveLawyerFiles();

    Completable updateQuestionStatus(Question question, String status);

    Completable updateQuestionFeedback(Question question, String feedback);

    Flowable<MemoEvent> retrieveMemosOfLawyer(LawyerUser lawyerUser);

    Flowable<QuestionEvent> retrieveQuestionsReceivedOfLawyer(LawyerUser lawyerUser);

    Maybe<LawyerUser> favoriteLawyer(LawyerUser lawyerUser, boolean isFavorited);

    Flowable<UserEvent> retrieveLawyerUsers();

    Flowable<UserEvent> retrieveIndividualUsers();

    Flowable<UserEvent> retrieveCommercialUsers();

    Flowable<CoinPackageEvent> retrieveCoinPackages();

    Flowable<Invoice> retrieveInvoices(boolean isLaweyer);

    Maybe<Long> addPurchasedCoins(Long coinAmount);

    Maybe<String> addPurchase(String purchaseResult, Long coinAmount);

    Maybe<String> consumePurchase(String purchaseUid);

    Completable requestLawyerProfileEdit();

    Completable reportUsers(List<Object> reportedUsers);

    Maybe<Field> fetchField(String fieldUid);

    Maybe<SubSubject> fetchSubSubject(String subSubjectUid);

}
