package com.example.atomicchemistry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class SamplePapersBoardCategoryActivity extends AppCompatActivity implements LoadDataAsync.AsyncDataHandleCallback{

    public static final String LOG_TAG = "BoardsCategoryActivity";
    private ListView mListView;
    BoardCategoryArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_papers_board_category);

        setTitle(R.string.select_standard_and_board);

        mListView = findViewById(R.id.board_category_list_view);
        LoadDataAsync loadDataAsync = new LoadDataAsync(UrlUtil.getUrlStringForBoards(), LoadDataAsync.RETURN_TYPE_BOARDS, this);
        loadDataAsync.execute();
    }


    @Override
    public void asyncDataHandleCallback(ArrayList<ArrayList<String>> list, int retType, Object ...args) {
        Log.i(LOG_TAG, "returnedList:"+list);
        if(list.size()==0){
            Toast.makeText(this, R.string.unable_to_fetch_data_from_server, Toast.LENGTH_SHORT).show();
        }
        if(retType == LoadDataAsync.RETURN_TYPE_BOARDS) {
            // getting available boards for each standard in offline mode
            String baseSamplePapersDir = MainActivity.CAT_STRINGS[MainActivity.CAT_SAMPLE_PAPERS];
            ArrayList<BoardCategoryItem> stdBoardList = new ArrayList<>();
            for (int i = 9; i < 13; ++i) {
                BoardCategoryItem boardCategoryItem = new BoardCategoryItem(i);
                File stdFile = FileUtil.getFileAtExternalFilesDir(this, new File(baseSamplePapersDir, String.valueOf(i)).getPath());
                File[] childFiles = stdFile.listFiles();
                // adding board from downloaded boards, helpful in case if internet is not connected or board removed from online json
                if (childFiles != null)
                    for (File child : childFiles) {
                        if (child.isDirectory()) {
                            boardCategoryItem.addBoard(child.getName());
                        }
                    }
                // adding board from fetched data
                if(list.size()>=4)
                    for (String board : list.get(i - 9)) {
                        boardCategoryItem.addBoard(board);
                    }
                if(boardCategoryItem.getBoardSet().size() > 0)
                    stdBoardList.add(boardCategoryItem);
            }
            if(stdBoardList.size()==0){
                Toast.makeText(this, R.string.unable_to_fetch_data, Toast.LENGTH_SHORT).show();
                finish();
            }else {
                mAdapter = new BoardCategoryArrayAdapter(this, stdBoardList);
                mListView.setAdapter(mAdapter);
            }


        }else if(retType == LoadDataAsync.RETURN_TYPE_FILES){
            // if returned data is list of files open download open activity

            int stdValue = (Integer) args[0];
            String board = (String) args[1];
//            int titleResId = ResUtil.getStandardStringId(this, stdValue); //getResources().getIdentifier(MainActivity.STD_STRINGS[stdValue-9]+"_standard","string",getPackageName());
            String title = ResUtil.getStandardString(this, stdValue); //getResources().getString(titleResId);
            String workingDir = FileUtil.getWorkingDirOfSamplePapersForBoard(this, stdValue, board); //MainActivity.CAT_STRINGS[MainActivity.CAT_SAMPLE_PAPERS]+File.separator+stdValue+File.separator+board;

            ArrayList<DownloadOpenItem> dOList = new ArrayList<>();
            // to keep track of added files
            HashSet<String> pdfFiles = new HashSet<>();
            // adding data from local files
            // getting all the files for the standard and the board available in offline mode
            File[] filesInDir = FileUtil.getFileAtExternalFilesDir(this, workingDir).listFiles();
            if(filesInDir != null)
                for (File file : filesInDir) {
                    if (file.getName().endsWith(".pdf")) {
                        String name = file.getName();
                        String fileTitle = name.substring(0, name.length() - 4);
                        String url = null;
                        String superTitle = getResources().getString(R.string.sample_paper) + " " + (pdfFiles.size() + 1);
                        int iconResId = ResUtil.getNumberImageResID(this, pdfFiles.size()+1); //getResources().getIdentifier("icon_" + (pdfFiles.size() + 1), "drawable", getPackageName());
                        String buttonText = getResources().getString(R.string.sample_paper);
                        DownloadOpenItem item = new DownloadOpenItem(superTitle, fileTitle, url, iconResId, buttonText);
                        dOList.add(item);
                        pdfFiles.add(fileTitle);
                    }
                }

            // adding files from fetched data
            for(int i=0;i<list.size();++i){
                String fileTitle = list.get(i).get(0);
                if(pdfFiles.contains(fileTitle))
                    continue;
                String superTitle = getResources().getString(R.string.sample_paper) + " " + (pdfFiles.size()+1);
                String url = list.get(0).get(1);
                int icondRedId = ResUtil.getNumberImageResID(this, pdfFiles.size()+1); //getResources().getIdentifier("icon_"+(pdfFiles.size()+1), "drawable", getPackageName());
                String buttonText = getResources().getString(R.string.sample_paper);
                DownloadOpenItem item = new DownloadOpenItem(superTitle, fileTitle, url, icondRedId, buttonText);
                dOList.add(item);
                pdfFiles.add(fileTitle);
            }

            if(dOList.size()==0){
                Toast.makeText(this, R.string.unable_to_fetch_data, Toast.LENGTH_SHORT).show();
            }else {


                Intent intent = new Intent(this, DownloadOpenActivity.class);
                intent.putExtra(DownloadOpenActivity.PARAM_TITLE, title);
                intent.putExtra(DownloadOpenActivity.PARAM_WORKING_DIR, workingDir);
                intent.putExtra(DownloadOpenActivity.PARAM_LIST, dOList);
                startActivity(intent);
            }

            // TODO: work from here
            // update server data and API and test sample papers functioning
        }


    }
}
