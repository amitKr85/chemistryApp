package com.example.pocketchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StandardBookActivity extends AppCompatActivity {

    public static final String PARAM_STANDARD = "standard_value";
    private final String logtag = "StandardBookActivity";
    public static final String DIR = "books";
    private int mStdVal=9;

    String mUrl = "http://dummix.cf/assignment_ga.pdf";
    //  dummy chapter list for 9,10,11 and given ones for 12
    public final List<List<String>> urlList = Arrays.asList(
            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
            Arrays.asList(mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl,mUrl),
            Arrays.asList("http://dummix.cf/chemistry/books/12/lech101.pdf",
                    "http://dummix.cf/chemistry/books/12/lech102.pdf",
                    "http://dummix.cf/chemistry/books/12/lech103.pdf",
                    "http://dummix.cf/chemistry/books/12/lech104.pdf",
                    "http://dummix.cf/chemistry/books/12/lech105.pdf",
                    "http://dummix.cf/chemistry/books/12/lech106.pdf",
                    "http://dummix.cf/chemistry/books/12/lech107.pdf",
                    "http://dummix.cf/chemistry/books/12/lech108.pdf",
                    "http://dummix.cf/chemistry/books/12/lech109.pdf"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_book);

        TextView textView = findViewById(R.id.standard_book_text_view);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            textView.setText(bundle.getString(PARAM_STANDARD));
            mStdVal = Integer.valueOf(bundle.getString(PARAM_STANDARD));
        }

        int titleResID = getResources().getIdentifier(MainActivity.STD_STRINGS[mStdVal-9]+"_standard","string", getPackageName());
        setTitle(titleResID);

        int arrResId = getResources().getIdentifier(MainActivity.STD_STRINGS[mStdVal-9]+"_topics","array",getPackageName());
        List<String> topics = Arrays.asList(getResources().getStringArray(arrResId));
        List<String> urls = urlList.get(mStdVal-9);
        ArrayList<ChapterItem> chapterList = new ArrayList<>();
        for(int i=0;i<topics.size();++i){
            String title = topics.get(i);
            String url = i<urls.size() ? urls.get(i) : null;
            List<String> subTopics = getSubTopics(title);
            chapterList.add(new ChapterItem(mStdVal, title, url, subTopics));
        }

        ChapterArrayAdapter adapter = new ChapterArrayAdapter(this, chapterList);
        ListView listView = findViewById(R.id.chapter_list_view);
        listView.setAdapter(adapter);

    }

    public List<String> getSubTopics(String topic){
        String key = MainActivity.STD_STRINGS[mStdVal-9] +"_"+ topic.trim().replaceAll("[ -]","_").toLowerCase() + "_sub_topics";
        int arrResId = getResources().getIdentifier(key, "array", getPackageName());
        Log.i(logtag, "key="+key+", id="+arrResId);
        return Arrays.asList(getResources().getStringArray(arrResId));
    }

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
