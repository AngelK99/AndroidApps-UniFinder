package com.example.krich.unifinder.models;

/**
 * Created by krich on 10-Oct-17.
 */

public class ChatMessage {
    private String mText;
    private String mSender;

    public ChatMessage(){}

    public ChatMessage(String text, String sender){
        this.mText = text;
        this.mSender = sender;

    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String mSender) {
        this.mSender = mSender;
    }

    @Override
    public String toString()
    {
        return this.getText() + " " + this.getSender();
    }
}
