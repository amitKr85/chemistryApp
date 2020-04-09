package com.example.atomicchemistry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.atomicchemistry.R;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements LoadDataAsync.AsyncDataHandleCallback {

    private final String LOG_TAG = "MainActivity";

    public static final String[] STD_STRINGS = new String[]{"ninth","tenth", "eleventh","twelfth"};
    public static final String[] CAT_STRINGS = new String[]{"books","solutions","notes","imp_questions","sample_papers"};
    public static final int CAT_BOOKS = 0;
    public static final int CAT_SOLUTIONS = 1;
    public static final int CAT_NOTES = 2;
    public static final int CAT_IMP_QUESTIONS = 3;
    public static final int CAT_SAMPLE_PAPERS = 4;

    public static final String DIR = "chemistry";

    public static final String PREFERENCE_FILE = ".PREFERENCE_FILE";
    public static final String LANG_KEY = "selected_language";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // requesting permission
        isStoragePermissionGranted();

        SharedPreferences pref = this.getSharedPreferences(this.getPackageName()+PREFERENCE_FILE, Context.MODE_PRIVATE);
        int selectedOption = pref.getInt(MainActivity.LANG_KEY, -1);
        Log.i(LOG_TAG, "Current lang. :"+selectedOption);
        if(pref.getInt(LANG_KEY,-1) == -1){
            SharedPreferences defaultPref = this.getSharedPreferences(this.getPackageName()+PREFERENCE_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = defaultPref.edit();
            editor.putInt(LANG_KEY, 0);
            editor.apply();
        }
    }

    public void notImplementedToast(){
        Toast.makeText(this, "not yet implemented",Toast.LENGTH_SHORT).show();
    }
    public void onButtonClicked(View view){
        switch (view.getId()){
            case R.id.books_button:
                Intent intentBooks = new Intent(this, StandardCategoryActivity.class);
                intentBooks.putExtra(StandardCategoryActivity.PARAM_CATEGORY, CAT_BOOKS);
                startActivity(intentBooks);
                break ;

            case R.id.solutions_button:
                Intent intentSol = new Intent(this, StandardCategoryActivity.class);
                intentSol.putExtra(StandardCategoryActivity.PARAM_CATEGORY, CAT_SOLUTIONS);
                startActivity(intentSol);
//                notImplementedToast();
                break;

            case R.id.notes_button:
                Intent intentNotes = new Intent(this, StandardCategoryActivity.class);
                intentNotes.putExtra(StandardCategoryActivity.PARAM_CATEGORY, CAT_NOTES);
                startActivity(intentNotes);
//                notImplementedToast();
                break;

            case R.id.imp_ques_button:
                Intent intentImp = new Intent(this, StandardCategoryActivity.class);
                intentImp.putExtra(StandardCategoryActivity.PARAM_CATEGORY, CAT_IMP_QUESTIONS);
                startActivity(intentImp);
//                notImplementedToast();
                break;

            case R.id.sample_papers_button:
                Intent intentSample = new Intent(this, SamplePapersBoardCategoryActivity.class);
                startActivity(intentSample);
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_menu_settings:
//                Toast.makeText(this,"Settings clicked",Toast.LENGTH_SHORT).show();
                Intent settingsIntent = new Intent(this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.home_menu_privacy_policy:
                Toast.makeText(this,"Privacy Policy clicked",Toast.LENGTH_SHORT).show();
                // testing
//                Log.i(LOG_TAG, ""+new File("/sdfds", item.getTitle()+".pdf").getAbsolutePath());
//                File file = new File()
                String testQuery = "http://dummix.cf/chemistry/sample_papers/get_files.php?std=10&board=cbse";
                LoadDataAsync loadDataAsync = new LoadDataAsync(UrlUtil.getUrlStringForBoards(),LoadDataAsync.RETURN_TYPE_BOARDS, this,1,2,3);
                loadDataAsync.execute();
                break;
            case R.id.home_menu_about:
                Toast.makeText(this,"About clicked",Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i(LOG_TAG, "Permission is granted");
                return true;
            } else {

                Log.i(LOG_TAG, "Permission not allowed yet.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.i(LOG_TAG, "Permission is already granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }else{
            requestAgainOrExit();
        }
    }

    public void requestAgainOrExit(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Allow WRITE permission");
        builder.setMessage("App needs the permission to download materials.");
        builder.setPositiveButton("REQUEST PERMISSION", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });
        builder.setNegativeButton("EXIT APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void asyncDataHandleCallback(ArrayList<ArrayList<String>> list, int retType, Object ...args) {
        Log.i(LOG_TAG," data received from LoadDataAsync");
        Log.i(LOG_TAG, list.toString());
        Log.i(LOG_TAG, "args:"+ Arrays.toString(args));
    }
}
