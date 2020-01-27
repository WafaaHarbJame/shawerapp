package com.shawerapp.android.model;

public class HireableLawyer {

    public static final HireableLawyer DUMMY1 = new HireableLawyer(LawyerProfile.DUMMY1, 100);

    public static final HireableLawyer DUMMY2 = new HireableLawyer(LawyerProfile.DUMMY2, 400);

    public static final HireableLawyer DUMMY3 = new HireableLawyer(LawyerProfile.DUMMY3, 150);

    private LawyerProfile lawyerProfile;

    private int lawyerFee;

    public HireableLawyer(LawyerProfile lawyerProfile, int lawyerFee) {
        this.lawyerProfile = lawyerProfile;
        this.lawyerFee = lawyerFee;
    }

    public int getLawyerFee() {
        return lawyerFee;
    }

    public String getAvatar() {
        return lawyerProfile.getAvatar();
    }

    public String getUserName() {
        return lawyerProfile.getUserName();
    }

    public String getFullName() {
        return lawyerProfile.getFullName();
    }

    public int getNumOfLikes() {
        return lawyerProfile.getNumOfLikes();
    }

    public String getNumOfExperience() {
        return lawyerProfile.getNumOfExperience();
    }
}
