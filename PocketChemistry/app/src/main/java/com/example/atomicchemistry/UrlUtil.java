package com.example.atomicchemistry;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlUtil {
    public static final String LOG_TAG = "UrlUtil";

    public static List<String> getUrlList(Context context, int catValue, int stdValue){
//        ArrayList<String> list = new ArrayList<>();
//        if(catValue<0 || catValue>3 || stdValue<9 || stdValue>12) {
//            Log.e(LOG_TAG, "invalid arguments, category:"+catValue+", standard:"+stdValue);
//            return list;
//        }
//        SharedPreferences pref = FileUtil.getSharedPreferences(context);
        // default : en
        String langCode = ResUtil.getSelectedLanguageCode(context); //pref.getString(MainActivity.LANG_KEY, MainActivity.LANG_EN);

        return getUrlList(context, catValue, stdValue, langCode);
//        try {
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(context.getAssets().open(MainActivity.CAT_STRINGS[catValue]+"_"+stdValue+"_"+langCode+".txt")));
//            String str;
////            int lineNum = -1;
//            while((str=reader.readLine())!=null){
//                str = str.trim();
//                if(str.length()>0)
//                    list.add(str);
////                if(++lineNum == stdValue-9){
////                    list = Arrays.asList(str.split(","));
////                }
//
//            }
//            reader.close();
//        }catch(Exception e){
//            Log.e(LOG_TAG, "error opening assets "+e);
//        }
//        return list;
    }

    public static List<String> getUrlList(Context context, int catValue, int stdValue, String langCode){
        ArrayList<String> list = new ArrayList<>();
        if(catValue<0 || catValue>3 || stdValue<9 || stdValue>12 || (!langCode.equals(MainActivity.LANG_EN) && !langCode.equals(MainActivity.LANG_HI))) {
            Log.e("getUrlList", "invalid arguments, category:"+catValue+", standard:"+stdValue+", langCode:"+langCode);
            return list;
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(MainActivity.CAT_STRINGS[catValue]+"_"+stdValue+"_"+langCode+".txt")));
            String str;
//            int lineNum = -1;
            while((str=reader.readLine())!=null){
                str = str.trim();
                if(str.length()>0)
                    list.add(str);
//                if(++lineNum == stdValue-9){
//                    list = Arrays.asList(str.split(","));
//                }

            }
            reader.close();
        }catch(Exception e){
            Log.e(LOG_TAG, "error opening assets "+e);
        }
        return list;
    }

    public static String getUrlStringForFiles(Context context, int stdValue, String board){
        String langCode = ResUtil.getSelectedLanguageCode(context);
        return "http://dummix.cf/chemistry/sample_papers/get_files.php?std="+stdValue+"&board="+board+"&lang="+langCode;
    }

    public static String getUrlStringForBoards(){
        return "http://dummix.cf/chemistry/sample_papers/get_boards.php";
    }
}
