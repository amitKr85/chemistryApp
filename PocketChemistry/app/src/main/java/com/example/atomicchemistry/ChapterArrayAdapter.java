package com.example.atomicchemistry;

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
import java.util.List;

public class ChapterArrayAdapter extends ArrayAdapter<ChapterItem> {

    Context mContext;
    public static final String logtag = "ChapterArrayAdapter";
    private String mCategory;
    ChapterArrayAdapter(Context context, List<ChapterItem> list, String category){
        super(context, 0, list);
        mContext = context;
        mCategory = category;
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

        if(mCategory.equals(MainActivity.CAT_STRINGS[0])) {
            //        StringBuilder subTopicsView = new StringBuilder();
            List<String> subTopics = item.getSubTopics();
            subTopicsContainer.removeAllViews();
            for (int i = 0; i < subTopics.size(); ++i) {
                View tempView = LayoutInflater.from(mContext).inflate(R.layout.subtopics_item, null);
                TextView indexTextView = tempView.findViewById(R.id.subtopics_index_text_view);
                TextView topicTextView = tempView.findViewById(R.id.subtopics_topic_text_view);
                indexTextView.setText(String.valueOf(i + 1));
                topicTextView.setText(subTopics.get(i));

                subTopicsContainer.addView(tempView);
                //            subTopicsView.append("• "+subTopics.get(i));
                //            if(i<subTopics.size()-1)
                //                subTopicsView.append(System.getProperty("line.separator"));
            }
            //        subTopicsTextView.setText(subTopicsView.toString());
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

        final int openStringId,openingStringId,downloadStringId,downloadingStringId,unavailableStringId;
        if(mCategory.equals(MainActivity.CAT_STRINGS[1])){
            openStringId = R.string.open_solution;
            openingStringId = R.string.opening_solution;
            downloadStringId = R.string.download_solution;
            downloadingStringId = R.string.solution_downloading;
            unavailableStringId = R.string.solution_unavailable;

        }else{
            openStringId = R.string.open_chapter;
            openingStringId = R.string.opening_chapter;
            downloadStringId = R.string.download_chapter;
            downloadingStringId = R.string.chapter_downloading;
            unavailableStringId = R.string.chapter_unavailable;
        }
        File file = getFile(item.getStdVal(), item.getTitle());
        Log.i(logtag, file.getAbsolutePath());
        if(file.exists())
            downloadButton.setText(mContext.getResources().getString(openStringId));
        else if(item.getUrl() != null)
            downloadButton.setText(downloadStringId);
        else
            downloadButton.setText(unavailableStringId);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getFile(item.getStdVal(), item.getTitle());
                if(file.exists()){
                    Toast.makeText(mContext,openingStringId,Toast.LENGTH_SHORT).show();
                    downloadButton.setText(openStringId);
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
                }else{

                    if(item.getUrl() != null) {
                        downloadButton.setText(downloadingStringId);
                        File cwd = getCWD(item.getStdVal());
                        Log.i(logtag, "downloading at " + cwd.getAbsolutePath());
                        cwd.mkdirs();


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
                        manager.enqueue(request);

                        Toast.makeText(mContext,"Downloading",Toast.LENGTH_SHORT).show();
                    }else{
                        downloadButton.setText(unavailableStringId);
                    }
                }

            }
        });


        return convertView;
    }

    public File getFile(int stdVal, String title){
        return new File(Environment.getExternalStorageDirectory()
            +File.separator+MainActivity.DIR
            +File.separator+ (mCategory.equals(MainActivity.CAT_STRINGS[1]) ? StandardBookActivity.SOLUTION_DIR : StandardBookActivity.BOOKS_DIR)
            +File.separator+String.valueOf(stdVal)
            +File.separator+title+".pdf");
    }

    public File getCWD(int stdVal){
        return new File(MainActivity.DIR
                +File.separator+ (mCategory.equals(MainActivity.CAT_STRINGS[1]) ? StandardBookActivity.SOLUTION_DIR : StandardBookActivity.BOOKS_DIR)
                +File.separator+String.valueOf(stdVal));
    }
}
