package com.example.krich.unifinder;

/**
 * Created by krich on 07-Oct-17.
 */

class User {
    private String mFirstName;
    private String mMidName;
    private String mLastName;
    private String mEmail;

    public User(){

    }

    public String getFirstName() {
        return mFirstName;
    }

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

    @Override
    public String toString(){
        return this.mFirstName + " " + this.mMidName + " " + this.mLastName + " " + this.mEmail;
    }
}
