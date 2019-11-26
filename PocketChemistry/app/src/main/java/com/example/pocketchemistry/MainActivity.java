package com.example.pocketchemistry;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "MainActivity";

    public static final String[] STD_STRINGS = new String[]{"ninth","tenth", "eleventh","twelfth"};

    public static final String DIR = "chemistry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // requesting permission
        isStoragePermissionGranted();
    }

    public void notImplementedToast(){
        Toast.makeText(this, "not yet implemented",Toast.LENGTH_SHORT).show();
    }
    public void onButtonClicked(View view){
        switch (view.getId()){
            case R.id.books_button:
                Intent intent = new Intent(this, StandardCategoryActivity.class);
                startActivity(intent);
                break ;

            case R.id.solutions_button:
                notImplementedToast();
                break;

            case R.id.notes_button:
                notImplementedToast();
                break;

            case R.id.imp_ques_button:
                notImplementedToast();
                break;

        }

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
}
