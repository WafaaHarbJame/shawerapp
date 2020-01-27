package com.shawerapp.android.model;

import java.util.ArrayList;
import java.util.List;

public final class PendingTask {

  public static List<PendingTask> getDummy() {
    List<PendingTask> tasks = new ArrayList<>(2);
    tasks.add(new PendingTask("Field 1", "Sub subject 1", 100));
    tasks.add(new PendingTask("Field 2", "Sub subject 2", 1000));
    return tasks;
  }

  private final String fieldName;
  private final String subSubjectName;
  private final int paymentValue;

  public PendingTask(String fieldName, String subSubjectName, int paymentValue) {
    this.fieldName = fieldName;
    this.subSubjectName = subSubjectName;
    this.paymentValue = paymentValue;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getSubSubjectName() {
    return subSubjectName;
  }

  public int getPaymentValue() {
    return paymentValue;
  }
}
