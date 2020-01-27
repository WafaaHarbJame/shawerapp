package com.shawerapp.android.model;

import com.shawerapp.android.adapter.item.InboundAttachmentItem;
import com.shawerapp.android.adapter.item.InboundMessageItem;
import com.shawerapp.android.adapter.item.InboundVoiceItem;
import com.shawerapp.android.adapter.item.OutboundAttachmentItem;
import com.shawerapp.android.adapter.item.OutboundMessageItem;
import com.shawerapp.android.adapter.item.OutboundVoiceItem;
import com.shawerapp.android.adapter.item.SystemChatMessageItem;
import com.shawerapp.android.adapter.item.SystemChatTimeStampItem;
import com.shawerapp.android.adapter.item.SystemRateLawyerItem;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import java.util.ArrayList;
import java.util.List;

public class DebugTools {

  public static boolean isLawyer = true;

  private static boolean stateRequest;
  private static int stateQuestion;

  public static List<AbstractFlexibleItem> requestCase() {
    if (stateRequest) {
      stateRequest = false;
      return creatFulfilledRequestCase();
    } else {
      stateRequest = true;
      return creatUnfulfilledRequestCase();
    }
  }

  public static List<AbstractFlexibleItem> questionCase() {
    switch (stateQuestion) {
      case 0:
      default:
        stateQuestion++;
        return createUnAnsweredQuestionCase();
      case 1:
        stateQuestion++;
        return createRequireFeedbackQuestionCase();
      case 2:
        stateQuestion++;
        return createRequireDetailQuestionCase();
      case 3:
        stateQuestion++;
        return createReady2RateQuestionCase();
      case 4:
        stateQuestion = 0;
        return createRatedQuestionCase();
    }
  }

  public static List<AbstractFlexibleItem> creatFulfilledRequestCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The request requested on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forRequest(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forRequest("", "0:59"));
    items.add(OutboundAttachmentItem.forRequest("", "Sample.txt - 56kb"));
    items.add(new SystemChatTimeStampItem("    The respond replied on dd/mm/yyyy , mm:ss"));
    items.add(InboundMessageItem.forRequest(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(new SystemChatMessageItem(
        "Your practice request has been reviewed and approved\nnow. Just contact the lawyer"));

    return items;
  }

  public static List<AbstractFlexibleItem> creatUnfulfilledRequestCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The request requested on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forRequest(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forRequest("", "0:59"));
    items.add(OutboundAttachmentItem.forRequest("", "Sample.txt - 56kb"));
    items.add(new SystemChatTimeStampItem("    The respond replied on dd/mm/yyyy , mm:ss"));
    items.add(InboundMessageItem.forRequest(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(new SystemChatMessageItem(
        "Your practice request is still under review\n or awaiting approval from a lawyer\n kindly wait a bit more"));

    return items;
  }

  public static List<AbstractFlexibleItem> createUnAnsweredQuestionCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The Question asked on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forQuestion(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forQuestion("", "0:59"));
    items.add(OutboundAttachmentItem.forQuestion("", "Sample.txt - 56kb"));
    items.add(new SystemChatMessageItem(
        "{Lawyer Name} still did not answer your question\n Kindly wait a bit more"));

    return items;
  }

  public static List<AbstractFlexibleItem> createRequireFeedbackQuestionCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The Question asked on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forQuestion(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forQuestion("", "0:59"));
    items.add(OutboundAttachmentItem.forQuestion("", "Sample.txt - 56kb"));
    items.add(new SystemChatTimeStampItem("    The Answer answered on dd/mm/yyyy , mm:ss"));
    items.add(InboundMessageItem.forAnswer(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(InboundVoiceItem.forAnswer("", "0:59"));
    items.add(InboundAttachmentItem.forAnswer("", "Sample.txt - 56kb"));
    items.add(new SystemChatMessageItem(
        "{Lawyer Name} answered your question\n And requires your feedback on the answer"));

    return items;
  }

  public static List<AbstractFlexibleItem> createRequireDetailQuestionCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The Question asked on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forQuestion(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forQuestion("", "0:59"));
    items.add(OutboundAttachmentItem.forQuestion("", "Sample.txt - 56kb"));
    items.add(new SystemChatTimeStampItem("    The Answer answered on dd/mm/yyyy , mm:ss"));
    items.add(InboundMessageItem.forAnswer(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(InboundVoiceItem.forAnswer("", "0:59"));
    items.add(new SystemChatMessageItem(
        "{Lawyer Name} could not answer your question accurately\n And need more details to do so"));

    return items;
  }

  public static List<AbstractFlexibleItem> createReady2RateQuestionCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The Question asked on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forQuestion(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forQuestion("", "0:59"));
    items.add(OutboundAttachmentItem.forQuestion("", "Sample.txt - 56kb"));
    items.add(new SystemChatTimeStampItem("    The Answer answered on dd/mm/yyyy , mm:ss"));
    items.add(InboundMessageItem.forAnswer(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(InboundVoiceItem.forAnswer("", "0:59"));
    items.add(InboundAttachmentItem.forAnswer("", "Sample.txt - 56kb"));
    items.add(new SystemChatMessageItem(
        "{Lawyer Name} did answer your question\n and closes your case. You may now rate the answer"));
    items.add(new SystemRateLawyerItem());
    return items;
  }

  public static List<AbstractFlexibleItem> createRatedQuestionCase() {
    List<AbstractFlexibleItem> items = new ArrayList<>();
    items.add(new SystemChatTimeStampItem("    The Question asked on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forQuestion(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forQuestion("", "0:59"));
    items.add(OutboundAttachmentItem.forQuestion("", "Sample.txt - 56kb"));
    items.add(new SystemChatTimeStampItem("    The Answer answered on dd/mm/yyyy , mm:ss"));
    items.add(InboundMessageItem.forAnswer(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(InboundVoiceItem.forAnswer("", "0:59"));
    items.add(new SystemChatTimeStampItem("    The Extra Details. Sent on dd/mm/yyyy , mm:ss"));
    items.add(OutboundMessageItem.forQuestion(
        "Lorem ipsum dolor sit amet, aliquet hendrerit deserunt fringilla semper ratione. Mi mauris. Quam et morbi sodales id cum nulla, praesent nulla donec aptent sed, enim consectetuer sollicitudin tristique quis sed parturient, ullamcorper tristique. Et amet lobortis gravida est et, sit suspendisse rutrum dignissim, nec integer ac hendrerit viverra, bibendum semper, duis ipsum. Donec orci maecenas orci, phasellus non, magna enim quam vitae adipiscing, pulvinar vehicula mus nostra sollicitudin, tempor aliquam consequat ut. Magna curabitur aliquam eget dolor, duis vestibulum dui vehicula. Wisi mollis sit eros non. Nec justo leo pede enim elit, accumsan inceptos nunc consectetuer nunc, nulla nibh maecenas mauris egestas, quam nonummy euismod integer posuere ut, tempor quo tempus bibendum lacinia sagittis massa. Porttitor aliqua, eu praesent lorem aliquam sollicitudin, ullamcorper lorem arcu ornare, lorem ante"));
    items.add(OutboundVoiceItem.forQuestion("", "0:59"));
    items.add(new SystemChatTimeStampItem("    The Answer answered on dd/mm/yyyy , mm:ss"));
    items.add(new SystemChatMessageItem(
        "{Lawyer Name} did answer your question\n and closes your case. You may now rate the answer"));
    items.add(InboundVoiceItem.forAnswer("", "0:59"));
    items.add(InboundAttachmentItem.forAnswer("", "Sample.txt - 56kb"));
    items.add(new SystemChatMessageItem("Case Closed"));
    items.add(new SystemRateLawyerItem(true, false));
    return items;
  }
}
