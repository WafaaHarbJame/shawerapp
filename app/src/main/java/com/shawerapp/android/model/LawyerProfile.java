package com.shawerapp.android.model;

import com.shawerapp.android.autovalue.Memo;

import java.util.ArrayList;
import java.util.List;

public final class LawyerProfile {

  public static LawyerProfile DUMMY1 = new LawyerProfile("jdlacruz"
      , "Juan Dela Cruz"
      , ""
      , "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante."
      , 200
      , 50
      , 100
      , "3-5 years"
      , 1000,
      1, PendingTask.getDummy());

  public static LawyerProfile DUMMY2 = new LawyerProfile("jdlacruz"
      , "John Doe"
      , ""
      , "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante."
      , 10
      , 120
      , 500
      , "10-15 years"
      , 5000,
      0, PendingTask.getDummy());

  public static LawyerProfile DUMMY3 = new LawyerProfile("jdlacruz"
      , "John Doe 2"
      , ""
      , "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante."
      , 200
      , 10
      , 100
      , "1-3 years"
      , 1000,
      1, PendingTask.getDummy());

  private final String userName;
  private final String fullName;
  private final String avatar;
  private final String description;
  private final int numOfQuestions;
  private final int numOfAnswers;
  private final int numOfLikes;
  private final String numOfExperience;
  private final int coinBalance;
  private final int onlineStatus;

  private final List<PendingTask> pendingTasks;
  private final List<Memo> memoList = new ArrayList<>();

  public LawyerProfile(String userName,
      String fullName,
      String avatar, String description,
      int numOfQuestions,
      int numOfAnswers,
      int numOfLikes,
      String numOfExperience,
      int coinBalance,
      int onlineStatus,
      List<PendingTask> pendingTasks) {
    this.userName = userName;
    this.fullName = fullName;
    this.avatar = avatar;
    this.description = description;
    this.numOfQuestions = numOfQuestions;
    this.numOfAnswers = numOfAnswers;
    this.numOfLikes = numOfLikes;
    this.numOfExperience = numOfExperience;
    this.coinBalance = coinBalance;
    this.onlineStatus = onlineStatus;
    this.pendingTasks = pendingTasks;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getUserName() {
    return userName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getDescription() {
    return description;
  }

  public int getNumOfQuestions() {
    return numOfQuestions;
  }

  public int getNumOfAnswers() {
    return numOfAnswers;
  }

  public int getNumOfLikes() {
    return numOfLikes;
  }

  public String getNumOfExperience() {
    return numOfExperience;
  }

  public int getCoinBalance() {
    return coinBalance;
  }

  public List<PendingTask> getPendingTasks() {
    return pendingTasks;
  }

  public List<Memo> getMemoList() {
    return memoList;
  }

  public int getOnlineStatus() {
    return onlineStatus;
  }
}
