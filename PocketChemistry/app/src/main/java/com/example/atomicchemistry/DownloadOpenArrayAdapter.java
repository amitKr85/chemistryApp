package com.example.atomicchemistry;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadOpenArrayAdapter extends ArrayAdapter<DownloadOpenItem> {


    Context mContext;
    private static final String LOG_TAG = "DownloadOpenAdapter";
    private String mWorkingDir;
    public ArrayList<Pair<Integer,Long>> mDownloadIdLists;

    DownloadOpenArrayAdapter(Context context, String workingDir, List<DownloadOpenItem> list){
        super(context, 0, list);
        mContext = context;
        mWorkingDir = workingDir;
        mDownloadIdLists = new ArrayList<>();
        File cwd = FileUtil.getFileAtExternalFilesDir(mContext, mWorkingDir);
        boolean flag = cwd.mkdirs();

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getTempFile()));
            mDownloadIdLists = (ArrayList<Pair<Integer,Long>>) ois.readObject();
            ois.close();
        }catch (Exception e){
            Log.e(LOG_TAG, e.toString());
        }

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.download_open_item, null);
        }

        final ImageView imageView = convertView.findViewById(R.id.file_image_view);
        final TextView fileSuperTitleView = convertView.findViewById(R.id.file_super_title_text_view);
        final TextView fileTitleTextView = convertView.findViewById(R.id.file_title_text_view);
        final Button downloadButton = convertView.findViewById(R.id.download_file_button);
        final DownloadOpenItem item = getItem(position);

//        int iconResId = mContext.getResources().getIdentifier("icon_"+(position+1), "drawable", mContext.getPackageName());
        int iconResId = item.getIconResId();
        imageView.setImageResource(iconResId);
//        int chapterResId = mContext.getResources().getIdentifier("chapter_"+(position+1),"string", mContext.getPackageName());
//        chapterTextView.setText(mContext.getResources().getString(chapterResId));
//        chapterTopicTextView.setText(item.getTitle());
        fileSuperTitleView.setText(item.getSuperTitle());
        fileTitleTextView.setText(item.getTitle());

        // setting openButton Strings
        final String openString = mContext.getString(R.string.open)+" "+item.getButtonText();
        final String openingString = mContext.getString(R.string.opening)+" "+item.getButtonText();
        final String downloadString = mContext.getString(R.string.download)+" "+item.getButtonText();
        final String downloadingString = mContext.getString(R.string.downloading)+" "+item.getButtonText();
        final String unavailableString = item.getButtonText()+" "+mContext.getString(R.string.unavailable);


//        File file = getFile(mStdValue, item.getTitle());
        File file = getFile(item);
        Log.i(LOG_TAG, file.getAbsolutePath()+":"+file.exists());

        // setting suitable text on openButton
        if(file.exists()){
            item.setOpenButtonStatus(DownloadOpenItem.STATUS_OPEN);
        }else{

            // checking if item is in download
            boolean inList = checkItemInDownloads(position);
            // downloading
            if(inList){
                item.setOpenButtonStatus(DownloadOpenItem.STATUS_DOWNLOADING);
            }
            // if link is available then download
            else if(item.getUrl() != null){
                item.setOpenButtonStatus(DownloadOpenItem.STATUS_DOWNLOAD);
            }
            // else unavailable
            else{
                item.setOpenButtonStatus(DownloadOpenItem.STATUS_UNAVAILABLE);
            }
        }

        switch(item.getOpenButtonStatus()){
            case DownloadOpenItem.STATUS_DOWNLOAD:       downloadButton.setText(downloadString);       break;
            case DownloadOpenItem.STATUS_DOWNLOADING:    downloadButton.setText(downloadingString);    break;
            case DownloadOpenItem.STATUS_OPEN:           downloadButton.setText(openString);           break;
            case DownloadOpenItem.STATUS_UNAVAILABLE:    downloadButton.setText(unavailableString);    break;
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getFile(item);
                // open if already exists then open
                if(file.exists()){
                    Toast.makeText(mContext, openingString, Toast.LENGTH_SHORT).show();
                    downloadButton.setText(openString);
                    openPDFFile(file);
                }
                else if(item.getUrl()==null){
                    Toast.makeText(mContext,R.string.source_link_not_available,Toast.LENGTH_SHORT).show();
                }else {
                    if (checkItemInDownloads(position)) {
                        Toast.makeText(mContext, R.string.file_already_in_downloads, Toast.LENGTH_SHORT).show();
                    }else{
                        downloadButton.setText(downloadingString);
                        File cwd = new File(mWorkingDir);
                        Log.i(LOG_TAG, "downloading at " + cwd.getAbsolutePath());
                        downloadPDF(position, item, cwd);

                    }
                }

            }
        });

        return convertView;
    }

    private File getTempFile(){
        return FileUtil.getFileAtExternalFilesDir(mContext, new File(mWorkingDir,".temp.tmp").getPath());
    }

    private File getFile(DownloadOpenItem item){
        return getFile(item, "pdf");
    }
    private File getFile(DownloadOpenItem item, String fileFormat) {
        return FileUtil.getFileAtExternalFilesDir(mContext, new File(mWorkingDir,item.getTitle()+"."+fileFormat).getPath());
    }

    public void saveDownloadListIds(){

        Log.i(LOG_TAG, "saving download lists");
        // removing failed download ids
        Iterator<Pair<Integer,Long>> iterator=mDownloadIdLists.iterator();
        while(iterator.hasNext()){
            Pair<Integer,Long> pair=iterator.next();
            if(!checkItemInDownloads(pair.first))
                iterator.remove();
        }
        // saving download ids and position
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getTempFile()));
            oos.writeObject(mDownloadIdLists);
            oos.close();
        }catch(Exception e){
            Log.e(LOG_TAG, e.toString());
        }
        Log.i(LOG_TAG, "download list saved");
    }


    private boolean checkItemInDownloads(int position){
        boolean inList = false;
        Iterator<Pair<Integer,Long>> iterator=mDownloadIdLists.iterator();
        while(iterator.hasNext()){
            Pair pair= iterator.next();

            // item found in downloading list
            if(pair.first.equals(position)){

                // checking if still downloading or it failed
                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                if(downloadManager!=null) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById((Long) pair.second);    // filter your download by download Id
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        c.close();
                        Log.i(LOG_TAG, "DOWNLOAD_STATUS for position "+position+":"+String.valueOf(status));
                        // found in list and downloading/paused
                        if(status==DownloadManager.STATUS_RUNNING
                                || status==DownloadManager.STATUS_PENDING
                                || status==DownloadManager.STATUS_PAUSED)
                            inList = true;

                    }
                }
                break;
            }
        }
        return inList;
    }

    private void downloadPDF(int position, DownloadOpenItem item, File cwd){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getUrl()));
        request.setDescription("Downloading " + item.getTitle());
        request.setTitle(item.getTitle());

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(true);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        FileUtil.downloadFileAtDestination(mContext, request, mWorkingDir, item);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if(manager!=null) {
            mDownloadIdLists.add(new Pair(position, manager.enqueue(request)));
            Toast.makeText(mContext, "Downloading", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext,"Unable to fetch Download Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPDFFile(File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.i("Chapter ArrayAdapter", GenericFileProvider.getUriForFile(mContext,
                mContext.getApplicationContext().getPackageName() + ".provider", file).toString());
        intent.setDataAndType(GenericFileProvider.getUriForFile(mContext,
                mContext.getApplicationContext().getPackageName() + ".provider", file),
                "application/pdf");
//                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }

}
