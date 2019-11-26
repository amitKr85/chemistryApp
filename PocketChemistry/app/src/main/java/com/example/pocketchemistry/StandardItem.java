package com.example.pocketchemistry;

import java.util.ArrayList;
import java.util.List;

public class StandardItem {
    private String mTitle;
    private List<String> mTopics;
    private int mIconResId, mStdValue;
    private boolean mTopicsCollapsed = true;

    StandardItem(String title, List<String> topics, int iconResId, int stdValue){
        mTitle = title;
        mTopics = topics;
        mIconResId = iconResId;
        mStdValue = stdValue;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<String> getTopics() {
        return mTopics;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public int getStdValue() {
        return mStdValue;
    }

    public void setTopicsCollapsed(boolean collapsed){
        mTopicsCollapsed = collapsed;
    }

    public boolean isTopicsCollapsed() {
        return mTopicsCollapsed;
    }
}
