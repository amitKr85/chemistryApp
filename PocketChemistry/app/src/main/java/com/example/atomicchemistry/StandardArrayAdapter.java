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


import java.util.List;

public class StandardArrayAdapter extends ArrayAdapter<StandardItem> {

    private Context mContext;
    private int mCatValue;
    StandardArrayAdapter(Context context, List<StandardItem> list, int catValue){
        super(context, 0, list);
        mContext = context;
        mCatValue = catValue;
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
        LinearLayout topicsContainer = convertView.findViewById(R.id.topics_container);

        final StandardItem item = getItem(position);
        imageView.setImageResource(item.getIconResId());
        stdTextView.setText(item.getTitle());

        /// if List is opened for Books Category
        if(mCatValue==0) {

            // building topics section
            List<String> topics = item.getTopics();
            topicsContainer.removeAllViews();
            for (int i = 0; i < topics.size(); ++i) {
                View tempView = LayoutInflater.from(mContext).inflate(R.layout.subtopics_item, null);
                TextView indexTextView = tempView.findViewById(R.id.subtopics_index_text_view);
                TextView topicTextView = tempView.findViewById(R.id.subtopics_topic_text_view);
                indexTextView.setText(String.valueOf(i + 1));
                topicTextView.setText(topics.get(i));

                topicsContainer.addView(tempView);
            }

            // toggle view/hide topics
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
        }
        // if List is opened for other categories
        else{
            // remove topics button for other categories
            detailsBtn.setVisibility(View.GONE);
            if(mCatValue==1)    // for Solutions category
                bookBtn.setText(R.string.view_solution);
            else if(mCatValue==2)   // for Notes category
                bookBtn.setText(R.string.view_notes);
            else if(mCatValue==3)   // for Imp. questions category
                bookBtn.setText(R.string.view_questions);
            else if(mCatValue==4)   // for Sample Papers category
                bookBtn.setText(R.string.view_sample_papers);
        }

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StandardBookActivity.class);
                intent.putExtra(StandardBookActivity.PARAM_STANDARD, String.valueOf(item.getStdValue()));
                intent.putExtra(StandardBookActivity.PARAM_CATEGORY, mCatValue);
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }
}
