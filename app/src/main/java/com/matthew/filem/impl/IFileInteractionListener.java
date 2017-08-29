package com.matthew.filem.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.matthew.filem.bean.FileInfo;
import com.matthew.filem.system.FileIconHelper;
import com.matthew.filem.system.FileSortHelper;

import java.util.Collection;

public interface IFileInteractionListener {
     View getViewById(int id);
     Context getContext();
     void startActivity(Intent intent);
     void onDataChanged();
     void onPick(FileInfo f);
     boolean shouldShowOperationPane();

    /**
     * Handle operation listener.
     * @param id
     * @return true: indicate have operated it; false: otherwise.
     */
     boolean onOperation(int id);
     String getDisplayPath(String path);
     String getRealPath(String displayPath);
     void runOnUiThread(Runnable r);
    // return true indicates the navigation has been handled
     boolean onNavigation(String path);
     boolean shouldHideMenu(int menu);
     FileIconHelper getFileIconHelper();
     FileInfo getItem(int pos);
     void sortCurrentList(FileSortHelper sort);
     Collection<FileInfo> getAllFiles();
     void addSingleFile(FileInfo file);
     boolean onBack();
     boolean onRefreshFileList(String path, FileSortHelper sort);
     int getItemCount();
}
