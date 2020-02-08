package com.shawerapp.android.Payment;

import com.ryanharter.auto.value.gson.GsonTypeAdapter;
import com.shawerapp.android.utils.UtcDateTypeAdapter;

import java.util.Date;

import javax.annotation.Nullable;

public class Question {
    public  String uid;
    public  String ar_subSubjectName;
    public  String subSubjectName;
    public  String subSubjectUid;
    public  String fieldUid;
    public  String ar_fieldName;
    public  String fieldName;
    public  String askerUid;
    public  String askerRole;
    public  String askerUsername;
    public  String assignedLawyerUid;
    public  String assignedLawyerName;
    public  String assignedLawyerUsername;
    public  String serviceFee;
    public  Date dateAdded;
    public  String status;
    public  String paid;
    public  String activeStatus;
    public  String transactionID;
    public  String answerFeedback;
    public String collection;
    public Date DateCreated;

    public Question(String uid, String ar_subSubjectName, String subSubjectName, String subSubjectUid, String fieldUid, String ar_fieldName, String fieldName, String askerUid, String askerRole, String askerUsername, String assignedLawyerUid, String assignedLawyerName, String assignedLawyerUsername, String serviceFee, Date dateAdded, String status, String paid, String activeStatus, String transactionID, String answerFeedback, String collection, Date dateCreated) {
        this.uid = uid;
        this.ar_subSubjectName = ar_subSubjectName;
        this.subSubjectName = subSubjectName;
        this.subSubjectUid = subSubjectUid;
        this.fieldUid = fieldUid;
        this.ar_fieldName = ar_fieldName;
        this.fieldName = fieldName;
        this.askerUid = askerUid;
        this.askerRole = askerRole;
        this.askerUsername = askerUsername;
        this.assignedLawyerUid = assignedLawyerUid;
        this.assignedLawyerName = assignedLawyerName;
        this.assignedLawyerUsername = assignedLawyerUsername;
        this.serviceFee = serviceFee;
        this.dateAdded = dateAdded;
        this.status = status;
        this.paid = paid;
        this.activeStatus = activeStatus;
        this.transactionID = transactionID;
        this.answerFeedback = answerFeedback;
        this.collection = collection;
        DateCreated = dateCreated;
    }
}
