package com.example.pocketchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StandardCategoryActivity extends AppCompatActivity{

    private final String LOG_TAG = "StandardCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_category);
        setTitle(getResources().getString(R.string.select_standard));

        ArrayList<StandardItem> itemList = new ArrayList<>();
        ArrayList<Integer> stdList = new ArrayList<>(Arrays.asList(9,10,11,12));
        ArrayList<Integer> topicsResList = new ArrayList<>(Arrays.asList(R.array.ninth_topics,R.array.tenth_topics,R.array.eleventh_topics, R.array.twelfth_topics));
        ArrayList<Integer> iconList = new ArrayList<>(Arrays.asList(R.drawable.icon_9,R.drawable.icon_10,R.drawable.icon_11,R.drawable.icon_12));
        for(int i=0;i<stdList.size();++i){
            int stdValue = stdList.get(i);
            int titleResId = getResources().getIdentifier(MainActivity.STD_STRINGS[stdValue-9]+"_standard","string",getPackageName());
            String title = getResources().getString(titleResId);
            int icon = iconList.get(i);
            List<String> topicsList = Arrays.asList(getResources().getStringArray(topicsResList.get(i)));

            itemList.add(new StandardItem(title, topicsList, icon, stdValue));
        }

        StandardArrayAdapter arrayAdapter = new StandardArrayAdapter(this, itemList);
        ListView listView = findViewById(R.id.standard_category_list_view);
        listView.setAdapter(arrayAdapter);

    }
}
