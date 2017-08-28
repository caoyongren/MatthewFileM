package com.matthew.filem.activity.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.matthew.filem.component.AppManager;
import com.matthew.filem.system.FileSortHelper;

public abstract class BaseActivity extends FragmentActivity {

    private FileSortHelper mFileSortHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //“无标题”功能的标志，关闭屏幕顶部的标题.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        AppManager.getAppManager().addActivity(this);
        mFileSortHelper = new FileSortHelper();
        initView();
        initData();
        initListener();
    }

    public void setFilePathBar(String displayPath) {}

    public FileSortHelper getFileSortHelper() {
        return mFileSortHelper;
    }

    protected abstract int getLayoutId();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
