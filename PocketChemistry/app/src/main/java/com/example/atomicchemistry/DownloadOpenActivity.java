package com.example.atomicchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadOpenActivity extends AppCompatActivity {

    public static final String PARAM_TITLE = "PARAM_TITLE";
    public static final String PARAM_LIST = "PARAM_LIST";
    public static final String PARAM_WORKING_DIR = "PARAM_WORKING_DIR";

    public static final String LOG_TAG = "DownloadOpenActivity";

    public DownloadOpenArrayAdapter mAdapter;
    public BroadcastReceiver mBroadcastReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_open);

        Bundle extras = getIntent().getExtras();
        List<DownloadOpenItem> list=new ArrayList<>();
        String title="Error";
        String workingDir = "";//this.getExternalFilesDir(null).getAbsolutePath();
        if(extras != null){
            if(extras.containsKey(PARAM_TITLE))
                title = extras.getString(PARAM_TITLE);
            if(extras.containsKey(PARAM_WORKING_DIR))
                workingDir = new File(workingDir, extras.getString(PARAM_WORKING_DIR)).getAbsolutePath();
            if(extras.containsKey(PARAM_LIST))
                list = (ArrayList<DownloadOpenItem>) extras.getSerializable(PARAM_LIST);
        }

        setTitle(title);

        ListView listView = findViewById(R.id.download_open_list_view);
        mAdapter = new DownloadOpenArrayAdapter(this, workingDir, list);

        listView.setAdapter(mAdapter);

        // to notify app when download is complete
        mBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long downloadedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Iterator<Pair<Integer,Long>> iterator = mAdapter.mDownloadIdLists.iterator();
                while(iterator.hasNext()){
                    Pair<Integer,Long> pair = iterator.next();
                    if(pair.second.equals(downloadedId)) {
                        // update open Button
                        mAdapter.notifyDataSetChanged();
                        Log.i(LOG_TAG, "list updated");
                        iterator.remove();
                        break;
                    }
                }
            }
        };
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mBroadcastReciever, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReciever);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.saveDownloadListIds();
    }
}
