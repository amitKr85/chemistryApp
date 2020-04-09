package com.example.atomicchemistry;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadDataAsync extends AsyncTask<URL,Void, ArrayList<ArrayList<String>>> {

    public interface AsyncDataHandleCallback{
        public void asyncDataHandleCallback(ArrayList<ArrayList<String>> list, int retType, Object ...args);
    }

    public static final int RETURN_TYPE_BOARDS = 0;
    public static final int RETURN_TYPE_FILES = 1;

    private static final String LOG_TAG = "LoadDataAsync";
    private String mQueryUrl;
    private int mReturnType;
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncDataHandleCallback callback;
    Object[] mArgs;
    public LoadDataAsync(String queryUrl, int returnType, Context context, Object ...args) {
        super();
        mQueryUrl = queryUrl;
        mReturnType = returnType;
        mContext = context;
        callback = (AsyncDataHandleCallback) mContext;
        mArgs = args;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog=new ProgressDialog(mContext);
        mDialog.setTitle(R.string.loading_data);
        mDialog.setMessage(mContext.getResources().getString(R.string.wait_while_fetching_the_data));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> objects) {
        super.onPostExecute(objects);
        mDialog.dismiss();
        // return fetched data to parent activity
        callback.asyncDataHandleCallback(objects, mReturnType, mArgs);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(ArrayList<ArrayList<String>> objects) {
        super.onCancelled(objects);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected ArrayList<ArrayList<String>> doInBackground(URL... urls) {

        ArrayList<ArrayList<String>> list=new ArrayList<>();
        // if no internet connection return empty list
        if(!InternetConnection.checkConnection(mContext)) {
//            Toast.makeText(mContext, R.string.no_internet_connection_found, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "no internet connection found");
            return list;
        }

        URL url=createURL(mQueryUrl);
        if(url==null){
            Log.e(LOG_TAG,"Empty Url returning empty list");
            return list;    //empty list
        }
        InputStream inputStream=null;
        String responseRawData=null;
        HttpURLConnection httpURLConnection=null;
        try {
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            responseRawData = getRawDataFromStream(inputStream);
            Log.i(LOG_TAG,"Response data="+responseRawData);

            list = getListFromRawData(responseRawData);
        } catch (IOException e) {
            Log.e(LOG_TAG,"exception in openConnection",e);
        }finally {
            if(httpURLConnection!=null)
                httpURLConnection.disconnect();
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,"exception in closing connection",e);
                }
            }
        }
        return list;
    }

    private ArrayList<ArrayList<String>> returnFilesData(String responseRawData){
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(responseRawData);
            for(int i=0;i<jsonArray.length();++i){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(new ArrayList<>(Arrays.asList(jsonObject.getString("name"), jsonObject.getString("url"))));
            }
        }catch(Exception e){
            Log.e(LOG_TAG, "error in parsing json"+e);
        }
        return list;
    }

    private ArrayList<ArrayList<String>> returnBoardsData(String responseRawData){
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(responseRawData);
            for(int i=0;i<jsonArray.length();++i)
                list.add(null);
            for(int i=0;i<jsonArray.length();++i){
                JSONObject stdItem = jsonArray.getJSONObject(i);
                int stdValue = stdItem.getInt("standard");
                ArrayList<String> boardsList = new ArrayList<>();
                JSONArray jsonBoards = stdItem.getJSONArray("boards");
                for(int j=0; j<jsonBoards.length(); ++j){
                    boardsList.add(jsonBoards.getString(j));
                }
                list.set(stdValue-9, boardsList);
            }
        }catch(Exception e){
            Log.e(LOG_TAG, "error in parsing json"+e);
        }
        return list;
    }

    private ArrayList<ArrayList<String>> getListFromRawData(String responseRawData) {

        switch (mReturnType){
            // it will return array of array of boards sorted by standard (lower to higher: 9,10,11,12)
            case RETURN_TYPE_BOARDS: return returnBoardsData(responseRawData);
            // it will return array of pairs (fileName, url) for the requested class and board
            case RETURN_TYPE_FILES: return returnFilesData(responseRawData);
            default: return null;
        }
//        ArrayList<ArrayList<String>> list=new ArrayList<>();
//
//        try{
//            JSONObject resultJSONObject=new JSONObject(responseRawData);
//            JSONArray array=resultJSONObject.getJSONArray("articles");
//            for(int i=0;i<array.length();++i){
//                JSONObject tmp=array.getJSONObject(i);
//                String title=tmp.getString("title");
//                String description=tmp.getString("description");
//                String url=tmp.getString("url");
//                String imgUrl=tmp.getString("urlToImage");
////                list.add(new NewsItem(title,description,url,imgUrl));
//            }
//        }catch (JSONException e) {
//
//            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
//        }
//        return list;
    }

    private String getRawDataFromStream(InputStream inputStream) {

        StringBuilder stringBuilder=new StringBuilder("");
        BufferedReader reader=new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            String line=reader.readLine();
            while(line!=null){
                stringBuilder.append(line);
                line=reader.readLine();
            }
        } catch (IOException e) {
            Log.e("Main Activity","exception in getRawDataFromStream ",e);
        }
        return stringBuilder.toString();
    }

    private URL createURL(String strURL){
        URL url=null;
        try {
            url=new URL(strURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"exception in create URL ",e);
        }
        return url;
    }

}
