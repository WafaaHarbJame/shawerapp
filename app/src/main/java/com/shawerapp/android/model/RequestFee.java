package com.shawerapp.android.model;

public class RequestFee {

  public static RequestFee DUMMY1 = new RequestFee("Sub-Subject 1",
      "Description1Description1Description1Description1Description1Description1", 200);
  public static RequestFee DUMMY2 = new RequestFee("Sub-Subject 2",
      "Description2Description2Description2Description2Description2Description2", 100);
  public static RequestFee DUMMY3 = new RequestFee("Sub-Subject 3",
      "Description3Description3Description3Description3Description3Description3", 150);

  private final String name;
  private final String description;
  private final int fee;

  public RequestFee(String name, String description, int fee) {
    this.name = name;
    this.description = description;
    this.fee = fee;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getFee() {
    return fee;
  }
}

