package com.example.atomicchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.atomicchemistry.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StandardCategoryActivity extends AppCompatActivity{

    private final String LOG_TAG = "StandardCategoryActivity";
    public static final String PARAM_CATEGORY = "category_value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_category);
        setTitle(getResources().getString(R.string.select_standard));

        ArrayList<StandardItem> itemList = new ArrayList<>();
        ArrayList<Integer> stdList = new ArrayList<>(Arrays.asList(9,10,11,12));
//        ArrayList<Integer> topicsResList = new ArrayList<>(Arrays.asList(R.array.ninth_topics,R.array.tenth_topics,R.array.eleventh_topics, R.array.twelfth_topics));
//        ArrayList<Integer> iconList = new ArrayList<>();
//        for(int std: stdList)
//            iconList.add(ResUtil.getNumberImageResID(this, std));
        for(int i=0;i<stdList.size();++i){
            int stdValue = stdList.get(i);
//            int titleResId = ResUtil.getStandardStringId(this, stdValue);
            String title = ResUtil.getStandardString(this, stdValue);
            int icon = ResUtil.getNumberImageResID(this, stdValue);
            List<String> topicsList = ResUtil.getStandardTopics(this, stdValue);

            itemList.add(new StandardItem(title, topicsList, icon, stdValue));
        }

        Bundle extras = getIntent().getExtras();
        int catValue = 0;
        if(extras!=null)
            catValue = extras.getInt(PARAM_CATEGORY, 0);
        StandardArrayAdapter arrayAdapter = new StandardArrayAdapter(this, itemList, catValue);
        ListView listView = findViewById(R.id.standard_category_list_view);
        listView.setAdapter(arrayAdapter);

    }
}
