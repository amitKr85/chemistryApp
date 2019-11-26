package com.example.pocketchemistry;

import java.util.List;

public class ChapterItem {
    int mStdVal;
    String mTitle, mUrl;
//    int mIconResId;
    List<String> mSubTopics;
    boolean mSubTopicsCollapsed = true;

    ChapterItem(int stdVal,String title, String url, List<String> subTopics){
        mStdVal = stdVal;
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

    public int getStdVal(){
        return mStdVal;
    }
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
