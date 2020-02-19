package com.example.atomicchemistry;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomicchemistry.R;

import java.util.List;

public class StandardArrayAdapter extends ArrayAdapter<StandardItem> {

    public static final String TopicSep = ":";
    private Context mContext;
    private String mCategory;
    StandardArrayAdapter(Context context, List<StandardItem> list, String category){
        super(context, 0, list);
        mContext = context;
        mCategory = category;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.standard_item, null);
        }

        ImageView imageView = convertView.findViewById(R.id.standard_image_view);
        TextView stdTextView = convertView.findViewById(R.id.standard_text_view);
        Button detailsBtn = convertView.findViewById(R.id.view_topics_button);
        Button bookBtn = convertView.findViewById(R.id.view_book_button);
//        TextView topicsTextView = convertView.findViewById(R.id.topics_text_view);
        LinearLayout topicsContainer = convertView.findViewById(R.id.topics_container);

        final StandardItem item = getItem(position);
        imageView.setImageResource(item.getIconResId());
        stdTextView.setText(item.getTitle());

        if(mCategory.equals(MainActivity.CAT_STRINGS[0])) {
            //        StringBuilder topicsView = new StringBuilder();
            List<String> topics = item.getTopics();
            topicsContainer.removeAllViews();
            for (int i = 0; i < topics.size(); ++i) {
                View tempView = LayoutInflater.from(mContext).inflate(R.layout.subtopics_item, null);
                TextView indexTextView = tempView.findViewById(R.id.subtopics_index_text_view);
                TextView topicTextView = tempView.findViewById(R.id.subtopics_topic_text_view);
                indexTextView.setText(String.valueOf(i + 1));
                topicTextView.setText(topics.get(i));

                topicsContainer.addView(tempView);
                //            topicsView.append("• "+topics.get(i));
                //            if(i<topics.size()-1)
                //                topicsView.append(System.getProperty("line.separator"));
            }
            //        topicsTextView.setText(topicsView.toString());
            int tempH = 0;
            if (!item.isTopicsCollapsed()) {
                detailsBtn.setText(R.string.hide_topics);

                WindowManager mgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                Display display = mgr.getDefaultDisplay();
                topicsContainer.measure(display.getWidth(), display.getHeight());
                tempH = topicsContainer.getMeasuredHeight(); //view height
            } else {
                detailsBtn.setText(R.string.view_topics);
            }
            topicsContainer.getLayoutParams().height = tempH;
            topicsContainer.requestLayout();


            final DetailsAnimator animator = new DetailsAnimator(mContext, topicsContainer);
            detailsBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (item.isTopicsCollapsed()) {
                        item.setTopicsCollapsed(false);
                        ((Button) view).setText(mContext.getResources().getString(R.string.hide_topics));
                        animator.expand();
                    } else {
                        item.setTopicsCollapsed(true);
                        ((Button) view).setText(mContext.getResources().getString(R.string.view_topics));
                        animator.collapse();
                    }
                }
            });
        }else{
            detailsBtn.setVisibility(View.GONE);
            bookBtn.setText(R.string.view_solution);
        }

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StandardBookActivity.class);
                intent.putExtra(StandardBookActivity.PARAM_STANDARD, String.valueOf(item.getStdValue()));
                intent.putExtra(StandardBookActivity.PARAM_CATEGORY, mCategory);
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }
}
