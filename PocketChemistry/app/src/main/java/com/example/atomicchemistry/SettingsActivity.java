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
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    public static final String LOG_TAG = "SettingsActivity";
    Spinner mLanguageSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mLanguageSpinner = (Spinner) findViewById(R.id.spinner_language_selector);

        setTitle(R.string.settings);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(adapter);
        mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = FileUtil.getSharedPreferences(SettingsActivity.this).edit();
                String langCode;
                switch (position){
                    case 1: langCode = MainActivity.LANG_HI; break;
                    default: langCode = MainActivity.LANG_EN; break;
                }
                editor.putString(MainActivity.LANG_KEY, langCode);
                editor.apply();

                ResUtil.setLocale(SettingsActivity.this, langCode);

                setUIStrings();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String selectedLocaleCode = FileUtil.getSharedPreferences(this).getString(MainActivity.LANG_KEY, MainActivity.LANG_EN);
        int selectedOption;
        if(selectedLocaleCode.equals(MainActivity.LANG_HI))
            selectedOption = 1;
        else
            selectedOption = 0;
        mLanguageSpinner.setSelection(selectedOption);
        Log.i(LOG_TAG, "Current lang. :"+FileUtil.getSharedPreferences(this).getString(MainActivity.LANG_KEY,MainActivity.LANG_EN));
    }

    public void setUIStrings(){
        setTitle(R.string.settings);
        ((TextView)findViewById(R.id.settings_language_text_view)).setText(R.string.language);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(LOG_TAG, String.valueOf(mLanguageSpinner.getSelectedItemPosition()));
//        SharedPreferences.Editor editor = FileUtil.getSharedPreferences(this).edit();
//        String langCode;
//        switch (mLanguageSpinner.getSelectedItemPosition()){
//            case 1: langCode = MainActivity.LANG_HI; break;
//            default: langCode = MainActivity.LANG_EN; break;
//        }
//        editor.putString(MainActivity.LANG_KEY, langCode);
//        editor.apply();
//
//        ResUtil.setLocale(this, langCode);

    }
}
