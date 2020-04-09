package com.example.atomicchemistry;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class DownloadOpenItem implements Serializable {
    private String mSuperTitle,mTitle, mUrl, mButtonText;
    private int mIconResId;
    private int openButtonStatus = STATUS_UNKNOWN;

    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_DOWNLOAD = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_UNAVAILABLE = 2;
    public static final int STATUS_OPEN = 3;



    DownloadOpenItem(String superTitle, String title, String url, int iconResId, String buttonText){
        mSuperTitle = superTitle;
        mTitle = title;
        mUrl = url;
        mIconResId = iconResId;
        mButtonText = buttonText;
    }

    public String getSuperTitle() {
        return mSuperTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getButtonText() {
        return mButtonText;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public int getOpenButtonStatus() {
        return openButtonStatus;
    }

    public void setOpenButtonStatus(int status){
        openButtonStatus = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "superTitle:"+mSuperTitle+", title:"+mTitle+", url:"+mUrl+", button:"+mButtonText+", icon:"+mIconResId;
    }
}
