package com.example.atomicchemistry;

import java.util.List;

public class ChapterItem {
//    int mStdVal;
    String mTitle, mUrl;
//    int mIconResId;
    List<String> mSubTopics;
    boolean mSubTopicsCollapsed = true;

    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_DOWNLOAD = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_UNAVAILABLE = 2;
    public static final int STATUS_OPEN = 3;

    int openButtonStatus = STATUS_UNKNOWN;

//    ChapterItem(int stdVal,String title, String url, List<String> subTopics){
//        mStdVal = stdVal;
    ChapterItem(String title, String url, List<String> subTopics){
        mTitle = title;
        mUrl = url;
//        mIconResId = iconResId;
        mSubTopics = subTopics;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

//    public int getIconResId() {
//        return mIconResId;
//    }


    public List<String> getSubTopics() {
        return mSubTopics;
    }

    public void setSubTopicsCollapsed(boolean collapsed){
        mSubTopicsCollapsed = collapsed;
    }

    public boolean isSubTopicsCollapsed() {
        return mSubTopicsCollapsed;
    }
}
