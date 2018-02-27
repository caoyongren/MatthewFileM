package com.matthew.filem.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.matthew.filem.model.FileInfo;
import com.matthew.filem.system.FileIconTypeHelper;
import com.matthew.filem.system.FileSortHelper;

import java.util.Collection;

public interface IFileInteractionListener {
    View getViewById(int id);
    Context getContext();
    void startActivity(Intent intent);
    void onDataRefresh();
    void onPick(FileInfo f);

    boolean shouldShowOperationPane();

    /**
     * Handle operation listener.
     * @param id
     * @return true: indicate have operated it; false: otherwise.
     */
    boolean onOperation(int id);
    String getDisplayPath(String path);
    void runOnUiThread(Runnable r);
    FileInfo getItem(int pos);
    void sortCurrentList(FileSortHelper sort);
    Collection<FileInfo> getAllFiles();
    void addSingleFile(FileInfo file);
    boolean onRefreshFileList(String path, FileSortHelper sort);
    int getItemCount();


    FileIconTypeHelper getFileIconTypeHelper();
    boolean shouldHideMenu(int menu);
    boolean onBack();
    String getRealPath(String displayPath);
    boolean onNavigation(String path);
}
