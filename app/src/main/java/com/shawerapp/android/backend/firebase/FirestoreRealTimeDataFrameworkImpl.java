package com.shawerapp.android.backend.firebase;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.AnswerEvent;
import com.shawerapp.android.autovalue.AvailabilityCheckResponse;
import com.shawerapp.android.autovalue.CoinPackage;
import com.shawerapp.android.autovalue.CoinPackageEvent;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.FieldEvent;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.Invoice_;
import com.shawerapp.android.autovalue.LawyerFile;
import com.shawerapp.android.autovalue.LawyerFileEvent;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.LawyerUserEvent;
import com.shawerapp.android.autovalue.Memo;
import com.shawerapp.android.autovalue.MemoEvent;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.autovalue.PracticeRequestEvent;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.QuestionEvent;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.autovalue.SubSubjectEvent;
import com.shawerapp.android.autovalue.UserEvent;
import com.shawerapp.android.autovalue.annotation.GetEmailResponse;
import com.shawerapp.android.backend.base.ConnectivityUtils;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.completable.CompletableCreate;
import io.reactivex.internal.operators.flowable.FlowableCreate;
import io.reactivex.internal.operators.maybe.MaybeCreate;
import io.reactivex.internal.operators.single.SingleCreate;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by john.ernest on 05/10/2017.
 */

public class FirestoreRealTimeDataFrameworkImpl implements RealTimeDataFramework {

    private static final String USERS = "users";

    private static final String INVOICES = "invoices";

    private static final String FIELDS = "fields";

    private static final String SUBSUBJECTS = "subSubjects";

    private static final String USER_ROLE = "role";

    private static final String QUESTIONS = "questions";

    private static final String ANSWERS = "answers";

    private static final String SETTINGS = "settings";

    private static final String PRACTICE_REQUEST = "practiceRequests";

    private static final String FILES = "files";

    private static final String COIN_PACKAGES = "coinPackages";

    private static final String PURCHASES = "purchases";

    private static final String EXPENDITURES = "expenditures";

    private Context mContext;

    private FirebaseFirestore mFirestore;

    private CollectionReference mUsersRef;

    private ConnectivityUtils mUtils;

    private LoginUtil mLoginUtil;

    private FirebaseFunctions mFunctions;

    private HttpsCallableResult mHttpsCallableResult;

    @Inject
    public FirestoreRealTimeDataFrameworkImpl(Application application, ConnectivityUtils connectivityUtils, LoginUtil loginUtil) {
        mContext = application.getApplicationContext();
        mUtils = connectivityUtils;
        mLoginUtil = loginUtil;

        mFunctions = FirebaseFunctions.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build());

        mUsersRef = mFirestore.collection(USERS);
    }

    @Override
    public void setupPresence() {
        if (CommonUtils.isNotEmpty(mLoginUtil.getUserID())) {
            mUsersRef.document(mLoginUtil.getUserID())
                    .update("presence", "online")
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> Timber.e(CommonUtils.getExceptionString(e)));

            FirebaseDatabase.getInstance()
                    .getReference("presence")
                    .child(mLoginUtil.getUserID())
                    .setValue("online")
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> Timber.e(CommonUtils.getExceptionString(e)));

            FirebaseDatabase.getInstance()
                    .getReference("presence")
                    .child(mLoginUtil.getUserID())
                    .onDisconnect()
                    .setValue("offline")
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> Timber.e(CommonUtils.getExceptionString(e)));
        }
    }

    @Override
    public Single<Object> fetchUser(String uid) {
        return new SingleCreate<>(emitter ->
                mUsersRef.document(uid).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists() && documentSnapshot != null) {
                                String role = (String) documentSnapshot.get(USER_ROLE);
                                if (CommonUtils.isNotEmpty(role)) {
                                    if (role.equalsIgnoreCase(IndividualUser.ROLE_VALUE)) {
                                        emitter.onSuccess(IndividualUser.createFromSnapshot(documentSnapshot));
                                    } else if (role.equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
                                        emitter.onSuccess(CommercialUser.createFromSnapshot(documentSnapshot));
                                    } else if (role.equalsIgnoreCase(LawyerUser.ROLE_VALUE)) {
                                        emitter.onSuccess(LawyerUser.createFromSnapshot(documentSnapshot));
                                    } else {
                                        emitter.onError(new Throwable(mContext.getString(R.string.error_invalid_role)));
                                    }
                                } else {
                                    emitter.onError(new Throwable(mContext.getString(R.string.error_invalid_role)));
                                }
                            } else {
                                emitter.onError(new Throwable(mContext.getString(R.string.error_no_user_found)));
                            }
                        })
                        .addOnFailureListener(error -> emitter.onError(error)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<Object> observeUser(String uid) {
        return new FlowableCreate<>(emitter ->
                mUsersRef.document(uid).addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        emitter.onError(e);
                    } else {
                        if (documentSnapshot.exists() && documentSnapshot != null) {
                            String role = (String) documentSnapshot.get(USER_ROLE);
                            if (CommonUtils.isNotEmpty(role)) {
                                if (role.equalsIgnoreCase(IndividualUser.ROLE_VALUE)) {
                                    emitter.onNext(IndividualUser.createFromSnapshot(documentSnapshot));
                                } else if (role.equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
                                    emitter.onNext(CommercialUser.createFromSnapshot(documentSnapshot));
                                } else if (role.equalsIgnoreCase(LawyerUser.ROLE_VALUE)) {
                                    emitter.onNext(LawyerUser.createFromSnapshot(documentSnapshot));
                                } else {
                                    emitter.onError(new Throwable(mContext.getString(R.string.error_invalid_role)));
                                }
                            } else {
                                emitter.onError(new Throwable(mContext.getString(R.string.error_invalid_role)));
                            }
                        } else {
                            emitter.onError(new Throwable(mContext.getString(R.string.error_no_user_found)));
                        }
                    }
                }), BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Object> saveUser(Object user) {
        return mUtils.withNetworkCheck(new SingleCreate<>(
                emitter -> {
                    if (user instanceof IndividualUser) {
                        IndividualUser individualUser = (IndividualUser) user;
                        if (CommonUtils.isNotEmpty(individualUser.uid())) {
                            mUsersRef.document(individualUser.uid())
                                    .set(individualUser.toMap(), SetOptions.merge())
                                    .addOnSuccessListener(onSuccess -> emitter.onSuccess(individualUser))
                                    .addOnFailureListener(emitter::onError);
                        } else {
                            mUsersRef.add(individualUser.toMap())
                                    .addOnSuccessListener(documentReference -> emitter.onSuccess(individualUser))
                                    .addOnFailureListener(emitter::onError);
                        }
                    } else if (user instanceof CommercialUser) {
                        CommercialUser CommercialUser = (CommercialUser) user;
                        if (CommonUtils.isNotEmpty(CommercialUser.uid())) {
                            mUsersRef.document(CommercialUser.uid())
                                    .set(CommercialUser.toMap(), SetOptions.merge())
                                    .addOnSuccessListener(onSuccess -> emitter.onSuccess(CommercialUser))
                                    .addOnFailureListener(emitter::onError);
                        } else {
                            mUsersRef.add(CommercialUser.toMap())
                                    .addOnSuccessListener(documentReference -> emitter.onSuccess(CommercialUser))
                                    .addOnFailureListener(emitter::onError);
                        }
                    } else if (user instanceof LawyerUser) {
                        LawyerUser LawyerUser = (LawyerUser) user;
                        if (CommonUtils.isNotEmpty(LawyerUser.uid())) {
                            mUsersRef.document(LawyerUser.uid())
                                    .set(LawyerUser.toMap(), SetOptions.merge())
                                    .addOnSuccessListener(onSuccess -> emitter.onSuccess(LawyerUser))
                                    .addOnFailureListener(emitter::onError);
                        } else {
                            mUsersRef.add(LawyerUser.toMap())
                                    .addOnSuccessListener(documentReference -> emitter.onSuccess(LawyerUser))
                                    .addOnFailureListener(emitter::onError);
                        }
                    } else {
                        emitter.onError(new Throwable(mContext.getString(R.string.error_invalid_role)));
                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable updateUser(String param, Object updateData) {
        return mUtils.withNetworkCheck(
                firestoreTransaction(transaction -> {
                    DocumentReference userRef = mUsersRef.document(mLoginUtil.getUserID());
                    transaction.update(userRef, param, updateData);
                    return updateData;
                }))
                .toCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable saveToken(String token) {
        return mUtils.withNetworkCheck(
                firestoreTransaction(transaction -> {
                    DocumentReference userRef = mUsersRef.document(mLoginUtil.getUserID());
                    transaction.update(userRef, "fcmToken", token);
                    return token;
                }))
                .toCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Boolean> checkUsernameAvailability(CharSequence username) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username.toString());

        return mUtils.withNetworkCheck(Maybe.<Boolean>create(
                emitter -> mFunctions.getHttpsCallable("authCheckUsername")
                        .call(data)
                        .addOnSuccessListener(httpsCallableResult -> {
                            Gson gson = new Gson();
                            String jsonResult = gson.toJson(httpsCallableResult.getData());
                            try {
                                AvailabilityCheckResponse response = AvailabilityCheckResponse.typeAdapter(gson).fromJson(jsonResult);
                                if (response.code() == AvailabilityCheckResponse.SUCCESS) {
                                    emitter.onSuccess(response.available());
                                } else {
                                    emitter.onError(new Throwable(response.message()));
                                }
                            } catch (IOException e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnFailureListener(emitter::onError)
                        .addOnCanceledListener(emitter::onComplete)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Boolean> checkEmailAvailability(CharSequence email) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email.toString());

        return mUtils.withNetworkCheck(Maybe.<Boolean>create(
                emitter -> mFunctions.getHttpsCallable("authCheckEmail")
                        .call(data)
                        .addOnSuccessListener(httpsCallableResult -> {
                            Gson gson = new Gson();
                            String jsonResult = gson.toJson(httpsCallableResult.getData());
                            try {
                                AvailabilityCheckResponse response = AvailabilityCheckResponse.typeAdapter(gson).fromJson(jsonResult);
                                if (response.code() == AvailabilityCheckResponse.SUCCESS) {
                                    emitter.onSuccess(response.available());
                                } else {
                                    emitter.onError(new Throwable(response.message()));
                                }
                            } catch (IOException e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnFailureListener(emitter::onError)
                        .addOnCanceledListener(emitter::onComplete)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> getUserEmail(CharSequence username) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username.toString());

        return mUtils.withNetworkCheck(Maybe.<String>create(
                emitter -> mFunctions.getHttpsCallable("authGetEmail")
                        .call(data)
                        .addOnSuccessListener(httpsCallableResult -> {
                            Gson gson = new Gson();
                            String jsonResult = gson.toJson(httpsCallableResult.getData());
                            try {
                                GetEmailResponse response = GetEmailResponse.typeAdapter(gson).fromJson(jsonResult);
                                if (response.code() == GetEmailResponse.SUCCESS) {
                                    if (CommonUtils.isNotEmpty(response.email())) {
                                        emitter.onSuccess(response.email());
                                    } else {
                                        emitter.onError(new Throwable(response.message()));
                                    }
                                } else {
                                    emitter.onError(new Throwable(response.message()));
                                }
                            } catch (IOException e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnFailureListener(emitter::onError)
                        .addOnCanceledListener(emitter::onComplete)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<FieldEvent> retrieveFields() {
        return mUtils.withNetworkCheck(new FlowableCreate<FieldEvent>(
                emitter -> {
                    CollectionReference fieldCollectionRef = mFirestore.collection(FIELDS);

                    fieldCollectionRef.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Field field = Field.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(FieldEvent.create(RealTimeDataFramework.EVENT_ADDED, field));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(FieldEvent.create(RealTimeDataFramework.EVENT_REMOVED, field));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(FieldEvent.create(RealTimeDataFramework.EVENT_UPDATED, field));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<SubSubjectEvent> retrieveSubSubjectForField(Field selectedField) {
        return mUtils.withNetworkCheck(new FlowableCreate<SubSubjectEvent>(
                emitter -> {
                    Query subSubjectQuery = mFirestore.collection(SUBSUBJECTS)
                            .whereEqualTo("fieldUid", selectedField.uid());

                    subSubjectQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                SubSubject subSubject = SubSubject.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(SubSubjectEvent.create(RealTimeDataFramework.EVENT_ADDED, subSubject));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(SubSubjectEvent.create(RealTimeDataFramework.EVENT_REMOVED, subSubject));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(SubSubjectEvent.create(RealTimeDataFramework.EVENT_UPDATED, subSubject));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<LawyerUserEvent> retrieveLawyersForSubSubject(SubSubject selectedSubSubject) {
        return mUtils.withNetworkCheck(new FlowableCreate<LawyerUserEvent>(
                emitter -> {
                    Query lawyerUserQuery = mFirestore.collection(USERS)
                            .whereEqualTo("role", LawyerUser.ROLE_VALUE)
                            .whereEqualTo("status", "activated")
                            .whereEqualTo("subSubjects." + selectedSubSubject.uid(), true);

                    lawyerUserQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                LawyerUser lawyerUser = LawyerUser.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_ADDED, lawyerUser));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_REMOVED, lawyerUser));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_UPDATED, lawyerUser));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable addQuestion(Field selectedField, SubSubject selectedSubSubject, LawyerUser selectedLawyer, Answer questionDetails, String transactionID) {
        Long serviceFee;
        if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
            serviceFee = selectedLawyer.individualFees().get(selectedSubSubject.uid());
        } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
            serviceFee = selectedLawyer.commercialFees().get(selectedSubSubject.uid());
        } else {
            serviceFee = 0L;
        }

        return mUtils.withNetworkCheck(retrieveUserCoins()
                .flatMapCompletable(userCoins -> new CompletableCreate(
                        emitter -> {
//                            if (userCoins < serviceFee) {
//                                emitter.onError(new Throwable(mContext.getString(R.string.error_not_enough_coins)));
//                            }

                            DocumentReference userRef = mFirestore
                                    .collection(USERS)
                                    .document(mLoginUtil.getUserID());

                            userRef.get()
                                    .addOnSuccessListener(userSnapshot -> {
                                        WriteBatch batch = mFirestore.batch();

                                        DocumentReference questionRef = mFirestore.collection(QUESTIONS)
                                                .document();

                                        Question newQuestion = Question.create(
                                                questionRef.getId(),
                                                selectedSubSubject.ar_subSubjectName(),
                                                selectedSubSubject.subSubjectName(),
                                                selectedSubSubject.uid(),
                                                selectedField.uid(),
                                                selectedField.ar_fieldName(),
                                                selectedField.fieldName(),
                                                mLoginUtil.getUserID(),
                                                mLoginUtil.getUserRole(),
                                                mLoginUtil.getUsername(),
                                                (String) userSnapshot.get("imageUrl"),
                                                selectedLawyer.uid(),
                                                selectedLawyer.fullName(),
                                                selectedLawyer.username(),
                                                serviceFee,
                                                new Date(),
                                                Question.Status.PENDING_ANSWER,
                                                null,
                                                true,
                                                true,
                                                transactionID
                                                );

                                        DocumentReference answerRef = mFirestore
                                                .collection(ANSWERS)
                                                .document();

                                        Answer answerWithUid = Answer.create(
                                                answerRef.getId(),
                                                questionDetails.audioRecordingUrl(),
                                                questionDetails.questionDescription(),
                                                questionDetails.fileAttachments() != null && questionDetails.fileAttachments().size() > 0 ? questionDetails.fileAttachments() : null,
                                                mLoginUtil.getPhoneNumber(),
                                                (String) userSnapshot.get("fullName"),
                                                (String) userSnapshot.get("role"),
                                                new Date(),
                                                questionRef.getId(),
                                                newQuestion.status());

                                        long currentCoins = 0L;
                                        if (userSnapshot.get("coins") != null) {
                                            currentCoins = (long) userSnapshot.get("coins");
                                        }
                                        long newCoins = currentCoins - serviceFee;

                                        long questionsAsked = 0L;
                                        if (userSnapshot.get("questionsAsked") != null) {
                                            questionsAsked = (long) userSnapshot.get("questionsAsked");
                                        }

                                        batch.set(questionRef, newQuestion.toMap());
                                        batch.set(answerRef, answerWithUid.toMap());
                                        batch.update(userRef, "coins", newCoins);
                                        batch.update(userRef, "questionsAsked", questionsAsked + 1);

                                        batch.commit()
                                                .addOnSuccessListener(aVoid -> emitter.onComplete())
                                                .addOnFailureListener(emitter::onError);
                                    })
                                    .addOnFailureListener(emitter::onError);
                        })))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference lawyerRef = mFirestore
                            .collection(USERS)
                            .document(selectedLawyer.uid());

                    lawyerRef.get()
                            .addOnSuccessListener(lawyerSnapshot -> {
                                long questionsReceived = 0L;
                                if (lawyerSnapshot.get("questionsReceived") != null) {
                                    questionsReceived = (long) lawyerSnapshot.get("questionsReceived");
                                }
                                lawyerRef.update("questionsReceived", questionsReceived + 1)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference expenditureRef = mFirestore
                            .collection(EXPENDITURES)
                            .document();

                    Map<String, Object> expenditure = new HashMap<>();
                    expenditure.put("uid", expenditureRef.getId());
                    expenditure.put("userUid", mLoginUtil.getUserID());
                    expenditure.put("expenditureType", "question");
                    expenditure.put("dateCreated", new Date());
                    expenditure.put("coinsSpent", serviceFee);

                    expenditureRef.set(expenditure)
                            .addOnSuccessListener(aVoid -> emitter.onComplete())
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Long> retrieveUserCoins() {
        return Maybe.<Long>create(
                emitter -> mFirestore.collection(USERS)
                        .whereEqualTo("uid", mLoginUtil.getUserID())
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                emitter.onSuccess(1000L);
//                                emitter.onError(e);
                            } else {
                                Long userCoins = (Long) queryDocumentSnapshots.getDocuments().get(0).get("coins");
                                emitter.onSuccess(userCoins);
                            }
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<LawyerUserEvent> retrieveLawyers() {
        return mUtils.withNetworkCheck(new FlowableCreate<LawyerUserEvent>(
                emitter -> {
                    Query lawyerUserQuery = mFirestore.collection(USERS)
                            .whereEqualTo("status", "activated")
                            .whereEqualTo("role", LawyerUser.ROLE_VALUE);

                    lawyerUserQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                LawyerUser lawyerUser = LawyerUser.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_ADDED, lawyerUser));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_REMOVED, lawyerUser));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(LawyerUserEvent.create(RealTimeDataFramework.EVENT_UPDATED, lawyerUser));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<QuestionEvent> retrieveQuestionsAsked() {
        return mUtils.withNetworkCheck(new FlowableCreate<QuestionEvent>(
                emitter -> {
                    Query questionQuery = mFirestore.collection(QUESTIONS)
                            .whereEqualTo("askerUid", mLoginUtil.getUserID())
                            .orderBy("dateAdded", Query.Direction.DESCENDING);

                    questionQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();
                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Question question = Question.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_ADDED, question));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_REMOVED, question));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_UPDATED, question));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<QuestionEvent> retrieveQuestionsReceived() {
        return mUtils.withNetworkCheck(new FlowableCreate<QuestionEvent>(
                emitter -> {
                    Query questionQuery = mFirestore.collection(QUESTIONS)
                            .whereEqualTo("assignedLawyerUid", mLoginUtil.getUserID())
                            .orderBy("dateAdded", Query.Direction.DESCENDING);//Edited

                    questionQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Question question = Question.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_ADDED, question));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_REMOVED, question));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_UPDATED, question));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable addPracticeRequest(Field selectedField, SubSubject selectedSubSubject, String recordedAudioUrl, List<String> attachmentFileUrls, String questionDescription, Long practiceRequestCost) {
        return mUtils.withNetworkCheck(retrieveUserCoins())
                .flatMapCompletable(userCoins -> new CompletableCreate(
                        emitter -> {
                            if (userCoins < practiceRequestCost) {
                                emitter.onError(new Throwable(mContext.getString(R.string.error_not_enough_coins)));
                            }

                            DocumentReference practiceRequestRef = mFirestore.collection(PRACTICE_REQUEST)
                                    .document();

                            PracticeRequest newPracticeRequest = PracticeRequest.builder()
                                    .uid(practiceRequestRef.getId())
                                    .subSubjectName(selectedSubSubject.ar_subSubjectName())
                                    .subSubjectName(selectedSubSubject.subSubjectName())
                                    .serviceFee(practiceRequestCost)
                                    .origin(mContext.getString(R.string.app_name))
                                    .dateCreated(new Date())
                                    .dateFulfilled(null)
                                    .audioRecordingUrl(recordedAudioUrl)
                                    .questionDescription(questionDescription)
                                    .fileAttachments(attachmentFileUrls != null && attachmentFileUrls.size() > 0 ? attachmentFileUrls : null)
                                    .requesterUid(mLoginUtil.getUserID())
                                    .status(PracticeRequest.Status.UNFULFILLED)
                                    .build();

                            practiceRequestRef.set(newPracticeRequest.toMap())
                                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);

                        }))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference userRef = mFirestore.collection(USERS)
                            .document(mLoginUtil.getUserID());

                    userRef.get()
                            .addOnSuccessListener(userSnapshot -> {
                                WriteBatch batch = mFirestore.batch();

                                long currentCoins = 0L;
                                if (userSnapshot.get("coins") != null) {
                                    currentCoins = (long) userSnapshot.get("coins");
                                }

                                long newCoins = currentCoins - practiceRequestCost;
                                batch.update(userRef, "coins", newCoins);

                                long practiceRequests = 0L;
                                if (userSnapshot.get("practiceRequests") != null) {
                                    practiceRequests = (long) userSnapshot.get("practiceRequests");
                                }
                                batch.update(userRef, "practiceRequests", practiceRequests + 1);

                                batch.commit()
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference expenditureRef = mFirestore
                            .collection(EXPENDITURES)
                            .document();

                    Map<String, Object> expenditure = new HashMap<>();
                    expenditure.put("uid", expenditureRef.getId());
                    expenditure.put("userUid", mLoginUtil.getUserID());
                    expenditure.put("expenditureType", "practiceRequest");
                    expenditure.put("dateCreated", new Date());
                    expenditure.put("coinsSpent", practiceRequestCost);

                    expenditureRef.set(expenditure)
                            .addOnSuccessListener(aVoid -> emitter.onComplete())
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Long> retrievePracticeRequestCost() {
        return Maybe.<Long>create(
                emitter -> {
                    Query costQuery = mFirestore.collection(SETTINGS)
                            .whereEqualTo("status", "enabled");

                    costQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Long practiceRequestCost = (Long) queryDocumentSnapshots.getDocuments().get(0).get("practiceRequestCost");
                            emitter.onSuccess(practiceRequestCost);
                        }
                    });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<PracticeRequestEvent> retrievePracticeRequestsSent() {
        return mUtils.withNetworkCheck(new FlowableCreate<PracticeRequestEvent>(
                emitter -> {
                    Query practiceRequestQuery = mFirestore.collection(PRACTICE_REQUEST)
                            .whereEqualTo("requesterUid", mLoginUtil.getUserID())
                            .orderBy("dateCreated", Query.Direction.DESCENDING);

                    practiceRequestQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                PracticeRequest practiceRequest = PracticeRequest.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(PracticeRequestEvent.create(RealTimeDataFramework.EVENT_ADDED, practiceRequest));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(PracticeRequestEvent.create(RealTimeDataFramework.EVENT_REMOVED, practiceRequest));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(PracticeRequestEvent.create(RealTimeDataFramework.EVENT_UPDATED, practiceRequest));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable addMemo(CharSequence memoContent) {
        return mUtils.withNetworkCheck(Completable
                .create(emitter -> {
                    DocumentReference memoRef = mFirestore.collection(USERS)
                            .document(mLoginUtil.getUserID())
                            .collection("memos")
                            .document();

                    Memo memo = Memo.create(
                            memoRef.getId(),
                            new Date(),
                            memoContent.toString());

                    memoRef.set(memo.toMap())
                            .addOnSuccessListener(aVoid -> emitter.onComplete())
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<MemoEvent> retrieveMemos() {
        return mUtils.withNetworkCheck(new FlowableCreate<MemoEvent>(
                emitter -> {
                    Query memoQuery = mFirestore.collection(USERS)
                            .document(mLoginUtil.getUserID())
                            .collection("memos")
                            .orderBy("datePosted", Query.Direction.DESCENDING);

                    memoQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Memo memo = Memo.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(MemoEvent.create(RealTimeDataFramework.EVENT_ADDED, memo));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(MemoEvent.create(RealTimeDataFramework.EVENT_REMOVED, memo));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(MemoEvent.create(RealTimeDataFramework.EVENT_UPDATED, memo));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<List<AnswerEvent>> retrieveAnswers(Question question) {
        return mUtils.withNetworkCheck(new MaybeCreate<List<AnswerEvent>>(
                emitter -> {
                    Query answerQuery = mFirestore.collection(ANSWERS)
                            .whereEqualTo("questionUid", question.uid())
                            .orderBy("dateSent", Query.Direction.ASCENDING);

                    answerQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentSnapshot> documentSnapshotIterator = documentSnapshots.getDocuments().iterator();
                            List<AnswerEvent> answerEvents = new ArrayList<>();
                            while (documentSnapshotIterator.hasNext()) {
                                DocumentSnapshot documentSnapshot = documentSnapshotIterator.next();
                                Answer answer = Answer.createFromSnapshot(documentSnapshot);
                                answerEvents.add(AnswerEvent.create(RealTimeDataFramework.EVENT_ADDED, answer));
                            }

                            Collections.sort(answerEvents, new Answer.Comparator());
                            emitter.onSuccess(answerEvents);
                            emitter.onComplete();
                        }
                    });
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable addAnswer(Question questionToRepondTo, Answer answer) {
        return new CompletableCreate(
                emitter -> mFirestore
                        .collection(USERS)
                        .document(mLoginUtil.getUserID())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            DocumentReference answerRef = mFirestore
                                    .collection(ANSWERS)
                                    .document();

                            Answer answerWithUid = Answer.create(
                                    answerRef.getId(),
                                    answer.audioRecordingUrl(),
                                    answer.questionDescription(),
                                    answer.fileAttachments() != null && answer.fileAttachments().size() > 0 ? answer.fileAttachments() : null,
                                    mLoginUtil.getPhoneNumber(),
                                    (String) documentSnapshot.get("fullName"),
                                    (String) documentSnapshot.get("role"),
                                    new Date(),
                                    questionToRepondTo.uid(),
                                    answer.answerFor());

                            answerRef.set(answerWithUid.toMap())
                                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        })
                        .addOnFailureListener(emitter::onError))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference userRef = mFirestore.collection(USERS)
                            .document(questionToRepondTo.askerUid());

                    userRef.get()
                            .addOnSuccessListener(userSnapshot -> {
                                long answersReceived = 0L;
                                if (userSnapshot.get("answersReceived") != null) {
                                    answersReceived = (long) userSnapshot.get("answersReceived");
                                }
                                userRef.update("answersReceived", answersReceived + 1)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference lawyerRef = mFirestore
                            .collection(USERS)
                            .document(questionToRepondTo.assignedLawyerUid());

                    lawyerRef.get()
                            .addOnSuccessListener(lawyerSnapshot -> {
                                WriteBatch batch = mFirestore.batch();

                                long answersSent = 0L;
                                if (lawyerSnapshot.get("answersSent") != null) {
                                    answersSent = (long) lawyerSnapshot.get("answersSent");
                                }
                                batch.update(lawyerRef, "answersSent", answersSent + 1);

                                if (questionToRepondTo.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
                                    long currentCoins = 0L;
                                    if (lawyerSnapshot.get("coins") != null) {
                                        currentCoins = (long) lawyerSnapshot.get("coins");
                                    }
                                    long newCoins = currentCoins + questionToRepondTo.serviceFee();
                                    batch.update(lawyerRef, "coins", newCoins);
                                }

                                batch.commit()
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<LawyerFileEvent> retrieveLawyerFiles() {
        return mUtils.withNetworkCheck(new FlowableCreate<LawyerFileEvent>(
                emitter -> {
                    Query lawyerFileQuery = mFirestore.collection(FILES);

                    lawyerFileQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                LawyerFile lawyerFile = LawyerFile.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(LawyerFileEvent.create(RealTimeDataFramework.EVENT_ADDED, lawyerFile));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(LawyerFileEvent.create(RealTimeDataFramework.EVENT_REMOVED, lawyerFile));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(LawyerFileEvent.create(RealTimeDataFramework.EVENT_UPDATED, lawyerFile));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable updateQuestionStatus(Question question, String status) {
        return mUtils.withNetworkCheck(
                firestoreTransaction(transaction -> {
                    DocumentReference userRef = mFirestore
                            .collection(QUESTIONS)
                            .document(question.uid());
                    transaction.update(userRef, "status", status);
                    return status;
                }))
                .toCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable updateQuestionFeedback(Question question, String feedback) {
        return mUtils.withNetworkCheck(
                firestoreTransaction(transaction -> {
                    DocumentReference userRef = mFirestore
                            .collection(QUESTIONS)
                            .document(question.uid());
                    transaction.update(userRef, "answerFeedback", feedback);
                    return feedback;
                }))
                .toCompletable()
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference userRef = mFirestore.collection(USERS)
                            .document(question.askerUid());

                    userRef.get()
                            .addOnSuccessListener(userSnapshot -> {
                                long answersRated = 0L;
                                if (userSnapshot.get("answersRated") != null) {
                                    answersRated = (long) userSnapshot.get("answersRated");
                                }
                                userRef.update("answersRated", answersRated + 1)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .andThen(new CompletableCreate(emitter -> {
                    DocumentReference userRef = mFirestore.collection(USERS)
                            .document(question.assignedLawyerUid());

                    userRef.get()
                            .addOnSuccessListener(userSnapshot -> {
                                Map<String, Boolean> likes = new HashMap<>();
                                if (userSnapshot.get("likes") != null) {
                                    likes = (Map<String, Boolean>) userSnapshot.get("likes");
                                }
                                if (feedback.equals(Question.Feedback.GOOD)) {
                                    likes.put(mLoginUtil.getUserID(), true);
                                    userRef.update("likes", likes)
                                            .addOnSuccessListener(aVoid -> emitter.onComplete())
                                            .addOnFailureListener(emitter::onError);
                                } else {
                                    emitter.onComplete();
                                }
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<MemoEvent> retrieveMemosOfLawyer(LawyerUser lawyerUser) {
        return mUtils.withNetworkCheck(new FlowableCreate<MemoEvent>(
                emitter -> {
                    Query memoQuery = mFirestore.collection(USERS)
                            .document(lawyerUser.uid())
                            .collection("memos")
                            .orderBy("datePosted", Query.Direction.DESCENDING);

                    memoQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Memo memo = Memo.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(MemoEvent.create(RealTimeDataFramework.EVENT_ADDED, memo));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(MemoEvent.create(RealTimeDataFramework.EVENT_REMOVED, memo));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(MemoEvent.create(RealTimeDataFramework.EVENT_UPDATED, memo));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<QuestionEvent> retrieveQuestionsReceivedOfLawyer(LawyerUser lawyerUser) {
        return mUtils.withNetworkCheck(new FlowableCreate<QuestionEvent>(
                emitter -> {
                    Query questionQuery = mFirestore.collection(QUESTIONS)
                            .whereEqualTo("assignedLawyerUid", lawyerUser.uid())
                            .orderBy("dateAdded", Query.Direction.DESCENDING);

                    questionQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Question question = Question.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_ADDED, question));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_REMOVED, question));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(QuestionEvent.create(RealTimeDataFramework.EVENT_UPDATED, question));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<LawyerUser> favoriteLawyer(LawyerUser lawyerUser, boolean isFavorited) {
        return mUtils.withNetworkCheck(
                firestoreTransaction(transaction -> {
                    DocumentReference userRef = mFirestore
                            .collection(USERS)
                            .document(lawyerUser.uid());

                    DocumentSnapshot lawyerSnapshot = transaction.get(userRef);

                    Map<String, Boolean> favoritedBy = (Map<String, Boolean>) lawyerSnapshot.get("favoritedBy");
                    if (favoritedBy == null) {
                        favoritedBy = new HashMap<>();
                    }
                    favoritedBy.put(mLoginUtil.getUserID(), isFavorited);
                    transaction.update(userRef, "favoritedBy", favoritedBy);

                    LawyerUser updatedUser = LawyerUser.createFromSnapshot(lawyerSnapshot);
                    updatedUser.favoritedBy().put(mLoginUtil.getUserID(), isFavorited);
                    return updatedUser;
                }))
                .toMaybe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<UserEvent> retrieveLawyerUsers() {
        return mUtils.withNetworkCheck(new FlowableCreate<UserEvent>(
                emitter -> {
                    Query userQuery = mFirestore.collection(USERS)
                            .whereEqualTo("role", LawyerUser.ROLE_VALUE);

                    userQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                LawyerUser user = LawyerUser.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_ADDED, user));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_REMOVED, user));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_UPDATED, user));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<UserEvent> retrieveIndividualUsers() {
        return mUtils.withNetworkCheck(new FlowableCreate<UserEvent>(
                emitter -> {
                    Query userQuery = mFirestore.collection(USERS)
                            .whereEqualTo("role", IndividualUser.ROLE_VALUE);

                    userQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                IndividualUser user = IndividualUser.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_ADDED, user));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_REMOVED, user));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_UPDATED, user));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<UserEvent> retrieveCommercialUsers() {
        return mUtils.withNetworkCheck(new FlowableCreate<UserEvent>(
                emitter -> {
                    Query userQuery = mFirestore.collection(USERS)
                            .whereEqualTo("role", CommercialUser.ROLE_VALUE);

                    userQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                CommercialUser user = CommercialUser.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_ADDED, user));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_REMOVED, user));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(UserEvent.create(RealTimeDataFramework.EVENT_UPDATED, user));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<CoinPackageEvent> retrieveCoinPackages() {
        return mUtils.withNetworkCheck(new FlowableCreate<CoinPackageEvent>(
                emitter -> {
                    Query coinPackageQuery = mFirestore.collection(COIN_PACKAGES)
                            .whereEqualTo("status", CoinPackage.Status.ACTIVE)
                            .orderBy("coinAmount");

                    coinPackageQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                CoinPackage coinPackage = CoinPackage.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        emitter.onNext(CoinPackageEvent.create(RealTimeDataFramework.EVENT_ADDED, coinPackage, "0"));
                                        break;
                                    case REMOVED:
                                        emitter.onNext(CoinPackageEvent.create(RealTimeDataFramework.EVENT_REMOVED, coinPackage, "0"));
                                        break;
                                    case MODIFIED:
                                        emitter.onNext(CoinPackageEvent.create(RealTimeDataFramework.EVENT_UPDATED, coinPackage, "0"));
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Long> addPurchasedCoins(Long coinAmount) {
        return new MaybeCreate<Long>(
                emitter -> {
                    DocumentReference userRef = mFirestore.collection(USERS)
                            .document(mLoginUtil.getUserID());

                    userRef.get()
                            .addOnSuccessListener(userSnapshot -> {
                                WriteBatch batch = mFirestore.batch();

                                long currentCoins = 0L;
                                if (userSnapshot.get("coins") != null) {
                                    currentCoins = (long) userSnapshot.get("coins");
                                }
                                long newCoins = currentCoins + coinAmount;
                                batch.update(userRef, "coins", newCoins);

                                batch.commit()
                                        .addOnSuccessListener(aVoid -> emitter.onSuccess(newCoins))
                                        .addOnFailureListener(emitter::onError);
                            })
                            .addOnFailureListener(emitter::onError);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> addPurchase(String purchaseResult, Long coinAmount) {
        return new MaybeCreate<String>(
                emitter -> {
                    Query query = mFirestore
                            .collection(PURCHASES)
                            .whereEqualTo("purchaseReceipt", purchaseResult);

                    query.get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    DocumentReference purchaseRef = mFirestore
                                            .collection(PURCHASES)
                                            .document();

                                    Map<String, Object> purchase = new HashMap<>();
                                    purchase.put("uid", purchaseRef.getId());
                                    purchase.put("dateCreated", new Date());
                                    purchase.put("purchaseReceipt", purchaseResult);
                                    purchase.put("coinAmount", coinAmount);

                                    purchaseRef.set(purchase)
                                            .addOnSuccessListener(aVoid -> emitter.onSuccess(purchaseRef.getId()))
                                            .addOnFailureListener(emitter::onError);
                                } else {
                                    DocumentSnapshot purchaseSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    if (purchaseSnapshot.exists()) {
                                        emitter.onSuccess(purchaseSnapshot.getId());
                                    } else {
                                        DocumentReference purchaseRef = mFirestore
                                                .collection(PURCHASES)
                                                .document();

                                        Map<String, Object> purchase = new HashMap<>();
                                        purchase.put("uid", purchaseRef.getId());
                                        purchase.put("dateCreated", new Date());
                                        purchase.put("purchaseReceipt", purchaseResult);

                                        purchaseRef.set(purchase)
                                                .addOnSuccessListener(aVoid -> emitter.onSuccess(purchaseRef.getId()))
                                                .addOnFailureListener(emitter::onError);
                                    }
                                }
                            })
                            .addOnFailureListener(emitter::onError);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> consumePurchase(String purchaseUid) {
        return firestoreTransaction(
                transaction -> {
                    DocumentReference purchaseRef = mFirestore
                            .collection(PURCHASES)
                            .document(purchaseUid);

                    transaction.update(purchaseRef, "isConsumed", true);
                    transaction.update(purchaseRef, "consumeDate", new Date());
                    return purchaseUid;
                })
                .toMaybe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable requestLawyerProfileEdit() {
        return Completable
                .create(emitter -> {
                    CollectionReference requests = mFirestore.collection("requests");

                    fetchUser(mLoginUtil.getUserID())
                            .subscribe(currentUser -> {
                                DocumentReference newRequest = requests.document();

                                Map<String, Object> data = new HashMap<>();
                                data.put("uid", newRequest.getId());
                                LawyerUser lawyerUser = (LawyerUser) currentUser;
                                data.put("requesterUser", lawyerUser.toMap());
                                data.put("type", "profileEdit");
                                data.put("dateCreated", new Date());

                                newRequest.set(data)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable reportUsers(List<Object> reportedUsers) {
        return fetchUser(mLoginUtil.getUserID())
                .flatMapCompletable(currentUser -> {
                    Map<String, Object> data = new HashMap<>();

                    if (currentUser instanceof LawyerUser) {
                        LawyerUser lawyerUser = (LawyerUser) currentUser;
                        data.put("requesterUser", new JSONObject(lawyerUser.toMap()));
                    } else if (currentUser instanceof CommercialUser) {
                        CommercialUser commercialUser = (CommercialUser) currentUser;
                        data.put("requesterUser", new JSONObject(commercialUser.toMap()));
                    } else {
                        IndividualUser individualUser = (IndividualUser) currentUser;
                        data.put("requesterUser", new JSONObject(individualUser.toMap()));
                    }

                    List<JSONObject> reportedUserData = new ArrayList<>();
                    for (Object user : reportedUsers) {
                        if (user instanceof LawyerUser) {
                            LawyerUser lawyerUser = (LawyerUser) user;
                            reportedUserData.add(new JSONObject(lawyerUser.toMap()));
                        } else if (user instanceof CommercialUser) {
                            CommercialUser commercialUser = (CommercialUser) user;
                            reportedUserData.add(new JSONObject(commercialUser.toMap()));
                        } else {
                            IndividualUser individualUser = (IndividualUser) user;
                            reportedUserData.add(new JSONObject(individualUser.toMap()));
                        }
                    }
                    data.put("reportedUsers", reportedUserData);

                    return mUtils.withNetworkCheck(Completable.create(
                            emitter -> mFunctions.getHttpsCallable("reportedUser")
                                    .call(data)
                                    .addOnSuccessListener(httpsCallableResult -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError)
                                    .addOnCanceledListener(emitter::onComplete)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                });
    }

    @Override
    public Maybe<Field> fetchField(String fieldUid) {
        return mUtils.withNetworkCheck(new MaybeCreate<Field>(
                emitter -> {
                    Query query = mFirestore
                            .collection(FIELDS)
                            .whereEqualTo("uid", fieldUid);

                    query.get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (querySnapshot.getDocuments() == null || querySnapshot.getDocuments().isEmpty()) {
                                    emitter.onComplete();
                                    return;
                                }

                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    emitter.onSuccess(Field.createFromSnapshot(documentSnapshot));
                                }
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<SubSubject> fetchSubSubject(String subSubjectUid) {
        return mUtils.withNetworkCheck(new MaybeCreate<SubSubject>(
                emitter -> {
                    Query query = mFirestore
                            .collection(SUBSUBJECTS)
                            .whereEqualTo("uid", subSubjectUid);

                    query.get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (querySnapshot.getDocuments() == null || querySnapshot.getDocuments().isEmpty()) {
                                    emitter.onComplete();
                                    return;
                                }

                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    emitter.onSuccess(SubSubject.createFromSnapshot(documentSnapshot));
                                }
                            })
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> Single<T> firestoreTransaction(Function<Transaction, T> transactionFunction) {
        return mUtils.withNetworkCheck(
                new SingleCreate<T>(emitter ->
                        mFirestore
                                .runTransaction(transaction -> {
                                    try {
                                        return transactionFunction.apply(transaction);
                                    } catch (Exception e) {
                                        Timber.e(CommonUtils.getExceptionString(e));
                                        return null;
                                    }
                                })
                                .addOnSuccessListener(emitter::onSuccess)
                                .addOnFailureListener(emitter::onError)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<Invoice> retrieveInvoices(boolean isLaweyer) {
        return mUtils.withNetworkCheck(new FlowableCreate<Invoice>(
                emitter -> {
                    Query userInvoicesQuery = null;
                    if (isLaweyer) {
                        userInvoicesQuery = mFirestore.collection(INVOICES)
                                .whereEqualTo("lawyerUid", mLoginUtil.getUserID())
                                .orderBy("orderDate", Query.Direction.DESCENDING);
                    } else {
                        userInvoicesQuery = mFirestore.collection(INVOICES)
                                .whereEqualTo("UserUid", mLoginUtil.getUserID())
                                .orderBy("orderDate", Query.Direction.DESCENDING);
                    }


                    userInvoicesQuery.addSnapshotListener((documentSnapshots, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                        } else {
                            Iterator<DocumentChange> documentChangeIterator = documentSnapshots.getDocumentChanges().iterator();

                            while (documentChangeIterator.hasNext()) {
                                DocumentChange documentChange = documentChangeIterator.next();
                                Invoice invoice = Invoice.createFromSnapshot(documentChange.getDocument());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        if (isLaweyer) {
                                            if (invoice.paid().equals("true")) {
                                                emitter.onNext(Invoice.create(invoice.uid(), invoice.orderVatPrice(), invoice.orderVat(),
                                                        invoice.orderTypePrice(), invoice.orderType(), invoice.orderSubTotal(),
                                                        invoice.orderRequestNumber(), invoice.orderDate(), invoice.collection(),
                                                        invoice.UserUid(), invoice.LawyerUid(), invoice.paid()));
                                            }
                                        } else {
                                            emitter.onNext(Invoice.create(invoice.uid(), invoice.orderVatPrice(), invoice.orderVat(),
                                                    invoice.orderTypePrice(), invoice.orderType(), invoice.orderSubTotal(),
                                                    invoice.orderRequestNumber(), invoice.orderDate(), invoice.collection(),
                                                    invoice.UserUid(), invoice.LawyerUid(), invoice.paid()));
                                        }

                                        break;
                                    case REMOVED:
                                        if (isLaweyer) {
                                            if (invoice.paid().equals("true")) {
                                                emitter.onNext(Invoice.create(invoice.uid(), invoice.orderVatPrice(), invoice.orderVat(),
                                                        invoice.orderTypePrice(), invoice.orderType(), invoice.orderSubTotal(),
                                                        invoice.orderRequestNumber(), invoice.orderDate(), invoice.collection(),
                                                        invoice.UserUid(), invoice.LawyerUid(), invoice.paid()));
                                            }
                                        } else {
                                            emitter.onNext(Invoice.create(invoice.uid(), invoice.orderVatPrice(), invoice.orderVat(),
                                                    invoice.orderTypePrice(), invoice.orderType(), invoice.orderSubTotal(),
                                                    invoice.orderRequestNumber(), invoice.orderDate(), invoice.collection(),
                                                    invoice.UserUid(), invoice.LawyerUid(), invoice.paid()));
                                        }
                                        break;
                                    case MODIFIED:
                                        if (isLaweyer) {
                                            if (invoice.paid().equals("true")) {
                                                emitter.onNext(Invoice.create(invoice.uid(), invoice.orderVatPrice(), invoice.orderVat(),
                                                        invoice.orderTypePrice(), invoice.orderType(), invoice.orderSubTotal(),
                                                        invoice.orderRequestNumber(), invoice.orderDate(), invoice.collection(),
                                                        invoice.UserUid(), invoice.LawyerUid(), invoice.paid()));
                                            }
                                        } else {
                                            emitter.onNext(Invoice.create(invoice.uid(), invoice.orderVatPrice(), invoice.orderVat(),
                                                    invoice.orderTypePrice(), invoice.orderType(), invoice.orderSubTotal(),
                                                    invoice.orderRequestNumber(), invoice.orderDate(), invoice.collection(),
                                                    invoice.UserUid(), invoice.LawyerUid(), invoice.paid()));
                                        }
                                        break;
                                }
                            }
                        }
                    });
                }, BackpressureStrategy.BUFFER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable addInvoice(Invoice invoice) {
        return mUtils.withNetworkCheck(Completable
                .create(emitter -> {

                    DocumentReference invoiceRef = mFirestore.collection(INVOICES).document();
                    Invoice invoice_ = Invoice.builder()
                            .UserUid(mLoginUtil.getUserID())
                            .collection("invoices")
                            .LawyerUid(invoice.LawyerUid())
                            .orderDate(new Date())
                            .orderRequestNumber(invoice.orderRequestNumber())
                            .orderSubTotal(String.valueOf(invoice.orderSubTotal()))
                            .orderType(invoice.orderType())
                            .orderTypePrice(String.valueOf(invoice.orderTypePrice()))
                            .orderVat("0.0%")
                            .orderVatPrice("0")
                            .uid(invoiceRef.getId())
                            .paid(invoice.paid())
                            .build();
                    invoiceRef.set(invoice_.toMap())
                            .addOnSuccessListener(onSuccess -> emitter.onComplete())
                            .addOnFailureListener(emitter::onError);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
