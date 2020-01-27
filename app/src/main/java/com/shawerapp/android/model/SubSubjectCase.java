package com.shawerapp.android.model;

public class SubSubjectCase {

  public static final SubSubjectCase DUMMY1 =
      new SubSubjectCase("Sub-Subject 1", "Lawyer 1", "100", 1);
  public static final SubSubjectCase DUMMY2 =
      new SubSubjectCase("Sub-Subject 2", "Lawyer 2", "100", 2);
  public static final SubSubjectCase DUMMY3 =
      new SubSubjectCase("Sub-Subject 3", "Lawyer 3", "100", 3);

  private final String subSubjectName;
  private final String lawyerName;
  private final String cost;
  private final int status;

  public SubSubjectCase(String subSubjectName, String lawyerName, String cost, int status) {
    this.subSubjectName = subSubjectName;
    this.lawyerName = lawyerName;
    this.cost = cost;
    this.status = status;
  }

  public String getSubSubjectName() {
    return subSubjectName;
  }

  public String getLawyerName() {
    return lawyerName;
  }

  public String getCost() {
    return cost;
  }

  public int getStatus() {
    return status;
  }
}
