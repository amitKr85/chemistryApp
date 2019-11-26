package com.example.pocketchemistry;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChapterArrayAdapter extends ArrayAdapter<ChapterItem> {

    Context mContext;
    public static final String logtag = "ChapterArrayAdapter";
    ChapterArrayAdapter(Context context, List<ChapterItem> list){
        super(context, 0, list);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chapter_item, null);
        }
        final ImageView imageView = convertView.findViewById(R.id.chapter_image_view);
        final TextView chapterTextView = convertView.findViewById(R.id.chapter_no_text_view);
        final TextView chapterTopicTextView = convertView.findViewById(R.id.chapter_topic_text_view);
        final TextView subTopicsTextView = convertView.findViewById(R.id.subtopics_text_view);
        final LinearLayout subTopicsContainer = convertView.findViewById(R.id.subtopics_container);
        final Button subTopicsButton = convertView.findViewById(R.id.view_subtopics_button);
        final Button downloadButton = convertView.findViewById(R.id.download_chapter_button);
        final ChapterItem item = getItem(position);

        int iconResId = mContext.getResources().getIdentifier("icon_"+(position+1), "drawable", mContext.getPackageName());
        imageView.setImageResource(iconResId);
        int chapterResId = mContext.getResources().getIdentifier("chapter_"+(position+1),"string", mContext.getPackageName());
        chapterTextView.setText(mContext.getResources().getString(chapterResId));
        chapterTopicTextView.setText(item.getTitle());

        StringBuilder subTopicsView = new StringBuilder();
        List<String> subTopics = item.getSubTopics();
        for(int i=0;i<subTopics.size(); ++i){
            subTopicsView.append("• "+subTopics.get(i));
            if(i<subTopics.size()-1)
                subTopicsView.append(System.getProperty("line.separator"));
        }
        subTopicsTextView.setText(subTopicsView.toString());
        int tempH = 0;
        if(!item.isSubTopicsCollapsed()) {
            subTopicsButton.setText(R.string.hide_subtopics);

            WindowManager mgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = mgr.getDefaultDisplay();
            subTopicsContainer.measure(display.getWidth(), display.getHeight());
            tempH = subTopicsContainer.getMeasuredHeight();
        }else{
            subTopicsButton.setText(R.string.view_subtopics);
        }
        subTopicsContainer.getLayoutParams().height = tempH;
        subTopicsContainer.requestLayout();

        final DetailsAnimator animator = new DetailsAnimator(mContext, subTopicsContainer);
        subTopicsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(item.isSubTopicsCollapsed()){
                    item.setSubTopicsCollapsed(false);
                    ((Button)view).setText(mContext.getResources().getString(R.string.hide_subtopics));
                    animator.expand();
                }else{
                    item.setSubTopicsCollapsed(true);
                    ((Button)view).setText(mContext.getResources().getString(R.string.view_subtopics));
                    animator.collapse();
                }
            }
        });

        File file = getFile(item.getStdVal(), item.getTitle());
        Log.i(logtag, file.getAbsolutePath());
        if(file.exists())
            downloadButton.setText(mContext.getResources().getString(R.string.open_chapter));
        else if(item.getUrl() != null)
            downloadButton.setText(mContext.getResources().getString(R.string.download_chapter));
        else
            downloadButton.setText(mContext.getResources().getString(R.string.chapter_unavailable));

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getFile(item.getStdVal(), item.getTitle());
                if(file.exists()){
                    Toast.makeText(mContext,"Opening chapter",Toast.LENGTH_SHORT).show();
                    downloadButton.setText(mContext.getResources().getString(R.string.open_chapter));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    mContext.startActivity(intent);
                }else{

                    if(item.getUrl() != null) {
                        downloadButton.setText(mContext.getResources().getString(R.string.chapter_downloading));
                        File cwd = getCWD(item.getStdVal());
                        Log.i(logtag, "downloading at " + cwd.getAbsolutePath());
                        cwd.mkdirs();


                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getUrl()));
                        request.setDescription("Downloading " + item.getTitle());
                        request.setTitle(item.getTitle());
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        request.setDestinationInExternalPublicDir(cwd.getAbsolutePath(), item.getTitle() + ".pdf");

                        // get download service and enqueue file
                        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(mContext,"Downloading",Toast.LENGTH_SHORT).show();
                    }else{
                        downloadButton.setText(mContext.getResources().getString(R.string.chapter_unavailable));
                    }
                }

            }
        });


        return convertView;
    }

    public File getFile(int stdVal, String title){
        return new File(Environment.getExternalStorageDirectory()
            +File.separator+MainActivity.DIR
            +File.separator+StandardBookActivity.DIR
            +File.separator+String.valueOf(stdVal)
            +File.separator+title+".pdf");
    }

    public File getCWD(int stdVal){
        return new File(MainActivity.DIR
                +File.separator+StandardBookActivity.DIR
                +File.separator+String.valueOf(stdVal));
    }
}
