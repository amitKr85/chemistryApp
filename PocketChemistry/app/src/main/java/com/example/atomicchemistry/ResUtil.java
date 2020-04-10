package com.example.atomicchemistry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ResUtil {
    public static String LOCALE_CODE_ENGLISH = "en";
    public static String LOCALE_CODE_HINDI = "hi";

    public static final int MAX_NUMBER_ICON_AVAILABLE = 12;

    public static void setLocale(Context context){
        SharedPreferences pref = FileUtil.getSharedPreferences(context);
        String localeCode = pref.getString(MainActivity.LANG_KEY, MainActivity.LANG_EN);
        setLocale(context, localeCode);
    }
    public static void setLocale(Context context, String localeCode){
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));

        res.updateConfiguration(config, metrics);
    }
    public static String getSelectedLanguageCode(Context context){
        return FileUtil.getSharedPreferences(context).getString(MainActivity.LANG_KEY, MainActivity.LANG_EN);
    }

    public static int getNumberImageResID(Context context,int value){
        if(value<1) {
            return context.getResources().getIdentifier("icon_1", "drawable", context.getPackageName());
        }else if(value>MAX_NUMBER_ICON_AVAILABLE){
            return context.getResources().getIdentifier("icon_"+MAX_NUMBER_ICON_AVAILABLE,"drawable", context.getPackageName());
        }else{
            return context.getResources().getIdentifier("icon_"+value,"drawable", context.getPackageName());
        }

    }

    public static List<String> getStandardTopics(Context context, int stdVal){
        int resId = getStandardTopicArrayId(context, stdVal);
        if(resId<=0){
            Log.e("getStdTopicArray", "invalid stdVal:"+stdVal);
            return null;
        }
        return Arrays.asList(context.getResources().getStringArray(resId));
    }

    public static int getStandardTopicArrayId(Context context, int stdVal){
        if(stdVal<9 || stdVal>12){
            Log.e("getStdTopicArrayId", "invalid stdVal:"+stdVal);
            return -1;
        }
        return context.getResources().getIdentifier(MainActivity.STD_STRINGS[stdVal-9]+"_topics","array",context.getPackageName());
    }

    public static String getStandardString(Context context, int stdVal){
        int resId = getStandardStringId(context, stdVal);
        if(resId<=0){
            Log.e("getStdString", "invalid stdVal:"+stdVal);
            return "";
        }
        return context.getResources().getString(resId);
    }

    public static int getStandardStringId(Context context,int stdVal){
        if(stdVal<9 || stdVal>12){
            Log.e("getStdStringId", "invalid stdVal:"+stdVal);
            return -1;
        }
        return context.getResources().getIdentifier(MainActivity.STD_STRINGS[stdVal-9]+"_standard","string",context.getPackageName());
    }

    public static String getString(Context context,int stringId){
        return context.getResources().getString(stringId);
    }
}
