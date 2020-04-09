package com.example.atomicchemistry;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlUtil {
    public static final String LOG_TAG = "UrlUtil";

    public static List<String> getUrlList(Context context, int catValue, int stdValue){
        List<String> list = new ArrayList<>();
        if(catValue<0 || catValue>3 || stdValue<9 || stdValue>12) {
            Log.e(LOG_TAG, "invalid arguments, category:"+catValue+", standard:"+stdValue);
            return list;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(MainActivity.CAT_STRINGS[catValue]+".txt")));
            String str;
            int lineNum = -1;
            while((str=reader.readLine())!=null){
                if(++lineNum == stdValue-9){
                    list = Arrays.asList(str.split(","));
                }
            }
            reader.close();
        }catch(Exception e){
            Log.e(LOG_TAG, "error opening assets "+e);
        }
        return list;
    }

    public static String getUrlStringForFiles(int stdValue, String board){
        return "http://dummix.cf/chemistry/sample_papers/get_files.php?std="+stdValue+"&board="+board;
    }

    public static String getUrlStringForBoards(){
        return "http://dummix.cf/chemistry/sample_papers/get_boards.php";
    }
}
