package com.example.atomicchemistry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BoardCategoryArrayAdapter extends ArrayAdapter<BoardCategoryItem> {

    Context mContext;
    BoardCategoryArrayAdapter(Context context, List<BoardCategoryItem> list){
        super(context, 0 ,list);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.board_category_item, null);

        final ImageView imageView = convertView.findViewById(R.id.standard_image_view);
        final TextView stdTextView = convertView.findViewById(R.id.standard_text_view);
        final LinearLayout boardsContainer = convertView.findViewById(R.id.boards_container_linear_layout);

        boardsContainer.removeAllViews();

        final BoardCategoryItem item = getItem(position);
        final int stdValue = item.getStdValue();
        int titleResId = mContext.getResources().getIdentifier(MainActivity.STD_STRINGS[stdValue-9]+"_standard","string",mContext.getPackageName());
        int iconResId = mContext.getResources().getIdentifier("icon_"+String.valueOf(stdValue),"drawable", mContext.getPackageName());

        imageView.setImageResource(iconResId);
        stdTextView.setText(titleResId);

        for(final String board:item.getBoardSet()){
            LinearLayout tempLayout =  (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.board_button_layout, null);
            Button button = tempLayout.findViewById(R.id.board_button);
            button.setText(board);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    LoadDataAsync loadDataAsync = new LoadDataAsync(UrlUtil.getUrlStringForFiles(stdValue, board),
                            LoadDataAsync.RETURN_TYPE_FILES,
                            mContext,
                            stdValue, board);
                    loadDataAsync.execute();
                }
            });

            boardsContainer.addView(tempLayout);
        }
        return convertView;
    }
}
