package com.example.atomicchemistry;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

public class FileUtil {
    public static File getFileAtExternalFilesDir(Context context,String filePath){
        return new File(context.getExternalFilesDir(null).getAbsolutePath(), filePath);
    }

    public static void downloadFileAtDestination(Context context, DownloadManager.Request request, String workingDir, DownloadOpenItem item){
        String filePath=new File(workingDir, item.getTitle()+".pdf").getAbsolutePath();
        request.setDestinationInExternalFilesDir(context,null, filePath);
//        request.setDestinationInExternalPublicDir(cwd.getAbsolutePath(), item.getTitle() + ".pdf");

    }

    public static SharedPreferences getSharedPreferences(Context context){
        final String PREFERENCE_FILE = ".PREFERENCE_FILE";
        return context.getSharedPreferences(context.getPackageName()+PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public static String getWorkingDir(Context context, int catValue, int stdValue){
        if(catValue<0 || catValue>3 || stdValue<9 || stdValue>12){
            Log.e("getWorkingDir","invalid arguments catValue:"+catValue+", stdValue:"+stdValue);
            return "";
        }
        String langCode = ResUtil.getSelectedLanguageCode(context);
        return MainActivity.CAT_STRINGS[catValue]
                +File.separator + stdValue
                +File.separator + langCode;
    }

    public  static String getWorkingDirOfSamplePapersForBoard(Context context, int stdValue, String board){
        if(stdValue<9 || stdValue>12){
            Log.e("getWorkingDirOfSample","invalid arguments stdValue:"+stdValue+", board:"+board);
            return "";
        }
        String langCode = ResUtil.getSelectedLanguageCode(context);
        return MainActivity.CAT_STRINGS[MainActivity.CAT_SAMPLE_PAPERS]
                +File.separator + stdValue
                +File.separator + board
                +File.separator + langCode;
    }
}
