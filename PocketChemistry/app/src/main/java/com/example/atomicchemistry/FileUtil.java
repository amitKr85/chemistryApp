package com.example.atomicchemistry;

import android.app.DownloadManager;
import android.content.Context;

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
}
