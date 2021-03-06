package com.matthew.filem.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matthew.filem.R;
import com.matthew.filem.view.activity.base.BaseActivity;
import com.matthew.filem.view.fragment.leftbar.CommonRightFragment;
import com.matthew.filem.model.FileInfo;
import com.matthew.filem.model.SeafileAccountInfo;
import com.matthew.filem.model.SeafileLibraryInfo;
import com.matthew.filem.component.CopyInfoDialog;
import com.matthew.filem.component.PopOnClickLintener;
import com.matthew.filem.component.PopWinShare;
import com.matthew.filem.component.SearchOnKeyListener;
import com.matthew.filem.component.UsbPropertyDialog;
import com.matthew.filem.view.fragment.SearchFragment;
import com.matthew.filem.view.fragment.base.BaseFragment;
import com.matthew.filem.view.fragment.computer.CloudDiskFragment;
import com.matthew.filem.view.fragment.computer.PersonalDiskFragment;
import com.matthew.filem.view.fragment.leftbar.ComputerFragment;
import com.matthew.filem.impl.IFileInteractionListener;
import com.matthew.filem.adapter.CommonFileAdapter;
import com.matthew.filem.system.FileOperationHelper;
import com.matthew.filem.system.Util;
import com.matthew.filem.utils.Constants;
import com.matthew.filem.utils.L;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.SeafileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity
                 implements View.OnClickListener, View.OnTouchListener {

    public static final String TAG = "MainActivity -- > DEBUG::";
    public static final String KEY_VIEW_TAG = "viewtag";
    public static final String SWITCH_VIEW_INTENT = "com.switchview";
    public static final String KEY_SWITCH_VIEW = "switch_view";
    public static final String DEFAULT_VIEW_TAG_GRID = "grid";
    public static final String DEFAULT_VIEW_TAG_LIST = "list";
    public static final String SWITCH_MENU_INTENT = "com.switchmenu";

    private static final int POPWINDOW_X = -15;
    private static final int POPWINDOW_Y = 10;
    private static final int USB_POPWINDOW_X = 60;
    private static final int USB_POPWINDOW_Y = 10;
    private static final int ACTIVITY_MIN_COUNT_FOR_BACK = 3;

    private static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    private static final String USB_DEVICE_ATTACHED = "usb_device_attached";
    private static final String USB_DEVICE_DETACHED = "usb_device_detached";
    private static final String IV_SWITCH_VIEW = "iv_switch_view";
    private static final String SETTING_POPWINDOW_TAG = "iv_setting";
    private static final String USB_POPWINDOW_TAG = "iv_usb";

    private Context mContext = this;
    //控件命名原则：１．控件名要简写　２．控件位置　３．控件的个性
    private TextView mTvMainDesktop;
    private TextView mTvMainMusic;
    private TextView mTvMainVideo;
    private TextView mTvMainPicture;
    private TextView mTvMainDocument;
    private TextView mTvMainDownload;
    private TextView mTvMainRecycle;
    private TextView mTvMainComputer;

    private TextView mTvMainSdaOne;
    private TextView mTvMainSdaTwo;
    private TextView mTvMainThree;

    private ImageView mIvMainListView;
    private ImageView mIvMainGridView;
    private ImageView mIvMainBack;
    private ImageView mIvMainSetting;
    private EditText mEtMainFilePath;
    private EditText mEtSearchView;
    private ImageView mIvMainSearchView;
    private TextView mTv_pop_up_one;
    private TextView mTv_pop_up_two;
    private TextView mTv_pop_up_three;
    private RelativeLayout mRl_usb_one;
    private RelativeLayout mRl_usb_two;
    private RelativeLayout mRl_usb_three;

    private FragmentManager mManager = getSupportFragmentManager();
    private PopWinShare mPopWinShare;
    public Fragment mCurFragment;
    public ComputerFragment mComputerFragment;

    private CommonRightFragment mDeskFragment, mMusicFragment, mVideoFragment, mPictrueFragment,
            mAddressFragment, mDocumentFragment, mDownloadFragment, mRecycleFragment;

    private CloudDiskFragment mCloudDiskFragment;
    private UsbConnectReceiver mReceiver;
    private String[] mUsb0;
    private String[] mUsb1;
    private boolean mIsMutiSelect;
    private SharedPreferences mSharedPreferences;
    public boolean mIsSdStorageFragment;

    public static Handler mHandler;
    private boolean mIsFirst = true;
    private HashMap<String, Integer> mHashMap;
    private SearchOnKeyListener mSearchOnKeyListener;
    private CopyInfoDialog mCopyInfoDialog;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mPopUpProgressDialog;
    public PersonalDiskFragment mPersonalDiskFragment;
    private CommonRightFragment mUsbStorageFragment;
    public BaseFragment mStartSearchFragment;
    private SearchFragment mSearchFragment;
    public String mCurPath;
    public SeafileAccountInfo mAccount;
    public SeafileUtils.SeafileSQLConsole mConsole;
    public CustomFileObserver mCustomFileObserver;
    private InitSeafileThread mInitSeafileThread;
    private SeafileThread mSeafileThread;
    private String mUsbPath;
    private ExecutorService mUsbSingleExecutor;
/**
 * </=============================== 分　割　线 ===================================================================== >
 * */
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mInitSeafileThread = new InitSeafileThread();
        mInitSeafileThread.start();

        mTvMainDesktop = (TextView) findViewById(R.id.tv_main_desk);//桌面
        mTvMainMusic = (TextView) findViewById(R.id.tv_main_music);//音乐
        mTvMainVideo = (TextView) findViewById(R.id.tv_main_video);//视频
        mTvMainPicture = (TextView) findViewById(R.id.tv_main_picture);//图片
        mTvMainDocument = (TextView) findViewById(R.id.tv_main_document);//文件
        mTvMainDownload = (TextView) findViewById(R.id.tv_main_download);//下载
        mTvMainRecycle = (TextView) findViewById(R.id.tv_main_recycle);//回收站
        mTvMainComputer = (TextView) findViewById(R.id.tv_main_computer);//计算机
        mTvMainSdaOne = (TextView) findViewById(R.id.tv_main_storage_one);//sda1
        mTvMainSdaTwo = (TextView) findViewById(R.id.tv_main_storage_two);//sda2
        mTvMainThree = (TextView) findViewById(R.id.tv_main_storage_three);//sda3

        mIvMainBack = (ImageView) findViewById(R.id.iv_main_back);//返回键
        mIvMainSetting = (ImageView) findViewById(R.id.iv_main_setting);//设置
        mIvMainGridView = (ImageView) findViewById(R.id.iv_main_grid_view);//网格视图
        mIvMainListView = (ImageView) findViewById(R.id.iv_main_list_view);//列表视图
        mEtMainFilePath = (EditText) findViewById(R.id.et_main_file_path);//文件路径
        mEtSearchView = (EditText) findViewById(R.id.et_search_main_view);//搜索输入
        mIvMainSearchView = (ImageView) findViewById(R.id.iv_main_search);//搜索图标

        mTv_pop_up_one = (TextView) findViewById(R.id.tv_pop_up_one);
        mTv_pop_up_two = (TextView) findViewById(R.id.tv_pop_up_two);
        mTv_pop_up_three = (TextView) findViewById(R.id.tv_pop_up_three);
        mRl_usb_one = (RelativeLayout) findViewById(R.id.rl_usb_one);
        mRl_usb_two = (RelativeLayout) findViewById(R.id.rl_usb_two);
        mRl_usb_three = (RelativeLayout) findViewById(R.id.rl_usb_three);

        //获取当前布局状态，进行保存．
        mSharedPreferences = getSharedPreferences(KEY_VIEW_TAG, Context.MODE_PRIVATE);
        String strViewTag = mSharedPreferences.getString(KEY_VIEW_TAG, DEFAULT_VIEW_TAG_GRID);
        L.i(TAG + "strViewTag:" +strViewTag);
        LocalCacheLayout.getInstance(mContext).setViewTag(strViewTag);

        //set default state.
        if (LocalCacheLayout.getViewTag() != null) {
            mIvMainGridView.setSelected(DEFAULT_VIEW_TAG_GRID.
                                        equals(LocalCacheLayout.getViewTag()));
            mIvMainListView.setSelected(DEFAULT_VIEW_TAG_LIST.
                                        equals(LocalCacheLayout.getViewTag()));
        }

        File file = new File(Constants.DOCUMENT_PATH);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }

        mUsbSingleExecutor = Executors.newSingleThreadExecutor();
        mHashMap = new HashMap<>();
        mHashMap.put(Constants.DESKFRAGMENT_TAG, R.id.tv_main_desk);
        mHashMap.put(Constants.MUSICFRAGMENT_TAG, R.id.tv_main_music);
        mHashMap.put(Constants.VIDEOFRAGMENT_TAG, R.id.tv_main_video);
        mHashMap.put(Constants.PICTRUEFRAGMENT_TAG, R.id.tv_main_picture);
        mHashMap.put(Constants.DOCUMENTFRAGMENT_TAG, R.id.tv_main_document);
        mHashMap.put(Constants.DOWNLOADFRRAGMENT_TAG, R.id.tv_main_download);
        mHashMap.put(Constants.RECYCLEFRAGMENT_TAG, R.id.tv_main_recycle);
        mHashMap.put(Constants.SDSTORAGEFRAGMENT_TAG, R.id.tv_main_computer);
        mHashMap.put(Constants.DETAILFRAGMENT_TAG, R.id.tv_main_picture);
        mHashMap.put(Constants.SYSTEMSPACEFRAGMENT_TAG, R.id.tv_main_storage_one);
        mHashMap.put(Constants.ADDRESSFRAGMENT_TAG, R.id.tv_main_storage_one);
        mHashMap.put(Constants.SYSTEM_SPACE_FRAGMENT_TAG, R.id.tv_main_computer);
        mHashMap.put(Constants.USBFRAGMENT_TAG,R.id.tv_main_storage_one);

        mCopyInfoDialog = CopyInfoDialog.getInstance(MainActivity.this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!Thread.currentThread().isInterrupted()) {
                    switch (msg.what) {
                        case Constants.DESKTOP_SHOW_FILE:
                            Intent showIntent = new Intent();
                            showIntent.setAction("android.intent.action.DESKTOP_SHOW_FILE");
                            showIntent.putExtra("path", (String) msg.obj);
                            MainActivity.this.sendBroadcast(showIntent);
                            break;
                        case Constants.DESKTOP_DELETE_FILE:
                            Intent deleteIntent = new Intent();
                            deleteIntent.setAction("android.intent.action.DESKTOP_DELETE_FILE");
                            deleteIntent.putExtra("path", (String) msg.obj);
                            MainActivity.this.sendBroadcast(deleteIntent);
                            break;
                        case UsbConnectReceiver.USB_STATE_ON:
                            initUsb(UsbConnectReceiver.USB_STATE_ON);
                            break;
                        case UsbConnectReceiver.USB_STATE_OFF:
                            initUsb(UsbConnectReceiver.USB_STATE_OFF);
                            break;
                        case 2:
                            initUsb(0);
                            break;
                        case Constants.USB1_READY:
                            mRl_usb_one.setVisibility(View.VISIBLE);
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            if (TextUtils.isEmpty(getCurPath()) ) {
                                mTvMainComputer.performClick();
                            }
                            break;
                        case Constants.USB2_READY:
                            mRl_usb_two.setVisibility(View.VISIBLE);
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            if (TextUtils.isEmpty(getCurPath()) ) {
                                mTvMainComputer.performClick();
                            }
                            break;
                        case Constants.USB3_READY:
                            mRl_usb_three.setVisibility(View.VISIBLE);
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            if (TextUtils.isEmpty(getCurPath()) ) {
                                mTvMainComputer.performClick();
                            }
                            break;
                        case Constants.REFRESH:
                            ((IFileInteractionListener) getVisibleFragment())
                                    .onRefreshFileList((String) msg.obj, getFileSortHelper());
                            resetClipboard();
                            break;
                        case Constants.COPY:
                            copy();
                            break;
                        case Constants.CUT:
                            cut();
                            break;
                        case Constants.PASTE:
                            paste();
                            break;
                        case Constants.COPY_INFO_SHOW:
                            mCopyInfoDialog.showDialog();
                            mCopyInfoDialog.changeTitle(MainActivity.this.getResources()
                                    .getString(R.string.copy_info));
                            L.i(TAG, "copy_info_show:" + Constants.COPY_INFO_SHOW);
                            break;
                        case Constants.COPY_INFO:
                            mCopyInfoDialog.changeMsg((String) msg.obj);
                            break;
                        case Constants.COPY_INFO_HIDE:
                            mCopyInfoDialog.cancel();
                            break;
                        case Constants.ONLY_REFRESH:
                            ((IFileInteractionListener) getVisibleFragment())
                                    .onRefreshFileList((String) msg.obj, getFileSortHelper());
                            break;
                        case Constants.USB_ONE:
                            if (Util.execUsb(new String[]{"df"}).size() != 1) {
                                mComputerFragment.hideMountSpaceOne();
                                if (getCurPath() != null && getCurPath().equals(mUsb0[0])) {
                                    showSdSFragmentAfterInstallUSB();
                                }
                            } else {
                                removeMobileDevice();
                            }
                            mRl_usb_one.setVisibility(View.GONE);
                            if (mPopUpProgressDialog != null) {
                                mPopUpProgressDialog.dismiss();
                            }
                            break;
                        case Constants.USB_TWO:
                            mRl_usb_two.setVisibility(View.GONE);
                            if (Util.execUsb(new String[]{"df"}).size() != 1) {
                                mComputerFragment.hideMountSpaceTwo();
                                if (getCurPath() != null && getCurPath().equals(mUsb1[0])) {
                                    showSdSFragmentAfterInstallUSB();
                                }
                            } else {
                                removeMobileDevice();
                            }
                            if (mPopUpProgressDialog != null) {
                                mPopUpProgressDialog.dismiss();
                            }
                            break;
                        case Constants.MENU_SHOWHIDE:
                            Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.can_not_search),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case Constants.SEAFILE_DATA_OK:
                            mCloudDiskFragment.setData(mAccount.mLibrarys);
                            mCloudDiskFragment.getAdapter().notifyDataSetChanged();
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void initData() {
        initFragment();
        checkFolder(null);
    }

    @Override
    protected void initListener() {
        mTvMainDesktop.setOnClickListener(this);
        mTvMainMusic.setOnClickListener(this);
        mTvMainVideo.setOnClickListener(this);
        mTvMainComputer.setOnClickListener(this);
        mTvMainPicture.setOnClickListener(this);
        mTvMainDocument.setOnClickListener(this);
        mTvMainDownload.setOnClickListener(this);
        mTvMainRecycle.setOnClickListener(this);
        mIvMainListView.setOnClickListener(this);
        mIvMainGridView.setOnClickListener(this);
        mIvMainBack.setOnClickListener(this);
        mIvMainSetting.setOnClickListener(this);
        mTvMainComputer.performClick();
        mTvMainSdaOne.setOnTouchListener(this);
        mTvMainSdaTwo.setOnTouchListener(this);
        mTvMainThree.setOnTouchListener(this);
        mSearchOnKeyListener = new SearchOnKeyListener(mManager,
                mEtSearchView.getText(), MainActivity.this);
        mEtSearchView.setOnKeyListener(mSearchOnKeyListener);
        mIvMainSearchView.setOnClickListener(this);
        NivagationOnClickLinstener nivagationOnClickLinstener = new NivagationOnClickLinstener();
        NivagationOnKeyLinstener nivagationOnKeyLinstener = new NivagationOnKeyLinstener();
        mEtMainFilePath.setOnClickListener(nivagationOnClickLinstener);
        mEtMainFilePath.setOnKeyListener(nivagationOnKeyLinstener);
        mEtMainFilePath.addTextChangedListener(new TextChangeListener());
        initUsb(-1);
        mCurFragment = mComputerFragment;
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        if (path != null) {
            mEtMainFilePath.setText(path);
            mEtMainFilePath.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ENTER));
            mEtMainFilePath.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                    KeyEvent.KEYCODE_ENTER));
        }
        setCurPath(path);
    }

    @Override
    public void setFilePathBar(String displayPath) {
        //顶部的：path显示．例如：SD卡/Download/app
        L.i(TAG + ":" + displayPath);
        if (displayPath != null) {
            if (mCurFragment == mComputerFragment &&
                    mComputerFragment.mCurFragment != null) {
                mEtMainFilePath.setText(displayPath);
            } else {
                if (mCurFragment instanceof CommonRightFragment) {
                    mEtMainFilePath.setText(displayPath);
                } else {
                    mEtMainFilePath.setText(null);
                }
            }
        }
    }

    private class InitSeafileThread extends Thread {
        @Override
        public void run() {
            super.run();
            SeafileUtils.init();
            SeafileUtils.start();
            mSeafileThread = new SeafileThread();
            mSeafileThread.start();
        }
    }

    private class SeafileThread extends Thread {
        private boolean isExistsSetting = false;
        private String id = "";
        @Override
        public void run() {
            super.run();
            ContentResolver mResolver = MainActivity.this.getContentResolver();
            Uri uriQuery = Uri.parse(Constants.OPENTHOS_URI);
            Cursor cursor = mResolver.query(uriQuery, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //current openthos id and password
                    SeafileUtils.mUserId = cursor.getString(cursor.getColumnIndex("openthosID"));
                    SeafileUtils.mUserPassword =
                                     cursor.getString(cursor.getColumnIndex("password"));
                    break;
                }
                cursor.close();
            }
            if (TextUtils.isEmpty(SeafileUtils.mUserId)
                                               || TextUtils.isEmpty(SeafileUtils.mUserPassword)) {
                return;
            }
            String librarys = SeafileUtils.listRemote();
            mAccount = new SeafileAccountInfo();
            mAccount.mUserName = SeafileUtils.mUserId;
            mConsole = new SeafileUtils.SeafileSQLConsole(MainActivity.this);
            mAccount.mUserId = mConsole.queryAccountId(mAccount.mUserName);
            mAccount.mFile = new File(SeafileUtils.SEAFILE_DATA_PATH, mAccount.mUserName);
            if (!mAccount.mFile.exists()) {
                mAccount.mFile.mkdirs();
            }
            try {
                if (librarys.equals("]")) {
                    librarys = getSharedPreferences(SeafileUtils.SEAFILE_DATA,
                            Context.MODE_PRIVATE).getString(SeafileUtils.SEAFILE_DATA, "");
                }
                JSONArray jsonArray = new JSONArray(librarys);
                for (int i = 0; i < jsonArray.length(); i++) {
                    SeafileLibraryInfo seafileLibrary = new SeafileLibraryInfo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    seafileLibrary.libraryId = jsonObject.getString("id");
                    seafileLibrary.libraryName =jsonObject.getString("name");
                    if (seafileLibrary.libraryName.equals(SeafileUtils.SETTING_SEAFILE_NAME)) {
                        isExistsSetting = true;
                        id = seafileLibrary.libraryId;
                        continue;
                    }
                    mAccount.mLibrarys.add(seafileLibrary);
                }
                getSharedPreferences(SeafileUtils.SEAFILE_DATA, Context.MODE_PRIVATE).edit()
                        .putString(SeafileUtils.SEAFILE_DATA, librarys).commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (mAccount.mLibrarys.size() > 0) {
                for (SeafileLibraryInfo seafileLibrary : mAccount.mLibrarys) {
                    String name = seafileLibrary.libraryName;
                    int isSync = mConsole.queryFile(mAccount.mUserId,
                            seafileLibrary.libraryId, seafileLibrary.libraryName);
                    seafileLibrary.isSync = isSync;
                    if (isSync == SeafileUtils.SYNC) {
                        SeafileUtils.sync(seafileLibrary.libraryId,
                                new File(mAccount.mFile, seafileLibrary.libraryName)
                                        .getAbsolutePath());
                    }
                }
                MainActivity.mHandler.sendEmptyMessage(Constants.SEAFILE_DATA_OK);
            }
            File settingSeafile = new File(SeafileUtils.SETTING_SEAFILE_PATH);
            if (!settingSeafile.exists()) {
                settingSeafile.mkdirs();
            }
            if (!isExistsSetting) {
                id = SeafileUtils.create(SeafileUtils.SETTING_SEAFILE_NAME);
            }
            SeafileUtils.sync(id, SeafileUtils.SETTING_SEAFILE_PROOT_PATH);
        }
    }

    public boolean isInitSeafile() {
        return mInitSeafileThread.isAlive();
    }

    public boolean isSeafile() {
        return mSeafileThread.isAlive();
    }

    private void showSdSFragmentAfterInstallUSB() {
        mManager.beginTransaction().remove(mCurFragment).show(mComputerFragment).commit();
        mCurFragment = mComputerFragment;
    }

    private void removeMobileDevice() {
        if (TextUtils.isEmpty(getCurPath())
                || (getCurPath() != null
                     && getCurPath().startsWith(Constants.PERMISS_DIR_STORAGE_USB))) {
            mManager.beginTransaction().remove(mComputerFragment).commit();
            mManager.beginTransaction().hide(mCurFragment).commit();
            mComputerFragment = new ComputerFragment(mManager,
                    USB_DEVICE_DETACHED, MainActivity.this);
            setSelectedBackground(R.id.tv_main_computer);
            mManager.beginTransaction().add(R.id.framelayout_right_main, mComputerFragment)
                    .show(mComputerFragment).commit();
            mCurFragment = mComputerFragment;
        } else {
            BaseFragment visibleFragment = (BaseFragment) getVisibleFragment();
            mManager.beginTransaction().remove(mComputerFragment).commit();
            mComputerFragment = new ComputerFragment(mManager,
                    USB_DEVICE_DETACHED, MainActivity.this);
            mManager.beginTransaction().add(R.id.framelayout_right_main, mComputerFragment)
                    .hide(mComputerFragment).commit();
            mComputerFragment.mCurFragment = visibleFragment;
        }
    }

    private void initUsb(int flags) {
        String[] cmd = new String[]{"df"};
        ArrayList<String[]> list = Util.execUsb(cmd);
        if (list != null && list.size() >= 1) {
            mUsb0 = list.get(0);
            if (mUsb0 != null && mUsb0.length > 0 && flags != 0 && flags != 1) {
                sendMsg(2);
            }
            if (list.size() >= 2) {
                mUsb1 = list.get(1);
                if (mUsb1 != null && mUsb1.length > 0 && flags != 0 && flags != 1) {
                    sendMsg(2);
                }
            }
        }
        if (flags == UsbConnectReceiver.USB_STATE_ON || flags == 2) {
            mTvMainSdaOne.setOnClickListener(MainActivity.this);
            mTv_pop_up_one.setOnClickListener(this);
            if (list.size() >= 2) {
                mTvMainSdaTwo.setOnClickListener(MainActivity.this);
                mTv_pop_up_two.setOnClickListener(this);
            }
            if (TextUtils.isEmpty(getCurPath())) {
                mManager.beginTransaction().remove(mComputerFragment).commit();
                mManager.beginTransaction().hide(mCurFragment).commit();
                mComputerFragment = new ComputerFragment(mManager, USB_DEVICE_ATTACHED,
                        MainActivity.this);
                setSelectedBackground(R.id.tv_main_computer);
                mManager.beginTransaction().add(R.id.framelayout_right_main, mComputerFragment)
                        .show(mComputerFragment).commit();
                mCurFragment = mComputerFragment;
            } else {
                BaseFragment visibleFragment = (BaseFragment) getVisibleFragment();
                mManager.beginTransaction().remove(mComputerFragment).commit();
                mComputerFragment = new ComputerFragment(mManager, USB_DEVICE_ATTACHED,
                        MainActivity.this);
                mManager.beginTransaction().add(R.id.framelayout_right_main, mComputerFragment)
                        .hide(mComputerFragment).commit();
                mComputerFragment.mCurFragment = visibleFragment;
            }
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setMessage(getString(R.string.USB_recognising));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

        } else if (flags == UsbConnectReceiver.USB_STATE_OFF) {
            if (!TextUtils.isEmpty(mUsbPath)) {
                if (mUsb0 != null && mUsbPath.equals(mUsb0[0])) {
                    mTv_pop_up_one.performClick();
                } else if (mUsb1 != null && mUsbPath.equals(mUsb1[0])) {
                    mTv_pop_up_two.performClick();
                }
            }
        }
    }

    private void initFragment() {
        mReceiver = new UsbConnectReceiver(this);
        FragmentTransaction transaction = mManager.beginTransaction();
        if (mDeskFragment == null) {
            mDeskFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.DESKTOP_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mDeskFragment,
                    Constants.DESKFRAGMENT_TAG).hide(mDeskFragment);
        }
        if (mMusicFragment == null) {
            mMusicFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.MUSIC_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mMusicFragment,
                    Constants.MUSICFRAGMENT_TAG).hide(mMusicFragment);
        }
        if (mVideoFragment == null) {
            mVideoFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.VIDEOS_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mVideoFragment,
                    Constants.VIDEOFRAGMENT_TAG).hide(mVideoFragment);
        }
        if (mPictrueFragment == null) {
            mPictrueFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.PICTURES_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mPictrueFragment,
                    Constants.PICTRUEFRAGMENT_TAG).hide(mPictrueFragment);
        }
        if (mDocumentFragment == null) {
            mDocumentFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.DOCUMENT_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mDocumentFragment,
                    Constants.DOCUMENTFRAGMENT_TAG).hide(mDocumentFragment);
        }
        if (mDownloadFragment == null) {
            mDownloadFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.DOWNLOAD_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mDownloadFragment,
                    Constants.DOWNLOADFRRAGMENT_TAG).hide(mDownloadFragment);
        }
        if (mRecycleFragment == null) {
            mRecycleFragment = new CommonRightFragment(Constants.LEFT_FAVORITES,
                    Constants.RECYCLE_PATH, null, null, true);
            transaction.add(R.id.framelayout_right_main, mRecycleFragment,
                    Constants.RECYCLEFRAGMENT_TAG).hide(mRecycleFragment);
        }
        if (mComputerFragment == null) {
            mComputerFragment = new ComputerFragment(mManager, null, MainActivity.this);
            transaction.add(R.id.framelayout_right_main, mComputerFragment).hide(mComputerFragment);
        }
        if (mPersonalDiskFragment == null) {
            mPersonalDiskFragment = new PersonalDiskFragment();
            transaction.add(R.id.framelayout_right_main, mPersonalDiskFragment).hide(mPersonalDiskFragment);
        }
        if (mCloudDiskFragment == null) {
            mCloudDiskFragment = new CloudDiskFragment();
            transaction.add(R.id.framelayout_right_main, mCloudDiskFragment).hide(mCloudDiskFragment);
        }
        transaction.commit();
    }

    //对于六个文件夹，不存在就需要创建．（系统中）
    private void checkFolder(Fragment fragment) {
        List<String> fileList = new ArrayList<>();
        fileList.add(Constants.DESKTOP_PATH);//storage/emulated/0/Desktop
        fileList.add(Constants.MUSIC_PATH);
        fileList.add(Constants.VIDEOS_PATH);
        fileList.add(Constants.PICTURES_PATH);
        fileList.add(Constants.DOCUMENT_PATH);
        fileList.add(Constants.DOWNLOAD_PATH);
        fileList.add(Constants.RECYCLE_PATH);
        for (int i = 0; i < fileList.size(); i++) {
            File file = new File(fileList.get(i));
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }
        }
        //刷新file path 'storage/emulated/0/Desktop' --> 'SD卡'
        if (fragment != null) {
            ((CommonRightFragment) fragment).refreshUI();
        }
    }

    class NivagationOnClickLinstener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.requestFocus();
        }
    }

    class NivagationOnKeyLinstener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                    v.clearFocus();
                    showSpaceFragment((TextView) v);
                    return true;
                case KeyEvent.KEYCODE_ESCAPE:
                    v.clearFocus();
                    return true;
            }
            return false;
        }
    }

    private void showSpaceFragment(TextView textView) {
        FragmentTransaction transaction = mManager.beginTransaction();
        String path = textView.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        int index = path.indexOf(Constants.SD_PATH);
        if (path.startsWith(Constants.SD_PATH)) {
            path = path.substring(index + 1);
            index = path.indexOf(Constants.SD_PATH);
        }
        String str = index == -1 ? path : path.substring(0, index);
        if (str.toLowerCase().equals(getResources().getString(R.string.address_sdcard))
                || str.toLowerCase().equals(getResources().getString(R.string.address_sd))) {
            if (index == -1) {
                path = Util.getSdDirectory();
            } else {
                path = Util.getSdDirectory() + path.substring(index);
            }
        }
        File file = new File(path);
        if (file.exists()) {
            transaction.hide(mCurFragment);
            mAddressFragment = new CommonRightFragment(
                                   Constants.LEFT_FAVORITES, path, null, null, false);
            transaction.add(R.id.framelayout_right_main, mAddressFragment, Constants.ADDRESSFRAGMENT_TAG);
            transaction.show(mAddressFragment).commit();
            mCurFragment = mAddressFragment;
            showRightFileInfo(R.id.et_main_file_path, path, mAddressFragment);
        } else {
            Toast.makeText(this, "" + getResources().getString(R.string.address_search_false),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        mReceiver.registerReceiver();
        super.onStart();
    }

    public class UsbConnectReceiver extends BroadcastReceiver {
        private static final String TAG = "UsbConnectReceiver";
        MainActivity execactivity;

        public static final int USB_STATE_ON = 0;
        public static final int USB_STATE_OFF = 1;
        public IntentFilter filter = new IntentFilter();

        public UsbConnectReceiver(Context context) {
            execactivity = (MainActivity) context;
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

            filter.addDataScheme("file");
        }

        public Intent registerReceiver() {
            return execactivity.registerReceiver(this, this.filter);
        }

        public void unregisterReceiver() {
            execactivity.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String dataString = intent.getDataString();
            mUsbPath = dataString.substring(7, dataString.length());
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) ||
                    intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
                sendMsg(USB_STATE_ON);
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED) ||
                    action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                if (!TextUtils.isEmpty(mUsbPath)) {
                    if (mUsb0 != null && mUsbPath.equals(mUsb0[0])) {
                        MainActivity.mHandler.sendEmptyMessage(Constants.USB_ONE);
                    } else if (mUsb1 != null && mUsbPath.equals(mUsb1[0])) {
                        MainActivity.mHandler.sendEmptyMessage(Constants.USB_TWO);
                    }
                }
            }
        }
    }

    private void sendMsg(int flags) {
        Message msg = new Message();
        msg.what = flags;
        mHandler.sendMessage(msg);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mIsMutiSelect = false;
        if (!mIsMutiSelect && !mIsFirst){
            sendBroadcastMessage("is_ctrl_press", null, mIsMutiSelect);
            mIsFirst = true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && !mEtSearchView.hasFocus()
                && !mEtMainFilePath.isFocused()) {
            onBackPressed();
        }
        if (event.isCtrlPressed()) {
            mIsMutiSelect = true;
        }
        if (mIsMutiSelect && mIsFirst) {
            sendBroadcastMessage("is_ctrl_press", null, mIsMutiSelect);
            mIsFirst = false;
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_X) {
            sendBroadcastMessage("iv_menu", "pop_cut", false);
            if (isCopyByHot()) {
                return false;
            }
            cut();
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_C) {
            sendBroadcastMessage("iv_menu", "pop_copy", false);
            if (isCopyByHot()) {
                return false;
            }
            copy();
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_V) {
            sendBroadcastMessage("iv_menu", "pop_paste", false);
            if (isCopyByHot()) {
                return false;
            }
            paste();
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_Z) {
            sendBroadcastMessage("iv_menu", "pop_cacel", false);
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_A) {
            sendBroadcastMessage("iv_menu", "pop_cacel", false);
            if (getVisibleFragment() instanceof CommonRightFragment) {
                final CommonRightFragment fragment = (CommonRightFragment) getVisibleFragment();
                fragment.mFileViewInteractionHub.onOperationSelectAll();
                CommonFileAdapter adapter = fragment.getCommonFileAdapter();
                List<FileInfo> list = adapter.getFileInfoTotalList();
                List<Integer> integerList = adapter.getSelectedFileList();
                for (int i = 0; i < list.size(); i++) {
                    integerList.add(i);
                }
                adapter.notifyDataSetChanged();
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_FORWARD_DEL && !event.isShiftPressed())
                || (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_D)) {
            sendBroadcastMessage("iv_menu", "pop_delete", false);
            if (isCopyByHot()) {
                return false;
            }
            ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.onOperationDelete();
        } else if (keyCode == KeyEvent.KEYCODE_FORWARD_DEL && event.isShiftPressed()) {
            sendBroadcastMessage("iv_menu", "pop_delete", false);
            if (isCopyByHot()) {
                return false;
            }
            ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.onOperationDeleteDirect();
        }
        if (keyCode == KeyEvent.KEYCODE_F2) {
            if (isCopyByHot()) {
                return false;
            }
            ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.onOperationRename();
        }
        if (keyCode == KeyEvent.KEYCODE_F5) {
            if (isCopyByHot()) {
                return false;
            }
            mHandler.sendMessage(Message.obtain(mHandler, Constants.ONLY_REFRESH,
                  ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.getCurrentPath()));
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            if (mEtMainFilePath.isFocused() || mEtSearchView.isFocused()) {
                return false;
            }
            if (getVisibleFragment() instanceof BaseFragment) {
                ((BaseFragment) getVisibleFragment()).enter();
            }
        }
        return false;
    }

    private boolean isCopyByHot() {
        return getVisibleFragment() instanceof PersonalDiskFragment
                || getVisibleFragment() instanceof ComputerFragment
                || mEtMainFilePath.isFocused() || mEtSearchView.isFocused();
    }

    public void cut() {
        ArrayList<FileInfo> list =
               ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.getSelectedFileList();
        StringBuffer stringBuffer = new StringBuffer();
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++ ) {
                stringBuffer.append("OtoCropFile:///" + list.get(i).filePath);
            }
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                    .setText(stringBuffer);
        }
    }

    public void paste() {
        String sourcePath = "";
        String destPath =
                    ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.getCurrentPath();
        try {
            sourcePath =
               (String) ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText();
        } catch (ClassCastException e) {
            sourcePath = "";
        }
        String[] srcCopyPaths = sourcePath.split("OtoFile:///");
        String[] srcCropPaths = sourcePath.split("OtoCropFile:///");
        if (!TextUtils.isEmpty(sourcePath) && sourcePath.startsWith("OtoFile:///")) {
            new CopyThread(srcCopyPaths, destPath).start();
        } else if (!TextUtils.isEmpty(sourcePath)
                                        && sourcePath.startsWith("OtoCropFile:///")) {
            new CropThread(srcCropPaths, destPath).start();
        }
    }

    private void resetClipboard() {
        String sourcePath = "";
        try {
            sourcePath =
                (String) ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText();
        } catch (ClassCastException e) {
            sourcePath = "";
        }
        if (!TextUtils.isEmpty(sourcePath)
               && sourcePath.startsWith("OtoCropFile:///")) {
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText("");
        }
    }

    class CopyThread extends Thread {
        String[] mSrcCopyPaths;
        String mDestPath;

        public CopyThread(String[] srcPaths, String destPath) {
            super();
            mSrcCopyPaths = srcPaths;
            mDestPath = destPath;
        }

        @Override
        public void run() {
            super.run();
            for (int i = 1; i < mSrcCopyPaths.length; i++) {
                FileOperationHelper.CopyFile(
                        mSrcCopyPaths[i].replace("OtoFile:///", ""), mDestPath);
            }
        }
    }

    class CropThread extends Thread {
        String[] mSrcCropPaths;
        String mDestPath;

        public CropThread(String[] srcPaths, String destPath) {
            super();
            mSrcCropPaths = srcPaths;
            mDestPath = destPath;
        }

        @Override
        public void run() {
            super.run();
            for (int i = 1; i < mSrcCropPaths.length; i++) {
                FileOperationHelper.MoveFile(
                        mSrcCropPaths[i].replace("OtoCropFile:///", ""), mDestPath, true);
            }
        }
    }

    public void copy() {
        ArrayList<FileInfo> list =
                ((BaseFragment) getVisibleFragment()).mFileViewInteractionHub.getSelectedFileList();
        StringBuffer stringBuffer = new StringBuffer();
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++ ) {
                stringBuffer.append("OtoFile:///" + list.get(i).filePath);
            }
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText(stringBuffer);
        }
    }

    //通过点击事件，触发碎片的切换～
    @Override
    public void onClick(View view) {
        clearNivagateFocus();
        switch (view.getId()) {
            case R.id.tv_main_desk:
                showRightFileInfo(R.id.tv_main_desk, Constants.DESKTOP_PATH, mDeskFragment);
                checkFolder(mDeskFragment);
                break;
            case R.id.tv_main_music:
                showRightFileInfo(R.id.tv_main_music, Constants.MUSIC_PATH, mMusicFragment);
                checkFolder(mMusicFragment);
                break;
            case R.id.tv_main_video:
                showRightFileInfo(R.id.tv_main_video, Constants.VIDEOS_PATH, mVideoFragment);
                checkFolder(mVideoFragment);
                break;
            case R.id.tv_main_picture:
                showRightFileInfo(R.id.tv_main_picture, Constants.PICTURES_PATH, mPictrueFragment);
                checkFolder(mPictrueFragment);
                break;
            case R.id.tv_main_document:
                showRightFileInfo(R.id.tv_main_document, Constants.DOCUMENT_PATH, mDocumentFragment);
                checkFolder(mDocumentFragment);
                break;
            case R.id.tv_main_download:
                showRightFileInfo(R.id.tv_main_download, Constants.DOWNLOAD_PATH, mDownloadFragment);
                checkFolder(mDownloadFragment);
                break;
            case R.id.tv_main_recycle:
                showRightFileInfo(R.id.tv_main_recycle, Constants.RECYCLE_PATH, mRecycleFragment);
                checkFolder(mRecycleFragment);
                break;
            case R.id.tv_main_computer:
                mIsSdStorageFragment = true;
                mEtMainFilePath.setText(null);
                Fragment fragment = mManager.findFragmentByTag(Constants.SYSTEMSPACEFRAGMENT_TAG);
                if (fragment != null) {
                    FragmentTransaction transaction = mManager.beginTransaction();
                    transaction.remove(fragment).commit();
                }

                showRightFileInfo(R.id.tv_main_computer, "", mComputerFragment);
                if (mComputerFragment != null) {
                    mComputerFragment.setSelectedCardBg(Constants.RETURN_TO_WHITE);
                }
                break;
            case R.id.tv_main_storage_one:
                setSelectedBackground(R.id.tv_main_storage_one);
                if (mCurFragment != null) {
                    mManager.beginTransaction().hide(mCurFragment).commit();
                }
                mUsbStorageFragment = new CommonRightFragment(
                                      Constants.USB_SPACE_FRAGMENT, mUsb0[0], null, null, false);
                mManager.beginTransaction().add(R.id.framelayout_right_main, mUsbStorageFragment,
                                               Constants.USBFRAGMENT_TAG).commit();
                mCurFragment = mUsbStorageFragment;
                break;
            case R.id.tv_main_storage_two:
                setSelectedBackground(R.id.tv_main_storage_two);
                if (mCurFragment != null) {
                    mManager.beginTransaction().hide(mCurFragment).commit();
                }
                mUsbStorageFragment = new CommonRightFragment(
                        Constants.USB_SPACE_FRAGMENT, mUsb1[0], null, null, false);
                mManager.beginTransaction().add(R.id.framelayout_right_main, mUsbStorageFragment,
                        Constants.USBFRAGMENT_TAG).commit();
                mCurFragment = mUsbStorageFragment;
                break;
            case R.id.tv_pop_up_one:
                uninstallUSB(Constants.USB_ONE);
                break;
            case R.id.tv_pop_up_two:
                uninstallUSB(Constants.USB_TWO);
                break;
            case R.id.iv_main_back:
                onBackPressed();
                break;
            case R.id.iv_main_setting:
                showPopWindow(SETTING_POPWINDOW_TAG, Constants.USB_NOT);
                break;
            case R.id.iv_main_grid_view:
                mIvMainGridView.setSelected(true);
                mIvMainListView.setSelected(false);
                LocalCacheLayout.setViewTag(DEFAULT_VIEW_TAG_GRID);
                sendBroadcastMessage(IV_SWITCH_VIEW, DEFAULT_VIEW_TAG_GRID, false);
                break;
            case R.id.iv_main_list_view:
                mIvMainGridView.setSelected(false);
                mIvMainListView.setSelected(true);
                LocalCacheLayout.setViewTag(DEFAULT_VIEW_TAG_LIST);
                sendBroadcastMessage(IV_SWITCH_VIEW, DEFAULT_VIEW_TAG_LIST, false);
                break;
            case R.id.iv_main_search:
                mEtSearchView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER));
                break;
        }
    }

    public void uninstallUSB(final int whichUsb) {
        if (mPopUpProgressDialog == null) {
            mPopUpProgressDialog = new ProgressDialog(this);
        }
        mPopUpProgressDialog.setMessage(getString(R.string.USB_umounting));
        mPopUpProgressDialog.setIndeterminate(true);
        mPopUpProgressDialog.setCancelable(true);
        mPopUpProgressDialog.setCanceledOnTouchOutside(true);
        mPopUpProgressDialog.show();
        mUsbSingleExecutor.execute(new UninstallUsbThread(whichUsb));
    }

    private class UninstallUsbThread implements Runnable {
        private int mWhichUsb;
        public UninstallUsbThread(int whichUsb) {
            mWhichUsb = whichUsb;
        }
        @Override
        public void run() {
            Process pro;
            BufferedReader in = null;
            try {
                pro = Runtime.getRuntime().exec("sync");
                in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                }
                MainActivity.mHandler.sendEmptyMessage(mWhichUsb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //通过点击左侧导航栏显示右侧的文件夹信息．－－> 碎片展示
    private void showRightFileInfo(int id, String path, Fragment fragment) {
        /**
         * 例如：　点击桌面，然后，进入里面一个文件夹，然后点击计算机
         * 　　再回到桌面，应该显示为最初的界面(不应该是进入的文件夹的界面)
         * 方式：　将数据清空！
         * {clearSelected: 选中的文件}
         * */
        if (fragment instanceof CommonRightFragment) {
            L.i(TAG, "showRightFileInfo: path -- >" + path);
            ((CommonRightFragment) fragment).setPath(path);
            CommonFileAdapter adapter = ((CommonRightFragment) fragment).getCommonFileAdapter();
            if (adapter != null) {
                L.i(TAG, "showRightFileInfo: list" + adapter.getSelectedFileList());
                adapter.getSelectedFileList().clear();
                ((CommonRightFragment) fragment).getFileViewInteractionHub().clearSelected();
            }
        }

        setSelectedBackground(id);
        mEtMainFilePath.setText(path);
        setCurPath(path);
        //碎片管理
        FragmentTransaction transaction = mManager.beginTransaction();
        if (mCurFragment != null) {
            transaction.hide(mCurFragment);
        }
        if (fragment != null) {
            transaction.show(fragment);
        }
        transaction.commit();
        mCurFragment = fragment;
    }

    private void setSelectedBackground(int id) {
        initSelectedState();
        switch (id) {
            case R.id.tv_main_computer:
                mTvMainComputer.setSelected(true);
                break;
            case R.id.tv_main_desk:
                mTvMainDesktop.setSelected(true);
                break;
            case R.id.tv_main_music:
                mTvMainMusic.setSelected(true);
                break;
            case R.id.tv_main_video:
                mTvMainVideo.setSelected(true);
                break;
            case R.id.tv_main_picture:
                mTvMainPicture.setSelected(true);
                break;
            case R.id.tv_main_document:
                mTvMainDocument.setSelected(true);
                break;
            case R.id.tv_main_download:
                mTvMainDownload.setSelected(true);
                break;
            case R.id.tv_main_recycle:
                mTvMainRecycle.setSelected(true);
                break;
            case R.id.tv_main_storage_one:
                mTvMainSdaOne.setSelected(true);
                break;
            case R.id.tv_main_storage_two:
                mTvMainSdaTwo.setSelected(true);
                break;
            case R.id.tv_main_storage_three:
                mTvMainThree.setSelected(true);
                break;
            default:
                break;
        }
    }

    private void initSelectedState() {
        mTvMainMusic.setSelected(false);
        mTvMainDesktop.setSelected(false);
        mTvMainVideo.setSelected(false);
        mTvMainComputer.setSelected(false);
        mTvMainPicture.setSelected(false);
        mTvMainSdaOne.setSelected(false);
        mTvMainSdaTwo.setSelected(false);
        mTvMainThree.setSelected(false);
        mTvMainDocument.setSelected(false);
        mTvMainDownload.setSelected(false);
        mTvMainRecycle.setSelected(false);
    }

    // send Message to System package
    //Todo: 使用Ａctivity和fragment之间的数据传递代替广播；
    private void sendBroadcastMessage(String name, String tag, boolean isCtrl) {
        Intent intent = new Intent();
        switch (name) {
            case IV_SWITCH_VIEW:
                intent.setAction(SWITCH_VIEW_INTENT);
                intent.putExtra(KEY_SWITCH_VIEW, tag);
                break;
            case "iv_fresh":
                intent.setAction("com.refreshview");
                break;
            case "iv_menu":
                intent.setAction(SWITCH_MENU_INTENT);
                intent.putExtra("pop_menu", tag);
                break;
            case "is_ctrl_press":
                intent.setAction("com.isCtrlPress");
                intent.putExtra("is_ctrl_press", isCtrl);
                break;
        }
        sendBroadcast(intent);
    }

    private void showPopWindow(String menu_tag, int whichUsb) {
        mPopWinShare = null;
        PopOnClickLintener paramOnClickListener = new PopOnClickLintener(menu_tag,
                                                      MainActivity.this, mManager);
        if (SETTING_POPWINDOW_TAG.equals(menu_tag)) {
            mPopWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    menu_tag);
            mPopWinShare.setFocusable(true);
            mPopWinShare.showAsDropDown(mIvMainSetting, POPWINDOW_X, POPWINDOW_Y);
        } else if (USB_POPWINDOW_TAG.equals(menu_tag)) {
            mPopWinShare = new PopWinShare(MainActivity.this, new usbListener(whichUsb),
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    menu_tag);
            mPopWinShare.setFocusable(true);
            mPopWinShare.showAsDropDown(mTvMainSdaOne, USB_POPWINDOW_X, USB_POPWINDOW_Y);
        }
        mPopWinShare.update();
        mPopWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mPopWinShare.dismiss();
                }
            }
        });
    }

    public void DismissPopwindow() {
        mPopWinShare.dismiss();
    }

    //重写Activity中的方法．点击back回退按键．
    @Override
    public void onBackPressed() {
        mSearchOnKeyListener.setInputData(null);
        mManager.findFragmentById(R.id.framelayout_right_main);
        if (mCurFragment != mComputerFragment) {
            if (mCurFragment instanceof CommonRightFragment && mCurFragment.getTag() != null) {
                CommonRightFragment sdCurFrament = (CommonRightFragment) mCurFragment;
                String currentPath = sdCurFrament.getCurrentPath();
                setCurPath(currentPath);
                mEtMainFilePath.setText(currentPath);
                if (mCurFragment.getTag().equals(Constants.PERSONALSYSTEMSPACE_TAG)) {
                    if (mPersonalDiskFragment.canGoBack()) {
                        //逐级返回但是不能返回到根部
                        mPersonalDiskFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() >= ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else {
                        backToUpDir("SDCard", mPersonalDiskFragment, R.id.tv_main_computer);
                    }
                } else if (mCurFragment.getTag().equals(Constants.SEAFILESYSTEMSPACE_TAG)) {
                    if (mCloudDiskFragment.canGoBack()) {
                        mCloudDiskFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() >= ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    }
                } else if (mCurFragment.getTag().equals(Constants.SDSSYSTEMSPACE_TAG)) {
                    if (mComputerFragment.canGoBack()) {
                        mComputerFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() >= ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.USBFRAGMENT_TAG)) {
                    if (mUsbStorageFragment.canGoBack()) {
                        mUsbStorageFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() >= ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.DESKFRAGMENT_TAG)) {
                    if (mDeskFragment.canGoBack()) {
                        mDeskFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.DESKTOP_PATH, mDeskFragment, R.id.tv_main_desk);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.MUSICFRAGMENT_TAG)) {
                    if (mMusicFragment.canGoBack()) {
                        mMusicFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.MUSIC_PATH, mMusicFragment, R.id.tv_main_music);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.VIDEOFRAGMENT_TAG)) {
                    if (mVideoFragment.canGoBack()) {
                        mVideoFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.VIDEOS_PATH, mVideoFragment, R.id.tv_main_video);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.PICTRUEFRAGMENT_TAG)) {
                    if (mPictrueFragment.canGoBack()) {
                        mPictrueFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.PICTURES_PATH, mPictrueFragment, R.id.tv_main_picture);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.DOCUMENTFRAGMENT_TAG)) {
                    if (mDocumentFragment.canGoBack()) {
                        mDocumentFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.DOCUMENT_PATH, mDocumentFragment, R.id.tv_main_document);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.DOWNLOADFRRAGMENT_TAG)) {
                    if (mDownloadFragment.canGoBack()) {
                        mDownloadFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.DOWNLOAD_PATH, mDownloadFragment, R.id.tv_main_download);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.RECYCLEFRAGMENT_TAG)) {
                    if (mRecycleFragment.canGoBack()) {
                        mRecycleFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else if (mManager.getBackStackEntryCount() == ACTIVITY_MIN_COUNT_FOR_BACK) {
                        backToUpDir(Constants.RECYCLE_PATH, mRecycleFragment, R.id.tv_main_recycle);
                    } else {
                        returnToRootDir();
                    }
                } else if (mCurFragment.getTag().equals(Constants.SEARCHSYSTEMSPACE_TAG)) {
                    CommonRightFragment searchSysFragment = (CommonRightFragment) mCurFragment;
                    if (searchSysFragment.canGoBack()) {
                        searchSysFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else {
                        returnToSearchFragment();
                    }
                } else if (mCurFragment.getTag().equals(Constants.ADDRESSFRAGMENT_TAG)) {
                    CommonRightFragment addressFragment = (CommonRightFragment) mCurFragment;
                    if (addressFragment.canGoBack()) {
                        addressFragment.goBack();
                    } else if (mManager.getBackStackEntryCount() > ACTIVITY_MIN_COUNT_FOR_BACK) {
                        mManager.popBackStack();
                    } else {
                        returnToRootDir();
                    }
                }
            } else if (mStartSearchFragment != null && mCurFragment instanceof SearchFragment) {
                mManager.beginTransaction().hide(mCurFragment).show(mStartSearchFragment).commit();
                mCurFragment = mStartSearchFragment;
                mStartSearchFragment = null;
            } else {
                returnToRootDir();
            }
        }
    }

    public Fragment getVisibleFragment(){
        List<Fragment> fragments = mManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    private void returnToSearchFragment() {
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.hide(getVisibleFragment());
        mSearchFragment = (SearchFragment) mManager
                              .findFragmentByTag(Constants.SEARCHFRAGMENT_TAG);
        fragmentTransaction.show(mSearchFragment);
        fragmentTransaction.commit();
        mCurFragment = mSearchFragment;
    }

    //Desktop/music/video/picture/document/download/recycler/
    private void backToUpDir(String path, Fragment fragment, int tvId) {
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.hide(getVisibleFragment()).show(fragment).commit();
        mEtMainFilePath.setText(path);
        setSelectedBackground(tvId);
        mCurFragment = fragment;
    }

    public void returnToRootDir() {
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.hide(mCurFragment);
        fragmentTransaction.show(mComputerFragment);
        fragmentTransaction.commit();
        mEtMainFilePath.setText(null);
        setSelectedBackground(R.id.tv_main_computer);
        mComputerFragment.setSelectedCardBg(Constants.RETURN_TO_WHITE);
        mCurFragment = mComputerFragment;
    }

    private void returnToSeafileDir() {
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.hide(getVisibleFragment());
        fragmentTransaction.show(mCloudDiskFragment);
        fragmentTransaction.commit();
        mEtMainFilePath.setText("seafile");
        //setSelectedBackground(R.id.tv_main_cloud_service);
        mCurFragment = mCloudDiskFragment;
    }

    public interface IBackPressedListener {
    }

    public void setNavigationPath(String displayPath) {
        mEtMainFilePath.setText(displayPath);
    }

    public void setCurPath(String path) {
        mCurPath = path;
    }

    public String getCurPath() {
        return mCurPath;
    }

    class usbListener implements View.OnClickListener{

        private int mWhichUsb;
        public usbListener(int whichUsb){
            mWhichUsb = whichUsb;
        }
        @Override
        public void onClick(View view) {
            clearNivagateFocus();
            DismissPopwindow();
            switch (view.getId()) {
                case R.id.pop_usb_view:
                    uninstallUSB(mWhichUsb);
                    Intent intent = new Intent();
                    intent.setAction(SWITCH_MENU_INTENT);
                    intent.putExtra("pop_menu", "view_or_dismiss");
                    sendBroadcast(intent);
                    break;
                case R.id.pop_usb_info:
                    if (mUsb0[0] != null && new File(mUsb0[0]).exists()) {
                        UsbPropertyDialog usbPropertyDialog =
                            new UsbPropertyDialog(MainActivity.this, mUsb0);
                        usbPropertyDialog.showDialog();
                    }
                    break;
            }
        }
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        clearNivagateFocus();
        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
            switch (view.getId()) {
               case R.id.tv_main_storage_one:
                   showPopWindow(USB_POPWINDOW_TAG, Constants.USB_ONE);
                   break;
               case R.id.tv_main_storage_two:
                   showPopWindow(USB_POPWINDOW_TAG, Constants.USB_TWO);
                   break;
               case R.id.tv_main_storage_three:
                   showPopWindow(USB_POPWINDOW_TAG, Constants.USB_THREE);
                   break;
            }
            return true;
        }
        return false;
    }

    class CustomFileObserver extends FileObserver {

        public CustomFileObserver(String path) {
            super(path);
        }
        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.CREATE:
                case FileObserver.DELETE:
                case FileObserver.MOVED_FROM:
                case FileObserver.MOVED_TO:
                case FileObserver.MODIFY:
                    mHandler.sendMessage(Message.obtain(mHandler, Constants.ONLY_REFRESH,
                                         ((BaseFragment) getVisibleFragment())
                                                       .mFileViewInteractionHub.getCurrentPath()));
                    break;
                default:
                    break;
            }
        }

    }
    private class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String sdfolder = getResources().getString(R.string.sd_folder);
            String path = editable.toString();
            if (path.contains(sdfolder)) {
                path = path.replace(sdfolder + "/", Constants.ROOT_PATH);
            }
            if (mCustomFileObserver != null) {
                mCustomFileObserver.stopWatching();
                mCustomFileObserver = null;
            }
            mCustomFileObserver = new CustomFileObserver(path);
            mCustomFileObserver.startWatching();
        }

    }
    public void clearNivagateFocus(){
        mEtSearchView.clearFocus();
        mEtMainFilePath.clearFocus();
    }

    @Override
    protected void onDestroy() {
        mReceiver.unregisterReceiver();
        if (mCustomFileObserver != null) {
            mCustomFileObserver.stopWatching();
            mCustomFileObserver = null;
        }
        super.onDestroy();
    }
}
