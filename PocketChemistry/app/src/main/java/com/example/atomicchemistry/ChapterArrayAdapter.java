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

public class ChapterArrayAdapter extends ArrayAdapter<ChapterItem> {


    Context mContext;
    private static final String LOG_TAG = "ChapterArrayAdapter";
    private int mCatValue,mStdValue;
    public ArrayList<Pair<Integer,Long>> mDownloadIdLists;
    ChapterArrayAdapter(Context context, List<ChapterItem> list, int catValue, int stdValue){
        super(context, 0, list);
        mContext = context;
        mCatValue = catValue;
        mStdValue = stdValue;
        mDownloadIdLists = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getFileUtil(mStdValue, ".temp", "tmp")));
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chapter_item, null);
        }
        final ImageView imageView = convertView.findViewById(R.id.chapter_image_view);
        final TextView chapterTextView = convertView.findViewById(R.id.chapter_no_text_view);
        final TextView chapterTopicTextView = convertView.findViewById(R.id.chapter_topic_text_view);
//        final TextView subTopicsTextView = convertView.findViewById(R.id.subtopics_text_view);
        final LinearLayout subTopicsContainer = convertView.findViewById(R.id.subtopics_container);
        final Button subTopicsButton = convertView.findViewById(R.id.view_subtopics_button);
        final Button downloadButton = convertView.findViewById(R.id.download_chapter_button);
        final ChapterItem item = getItem(position);

        int iconResId = mContext.getResources().getIdentifier("icon_"+(position+1), "drawable", mContext.getPackageName());
        imageView.setImageResource(iconResId);
        int chapterResId = mContext.getResources().getIdentifier("chapter_"+(position+1),"string", mContext.getPackageName());
        chapterTextView.setText(mContext.getResources().getString(chapterResId));
        chapterTopicTextView.setText(item.getTitle());

        if(mCatValue==0) {

            // building sub-topics list
            List<String> subTopics = item.getSubTopics();
            subTopicsContainer.removeAllViews();
            for (int i = 0; i < subTopics.size(); ++i) {
                View tempView = LayoutInflater.from(mContext).inflate(R.layout.subtopics_item, null);
                TextView indexTextView = tempView.findViewById(R.id.subtopics_index_text_view);
                TextView topicTextView = tempView.findViewById(R.id.subtopics_topic_text_view);
                indexTextView.setText(String.valueOf(i + 1));
                topicTextView.setText(subTopics.get(i));

                subTopicsContainer.addView(tempView);
            }

            // measuring height to be used in animation
            int tempH = 0;
            if (!item.isSubTopicsCollapsed()) {
                subTopicsButton.setText(R.string.hide_subtopics);

                WindowManager mgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                Display display = mgr.getDefaultDisplay();
                subTopicsContainer.measure(display.getWidth(), display.getHeight());
                tempH = subTopicsContainer.getMeasuredHeight();
            } else {
                subTopicsButton.setText(R.string.view_subtopics);
            }
            subTopicsContainer.getLayoutParams().height = tempH;
            subTopicsContainer.requestLayout();

            // set sub-topics View/Hide animation
            final DetailsAnimator animator = new DetailsAnimator(mContext, subTopicsContainer);
            subTopicsButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (item.isSubTopicsCollapsed()) {
                        item.setSubTopicsCollapsed(false);
                        ((Button) view).setText(mContext.getResources().getString(R.string.hide_subtopics));
                        animator.expand();
                    } else {
                        item.setSubTopicsCollapsed(true);
                        ((Button) view).setText(mContext.getResources().getString(R.string.view_subtopics));
                        animator.collapse();
                    }
                }
            });

        }else{
            subTopicsButton.setVisibility(View.GONE);
        }

        // setting openButton Strings
        final int openStringId,openingStringId,downloadStringId,downloadingStringId,unavailableStringId;
        // for Solutions category
        if(mCatValue==1){
            openStringId = R.string.open_solution;
            openingStringId = R.string.opening_solution;
            downloadStringId = R.string.download_solution;
            downloadingStringId = R.string.solution_downloading;
            unavailableStringId = R.string.solution_unavailable;

        }
        // for Notes category
        else if(mCatValue==2){
            openStringId = R.string.open_notes;
            openingStringId = R.string.opening_notes;
            downloadStringId = R.string.download_notes;
            downloadingStringId = R.string.notes_downloading;
            unavailableStringId = R.string.notes_unavailable;

        }
        // for Imp. Questions category
        else if(mCatValue==3){
            openStringId = R.string.open_questions;
            openingStringId = R.string.opening_questions;
            downloadStringId = R.string.download_questions;
            downloadingStringId = R.string.questions_downloading;
            unavailableStringId = R.string.questions_unavailable;
            
        }
        // for Sample Papers category
        else if(mCatValue==4){
            openStringId = R.string.open_sample_papers;
            openingStringId = R.string.opening_sample_papers;
            downloadStringId = R.string.download_sample_papers;
            downloadingStringId = R.string.sample_papers_downloading;
            unavailableStringId = R.string.sample_papers_unavailable;
            
        }
        // default: category Books
        else{
            openStringId = R.string.open_chapter;
            openingStringId = R.string.opening_chapter;
            downloadStringId = R.string.download_chapter;
            downloadingStringId = R.string.chapter_downloading;
            unavailableStringId = R.string.chapter_unavailable;
        }


        File file = getFile(mStdValue, item.getTitle());
        Log.i(LOG_TAG, file.getAbsolutePath());

        // setting suitable text on openButton
        if(file.exists()){
            item.openButtonStatus = ChapterItem.STATUS_OPEN;
        }else{
            
            // checking if item is in download
            boolean inList = checkItemInDownloads(position);
            // downloading
            if(inList){
                item.openButtonStatus = ChapterItem.STATUS_DOWNLOADING;
            }
            // if link is available then download
            else if(item.getUrl() != null){
                item.openButtonStatus = ChapterItem.STATUS_DOWNLOAD;
            }
            // else unavailable
            else{
                item.openButtonStatus = ChapterItem.STATUS_UNAVAILABLE;
            }
        }
        
        switch(item.openButtonStatus){
            case ChapterItem.STATUS_DOWNLOAD:       downloadButton.setText(downloadStringId);       break;
            case ChapterItem.STATUS_DOWNLOADING:    downloadButton.setText(downloadingStringId);    break;
            case ChapterItem.STATUS_OPEN:           downloadButton.setText(openStringId);           break;
            case ChapterItem.STATUS_UNAVAILABLE:    downloadButton.setText(unavailableStringId);    break;
        }


//        if(file.exists()) {
//            downloadButton.setText(mContext.getResources().getString(openStringId));
//        }
//        else if(item.getUrl() != null)
//            downloadButton.setText(downloadStringId);
//        else
//            downloadButton.setText(unavailableStringId);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getUrl()==null){
                    Toast.makeText(mContext,"Source Link not available",Toast.LENGTH_SHORT).show();
                }else {
                    File file = getFile(mStdValue, item.getTitle());
                    // open if already exists
                    if (file.exists()) {
                        Toast.makeText(mContext, openingStringId, Toast.LENGTH_SHORT).show();
                        downloadButton.setText(openStringId);
                        openPDFFile(file);
                    } else if (checkItemInDownloads(position)) {
                        Toast.makeText(mContext, "File already in downloads", Toast.LENGTH_SHORT).show();
                    }else{
                        downloadButton.setText(downloadingStringId);
                        File cwd = getCWD(mStdValue);
                        Log.i(LOG_TAG, "downloading at " + cwd.getAbsolutePath());
                        boolean tempRes = cwd.mkdirs();
                        downloadPDF(position, item, cwd);

                    }
                }

            }
        });

        return convertView;
    }

    public void saveDownloadListIds(){

        // removing failed download ids
        Iterator<Pair<Integer,Long>> iterator=mDownloadIdLists.iterator();
        while(iterator.hasNext()){
            Pair<Integer,Long> pair=iterator.next();
            if(!checkItemInDownloads(pair.first))
                iterator.remove();
        }
        // saving download ids and position
        try{
            ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(getFileUtil(mStdValue,".temp","tmp"))));
            oos.writeObject(mDownloadIdLists);
            oos.close();
        }catch(Exception e){
            Log.e(LOG_TAG, e.toString());
        }
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
    
    private void downloadPDF(int position, ChapterItem item, File cwd){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getUrl()));
        request.setDescription("Downloading " + item.getTitle());
        request.setTitle(item.getTitle());

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(true);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(cwd.getAbsolutePath(), item.getTitle() + ".pdf");

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

    public File getFile(int stdVal, String title){
        return getFileUtil(stdVal, title, "pdf");
    }

    public File getFileUtil(int stdVal, String title, String filetype){
        String catDir;
        if(mCatValue==1)
            catDir = StandardBookActivity.SOLUTION_DIR;
        else if(mCatValue==2)
            catDir = StandardBookActivity.NOTES_DIR;
        else if(mCatValue==3)
            catDir = StandardBookActivity.IMP_QUESTIONS_DIR;
        else if(mCatValue==4)
            catDir = StandardBookActivity.SAMPLE_PAPERS_DIR;
        else
            catDir = StandardBookActivity.BOOKS_DIR;

        return new File(Environment.getExternalStorageDirectory()
            +File.separator+MainActivity.DIR
            +File.separator+ catDir
            +File.separator+String.valueOf(stdVal)
            +File.separator+title+"."+filetype);
    }

    public File getCWD(int stdVal){
        String catDir;
        if(mCatValue==1)
            catDir = StandardBookActivity.SOLUTION_DIR;
        else if(mCatValue==2)
            catDir = StandardBookActivity.NOTES_DIR;
        else if(mCatValue==3)
            catDir = StandardBookActivity.IMP_QUESTIONS_DIR;
        else if(mCatValue==4)
            catDir = StandardBookActivity.SAMPLE_PAPERS_DIR;
        else
            catDir = StandardBookActivity.BOOKS_DIR;

        return new File(MainActivity.DIR
                +File.separator+ catDir
                +File.separator+String.valueOf(stdVal));
    }
}
