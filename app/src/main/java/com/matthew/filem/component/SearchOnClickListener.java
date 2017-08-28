package com.matthew.filem.component;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.View;

import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.R;
import com.matthew.filem.bean.SearchInfo;
import com.matthew.filem.fragment.SearchFragment;
import com.matthew.filem.utils.L;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.T;

import java.io.File;
import java.util.ArrayList;

public class SearchOnClickListener implements View.OnClickListener {
    private String LOG_TAG = "SearchOnQueryTextListener";
    private ProgressDialog progressDialog;
    private ArrayList<SearchInfo> mFileList = new ArrayList<>();
    FragmentManager manager;
    private final static String rootPath
                                = Environment.getExternalStorageDirectory().getAbsolutePath();
    File root = new File(rootPath);
    private Context context;
    private String input;

    public SearchOnClickListener(FragmentManager manager, Editable text, MainActivity context) {
        this.manager = manager;
        this.context = context;
        this.input = text.toString();
    }

    @Override
    public void onClick(View v) {
        v.requestFocus();
        if (mFileList.size() > 0 && LocalCacheLayout.getSearchText() != null) {
            mFileList.clear();
            startSearch(input.trim());
            if (mFileList.size() > 0) {
                startSearchFragment();
            } else {
                if (progressDialog != null){
                    progressDialog.dismiss();
                }
            }
        }
        assert mFileList != null;
        mFileList.clear();
        LocalCacheLayout.setSearchText(input.trim());
        startSearch(input.trim());
        L.e(LOG_TAG, mFileList.size() + "");
        if (mFileList.size() > 0) {
            startSearchFragment();
        } else {
            progressDialog.dismiss();
            T.showShort(context, context.getString(R.string.found_no_file));
        }
    }

    private void showDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("loading...");
        progressDialog.show();
    }

    private void startSearchFragment() {
        SearchFragment searchFragment = new SearchFragment(manager, mFileList);
        manager.popBackStack();
        manager.beginTransaction().replace(R.id.framelayout_right_main, searchFragment).commit();
        progressDialog.dismiss();
    }

    public void startSearch(final String text_search) {
        showDialog();
        if (root.exists() && root.isDirectory()) {
            final File[] currentFiles = root.listFiles();
            mFileList = searchFileFromDir(text_search, currentFiles);
        }
    }

    private ArrayList<SearchInfo> searchFileFromDir(String text_search, File[] files) {
        StringBuilder str_builder = new StringBuilder();
        File[] currentFiles;
        for (File file : files) {
            SearchInfo searchInfo = new SearchInfo();
            if (file.isDirectory()) {
                currentFiles = file.listFiles();
                str_builder.append(searchFileFromDir(text_search, currentFiles));
            }
            String fileName = file.getName();
            String filePath = file.getPath();

            if (fileName.contains(text_search)) {
                searchInfo.setFileName(fileName);
                searchInfo.setFilePath(filePath);
                if (mFileList.contains(fileName) && mFileList.contains(filePath)) {
                } else {
                    mFileList.add(searchInfo);
                }
            }
        }
        return mFileList;
    }
}