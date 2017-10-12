package com.example.krich.unifinder.models;

/**
 * Created by krich on 07-Oct-17.
 */

public class User {
    private String mFirstName;
    private String mMidName;
    private String mLastName;
    private String mEmail;
    private Boolean mHasLogged;
    private String mTelNum;
    private Boolean mTelHidden;
    private String mSex;

    public User(){

    }

    public User(String FirstName, String MidName, String LastName,
                String TelNum, Boolean IsHidden, String Sex){
        mFirstName = FirstName;
        mMidName = MidName;
        mLastName = LastName;
        mHasLogged = true;
        mTelNum = TelNum;
        mTelHidden = IsHidden;
        mSex = Sex;
    }

    public User(String Email){
        mEmail = Email;
        mHasLogged = false;
    }

    public String getFirstName() { return mFirstName; }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getMidName() {
        return mMidName;
    }

    public void setMidName(String mMidName) {
        this.mMidName = mMidName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public Boolean getHasLogged() { return mHasLogged; }

    public void setHasLogged(Boolean mHasLogged) { this.mHasLogged = mHasLogged; }

    public String getTelNum() { return mTelNum; }

    public void setTelNum(String mTelNum) { this.mTelNum = mTelNum; }

    public Boolean getTelHidden() { return mTelHidden; }

    public void setTelHidden(Boolean mTelHidden) { this.mTelHidden = mTelHidden; }

    public String getSex() { return mSex; }

    public void setSex(String mSex) { this.mSex = mSex; }
}
