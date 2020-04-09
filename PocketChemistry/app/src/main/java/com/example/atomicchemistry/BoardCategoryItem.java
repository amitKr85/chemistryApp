package com.example.atomicchemistry;

import java.util.HashSet;

public class BoardCategoryItem {
    private int mStdValue;
    private HashSet<String> mBoardSet;

    BoardCategoryItem(int stdValue){
        mStdValue = stdValue;
        mBoardSet = new HashSet<>();
    }

    public int getStdValue(){
        return mStdValue;
    }

    public HashSet<String> getBoardSet(){
        return mBoardSet;
    }

    public void addBoard(String board){
        mBoardSet.add(board);
    }
}
