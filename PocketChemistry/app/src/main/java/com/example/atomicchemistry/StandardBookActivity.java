package com.example.atomicchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StandardBookActivity extends AppCompatActivity {

    public static final String LOG_TAG = "StandardBookActivity";
    public static final String PARAM_STANDARD = "standard_value";
    public static final String PARAM_CATEGORY = "category_value";
    private final String logtag = "StandardBookActivity";
    public static final String BOOKS_DIR = "books";
    public static final String SOLUTION_DIR = "solution";
    public static final String NOTES_DIR = "notes";
    public static final String IMP_QUESTIONS_DIR = "imp_questions";
    public static final String SAMPLE_PAPERS_DIR = "sample_papers";
    private int mStdVal=9;
    private int mCatValue=0;
    public ChapterArrayAdapter mAdapter;
    public BroadcastReceiver mBroadcastReciever;

    public static String mUrl = "http://dummix.cf/assignment_ga.pdf";
//      dummy chapter list for 9,10,11 and given ones for 12
//    public static final List<List<String>> bookUrlList = Arrays.asList(
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList("http://dummix.cf/chemistry/books/12/lech101.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech102.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech103.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech104.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech105.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech106.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech107.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech108.pdf",
//                    "http://dummix.cf/chemistry/books/12/lech109.pdf"));
//
//    public static final List<List<String>> solutionUrlList = Arrays.asList(
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl));
//
//    public static final List<List<String>> notesUrlList = Arrays.asList(
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl));
//
//    public static final List<List<String>> impQuestionsUrlList = Arrays.asList(
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl));
//
//    public static final List<List<String>> samplePapersUrlList = Arrays.asList(
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
//            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_book);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mStdVal = Integer.valueOf(bundle.getString(PARAM_STANDARD,"9"));
            mCatValue = bundle.getInt(PARAM_CATEGORY, 0);
        }

        int titleResID = getResources().getIdentifier(MainActivity.STD_STRINGS[mStdVal-9]+"_standard","string", getPackageName());
        String headTitle = getResources().getString(titleResID);

        //////////////////////
        setTitle(titleResID);

        // getting source urls
        int arrResId = getResources().getIdentifier(MainActivity.STD_STRINGS[mStdVal-9]+"_topics","array",getPackageName());
        List<String> topics = Arrays.asList(getResources().getStringArray(arrResId));
//        List<List<String>> urlsList = bookUrlList;
//        if(mCatValue==1)
//            urlsList = solutionUrlList;
//        else if(mCatValue==2)
//            urlsList = notesUrlList;
//        else if(mCatValue==3)
//            urlsList = impQuestionsUrlList;
//        else if(mCatValue==4)
//            urlsList= samplePapersUrlList;

//        List<String> urls = urlsList.get(mStdVal-9);
        List<String> urls = UrlUtil.getUrlList(this, mCatValue, mStdVal);

//        ArrayList<ChapterItem> chapterList = new ArrayList<>();
//        for(int i=0;i<topics.size();++i){
//            String title = topics.get(i);
//            String url = i<urls.size() ? urls.get(i) : null;
//            List<String> subTopics = getSubTopics(title);
//            chapterList.add(new ChapterItem(title, url, subTopics));
//        }
        ArrayList<DownloadOpenItem> itemList = new ArrayList<>();
        for(int i=0;i<topics.size();++i){
            String superTitle = getResources().getString(R.string.chapter)+(i+1);
            String title = topics.get(i);
            String url = i<urls.size() ? urls.get(i) : null;
            int iconResId = getResources().getIdentifier("icon_"+(i+1), "drawable", getPackageName());
            String buttonText = MainActivity.CAT_STRINGS[mCatValue];
            itemList.add(new DownloadOpenItem(superTitle,title,url,iconResId,buttonText));
        }

        String workingDir = MainActivity.CAT_STRINGS[mCatValue]+ File.separator +mStdVal;

        Intent intent = new Intent(this, DownloadOpenActivity.class);
        intent.putExtra(DownloadOpenActivity.PARAM_TITLE, headTitle);
        intent.putExtra(DownloadOpenActivity.PARAM_WORKING_DIR, workingDir);
        intent.putExtra(DownloadOpenActivity.PARAM_LIST, itemList);
        startActivity(intent);
        finish();

//        Log.i(LOG_TAG,"Data Prepared");
//        mAdapter = new ChapterArrayAdapter(this, chapterList, mCatValue, mStdVal);
//        Log.i(LOG_TAG,"Adapter Prepared");
//        ListView listView = findViewById(R.id.chapter_list_view);
//        listView.setAdapter(mAdapter);

        // to notify app when download is complete
//        mBroadcastReciever = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //Fetching the download id received with the broadcast
//                long downloadedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                Iterator<Pair<Integer,Long>> iterator = mAdapter.mDownloadIdLists.iterator();
//                while(iterator.hasNext()){
//                    Pair<Integer,Long> pair = iterator.next();
//                    if(pair.second.equals(downloadedId)) {
//                        // update open Button
//                        mAdapter.notifyDataSetChanged();
//                        iterator.remove();
//                        break;
//                    }
//                }
//            }
//        };


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        registerReceiver(mBroadcastReciever, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mBroadcastReciever);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mAdapter.saveDownloadListIds();
    }

//    public List<String> getSubTopics(String topic){
//        String key = MainActivity.STD_STRINGS[mStdVal-9] +"_"+ topic.trim().replaceAll("[ -]","_").toLowerCase() + "_sub_topics";
//        int arrResId = getResources().getIdentifier(key, "array", getPackageName());
//        Log.i(logtag, "key="+key+", id="+arrResId);
//        return Arrays.asList(getResources().getStringArray(arrResId));
//    }

//    String mUrl = "http://dummix.cf/docs/LetUsC.pdf";

    // code to send pdf intent
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/example.pdf");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);


//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDescription("download book of C++");
//        request.setTitle("Download");
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "C++ book.pdf");
//
//        // get download service and enqueue file
//        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);

}
