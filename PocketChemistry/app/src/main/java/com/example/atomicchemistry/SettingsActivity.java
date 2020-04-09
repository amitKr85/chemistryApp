package com.example.atomicchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    public static final String LOG_TAG = "SettingsActivity";
    Spinner mLanguageSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mLanguageSpinner = (Spinner) findViewById(R.id.spinner_language_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(adapter);
        mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int selectedOption = this.getSharedPreferences(this.getPackageName()+MainActivity.PREFERENCE_FILE, Context.MODE_PRIVATE).getInt(MainActivity.LANG_KEY, 0);
        mLanguageSpinner.setSelection(selectedOption);
        Log.i(LOG_TAG, "Current lang. :"+this.getSharedPreferences(this.getPackageName()+MainActivity.PREFERENCE_FILE, Context.MODE_PRIVATE).getInt(MainActivity.LANG_KEY, -1));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(LOG_TAG, String.valueOf(mLanguageSpinner.getSelectedItemPosition()));
        SharedPreferences.Editor editor = this.getSharedPreferences(this.getPackageName()+MainActivity.PREFERENCE_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(MainActivity.LANG_KEY, mLanguageSpinner.getSelectedItemPosition());
        editor.apply();


    }
}
