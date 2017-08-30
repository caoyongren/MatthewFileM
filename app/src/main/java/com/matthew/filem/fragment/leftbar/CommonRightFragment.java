package com.matthew.filem.fragment.leftbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.matthew.filem.R;
import com.matthew.filem.activity.FileManagerPreferenceActivity;
import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.adapter.CommonFileAdapter;
import com.matthew.filem.component.FrameSelectView;
import com.matthew.filem.fragment.base.BaseFragment;
import com.matthew.filem.impl.IFileInteractionListener;
import com.matthew.filem.info.FileInfo;
import com.matthew.filem.system.FileCategoryHelper;
import com.matthew.filem.system.FileIconHelper;
import com.matthew.filem.system.FileSortHelper;
import com.matthew.filem.system.FileViewInteractionHub;
import com.matthew.filem.system.Settings;
import com.matthew.filem.system.Util;
import com.matthew.filem.utils.Constants;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.T;
import com.matthew.filem.view.drag.DragGridView;
import com.matthew.filem.view.drag.DragListView;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/**
 * Author: MasterMan
 * Describe:
 *   Right layout show info to file.@inflateFileData
 *   对于文件的监听．
 * */
public class CommonRightFragment extends BaseFragment implements
        IFileInteractionListener, MainActivity.IBackPressedListener {
    private static final String TAG = "CommonRightFragment : DEBUG:";
    public static final String ROOT_DIRECTORY = "root_directory";
    private static final String sdDir = Util.getSdDirectory();
    private CommonFileAdapter mAdapter;
    private FileCategoryHelper mFileCagetoryHelper;
    private FileIconHelper mFileIconHelper;
    private ArrayList<FileInfo> mFileNameList = new ArrayList<>();
    private Activity mActivity;
    private MainActivity mMainActivity;
    private DragListView mFileListView;
    private DragGridView mFileGridView;
    private FrameLayout mFragmentSysFl;
    private String curRootDir = "";
    private String mouseRightTag = "mouse";
    private boolean isCtrlPress;
    private boolean isDialogShow = false;
    private boolean isShow = false;
    private boolean mIsLeftItem;

    // memorize the scroll positions of previous paths
    private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<>();
    private String mPreviousPath;
    private View mEmptyView;
    private View mNoSdView;
    private HashMap<Enum, Boolean> mSortMap;
    private boolean mSdCardReady;
    private int mPos = -1;

    private GridViewOnGenericMotionListener mGridMotionListener;
    private ListViewOnGenericMotionListener mListMotionListener;
    private FrameSelectView mFrameSelectView;
    private List<FileInfo> fileInfoList;
    private List<FileInfo> mFileListInfo;
    private int GRID_LEFT_POS = 0;
    private int GRID_TOP_POS = 1;
    private int GRID_WIDTH_POS = 2;
    private int GRID_SPACE_POS = 3;
    private int GRID_NUMCOLUMNS_POS = 4;
    private int ADAPTER_WIDTH_POS = 0;
    private int ADAPTER_HEIGHT_POS = 1;

    @SuppressLint({"NewApi", "ValidFragment"})
    public CommonRightFragment(String sdSpaceFragment, String directPath,
                               ArrayList<FileInfo> fileInfoList,
                               FileViewInteractionHub.CopyOrMove mCopyOrMove, boolean isLeftItem) {
        super(sdSpaceFragment,directPath,fileInfoList,mCopyOrMove);
        mIsLeftItem = isLeftItem;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public CommonRightFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_right_layout;
    }

    @Override
    protected void initView() {
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mFragmentSysFl = (FrameLayout) rootView.findViewById(R.id.fragment_sys_fl);
        mFrameSelectView = new FrameSelectView(mMainActivity);
        mFragmentSysFl.addView(mFrameSelectView);
        mEmptyView = rootView.findViewById(R.id.empty_view);
        mSdCardReady = Util.isSDCardReady();
        mNoSdView = rootView.findViewById(R.id.sd_not_available_page);
        mFileListView = (DragListView) rootView.findViewById(R.id.right_layout_list);
        mFileGridView = (DragGridView) rootView.findViewById(R.id.right_layout_grid);

        initSortMap(true);
    }

    @Override
    protected void initData() {
        mFileCagetoryHelper = new FileCategoryHelper(mActivity);
        mFileViewInteractionHub = new FileViewInteractionHub(this);
        Intent intent = getActivity().getIntent();
        //TODO  delete
        mFileIconHelper = new FileIconHelper(mActivity);
        mGridMotionListener = new GridViewOnGenericMotionListener();
        mListMotionListener = new ListViewOnGenericMotionListener();
        if (MainActivity.DEFAULT_VIEW_TAG_LIST.equals(LocalCacheLayout.getViewTag())) {
            addHeadView(mActivity);
            mAdapter = new CommonFileAdapter(mActivity, R.layout.file_browser_item_list,
                    mFileNameList, mFileViewInteractionHub,
                    mFileIconHelper, mListMotionListener);
        } else if (MainActivity.DEFAULT_VIEW_TAG_GRID.equals(LocalCacheLayout.getViewTag())) {
            mAdapter = new CommonFileAdapter(mActivity, R.layout.file_browser_item_grid,
                    mFileNameList, mFileViewInteractionHub,
                    mFileIconHelper, mGridMotionListener);
        }
        boolean baseSd = intent.getBooleanExtra(Constants.KEY_BASE_SD,
                !FileManagerPreferenceActivity.isReadRoot(mActivity));

        String rootDir = intent.getStringExtra(ROOT_DIRECTORY);
        if (!TextUtils.isEmpty(rootDir)) {
            if (baseSd && sdDir.startsWith(rootDir)) {
                rootDir = sdDir;
            }
        } else {
            rootDir = baseSd ? sdDir : Constants.ROOT_PATH;
        }
        mFileViewInteractionHub.setRootPath(rootDir);

        String currentDir = FileManagerPreferenceActivity.getPrimaryFolder
                (mActivity, sdOrSystem, directorPath);
        if (!mIsLeftItem) {
            mMainActivity.setCurPath(currentDir);
        }
        Uri uri = intent.getData();
        if (uri != null) {
            if (baseSd && sdDir.startsWith(uri.getPath())) {
                currentDir = sdDir;
            } else {
                currentDir = uri.getPath();
            }
        }
        mFileViewInteractionHub.setCurrentPath(currentDir);
        curRootDir = currentDir;
        inflateFileData();

        if (mFileInfoList != null && mFileInfoList.size() > 0) {
            mFileViewInteractionHub.setCheckedFileList(mFileInfoList, mCopyOrMove);
        }
        initRegisterBroadcast();
        updateUI();
        setHasOptionsMenu(true);
        mFileListInfo = mAdapter.getFileInfoList();
    }

    @Override
    protected void initListener() {
        mFileGridView.setOnTouchListener(mGridMotionListener);
        mFileListView.setOnTouchListener(mListMotionListener);
        /*mFileListView.setOnDragChangeListener(new DragListView.OnChanageListener() {
            @Override
            public void onChange(int from, int to) {
                FileInfo fileInfo = mFileViewInteractionHub.getItem(to);
                if (from != -1 && to != -1 && fileInfo.IsDir) {
                    mFileViewInteractionHub.addDragSelectedItem(from);
                    mFileViewInteractionHub.onOperationMove();
                    mFileViewInteractionHub.onOperationDragConfirm(fileInfo.filePath);
                    L.e("from____________to", from + "______________" + to);
                }
            }
        });
        mFileListView.setOnGenericMotionListener(new MouseListOnGenericMotionListener());

        mFileGridView.setOnDragChangeListener(new DragGridView.OnChanageListener() {
            @Override
            public void onChange(int from, int to) {
                FileInfo fileInfo = mFileViewInteractionHub.getItem(to);
                if (from != -1 && to != -1 && fileInfo.IsDir) {
                    mFileViewInteractionHub.addDragSelectedItem(from);
                    mFileViewInteractionHub.onOperationMove();
                    mFileViewInteractionHub.onOperationDragConfirm(fileInfo.filePath);
                    L.e("from____________to", from + "______________" + to);
                }
            }
        });
        mFileGridView.setOnGenericMotionListener(new MouseGridOnGenericMotionListener());
        mFileGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (isShow==true && isDialogShow == false) {
                            isShow = false;
                            isDialogShow = true;
                            mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub,
                                                                       event);
                            if (mAdapter.getSelectFileList().size() != 0) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                }
                return false;
            }
        });*/
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case MainActivity.INTENT_SWITCH_VIEW:
                    if (null != intent.getExtras().getString(MainActivity.KEY_SWITCH_VIEW)) {
                        String switch_view = intent.getExtras().getString(MainActivity.KEY_SWITCH_VIEW);
                        selectorMenuId(switch_view);
                    }
                    break;
                case "com.switchmenu":
                    if (null != intent.getExtras().getString("pop_menu")) {
                        String pop_menu = intent.getExtras().getString("pop_menu");
                        //selectorMenuId(pop_menu);
                    }
                    break;
                case "com.isCtrlPress":
                    isCtrlPress = intent.getExtras().getBoolean("is_ctrl_press");
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                case Intent.ACTION_MEDIA_UNMOUNTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                    break;
            }
        }
    };

    private void selectorMenuId(String tag) {
        if (mFileViewInteractionHub.getSelectedFileList() != null) {

        }
        switch (tag) {
            case "pop_refresh":
                mFileViewInteractionHub.onOperationReferesh();
                break;
            case "pop_cancel_all":
                mFileViewInteractionHub.onOperationSelectAllOrCancel();
                break;
            case "pop_copy":
                if (mFileViewInteractionHub.getSelectedFileList() != null) {
                    mFileViewInteractionHub.doOnOperationCopy();
                }
                T.showShort(mActivity, getString(R.string.select_file_to_copy));
                break;
            case "pop_delete":
                if (mFileViewInteractionHub.getSelectedFileList() != null) {
                    //mFileViewInteractionHub.onOperationDelete();
                }
                break;
            case "pop_send":
                if (mFileViewInteractionHub.getSelectedFileList() != null) {
                    mFileViewInteractionHub.onOperationSend();
                }
                T.showShort(mActivity, getString(R.string.select_file_to_send));
                break;
            case "pop_create":
                mFileViewInteractionHub.onOperationCreateFolder();
                break;
            case "view_or_dismiss":
                mFileViewInteractionHub.onOperationShowSysFiles();
                break;
            case "pop_cut":
                mFileViewInteractionHub.onOperationMove();
                break;
            case "pop_paste":
                mFileViewInteractionHub.onOperationButtonConfirm();
                break;
            case "pop_cacel":
                mFileViewInteractionHub.onOperationButtonCancel();
                break;
            case MainActivity.DEFAULT_VIEW_TAG_GRID:
            case MainActivity.DEFAULT_VIEW_TAG_LIST:
                switchMode();
                mFileViewInteractionHub.clearSelection();
                break;
            default:
                break;
        }
    }

    //将排序类型作为数据放入Ｍap集合．
    private void initSortMap(boolean flag) {
        mSortMap = new HashMap<>();
        mSortMap.put(FileSortHelper.SortStyle.name, flag);
        mSortMap.put(FileSortHelper.SortStyle.size, flag);
        mSortMap.put(FileSortHelper.SortStyle.date, flag);
        mSortMap.put(FileSortHelper.SortStyle.type, flag);
    }

    private void switchMode() {
     if (MainActivity.DEFAULT_VIEW_TAG_LIST.equals(LocalCacheLayout.getViewTag())) {
            addHeadView(mActivity);
            mAdapter = new CommonFileAdapter(mActivity, R.layout.file_browser_item_list,
                                           mFileNameList, mFileViewInteractionHub,
                                           mFileIconHelper, mListMotionListener);
        } else if (MainActivity.DEFAULT_VIEW_TAG_GRID.equals(LocalCacheLayout.getViewTag())) {
            mAdapter = new CommonFileAdapter(mActivity, R.layout.file_browser_item_grid,
                                           mFileNameList, mFileViewInteractionHub,
                                           mFileIconHelper, mGridMotionListener);
        }
        inflateFileData();
    }

    private void addHeadView(Context context) {
        if (mFileListView.getHeaderViewsCount() == 0) {
            View headView =
                    LayoutInflater.from(context).inflate(R.layout.file_browser_item_list, null);
            ImageView lFileImage = (ImageView) headView.findViewById(R.id.file_image);
            Util.setText(headView, R.id.file_name,
                    context.getResources().getString(R.string.file_title_name),
                    context.getResources().getColor(R.color.file_title_color));
            Util.setText(headView, R.id.file_count,
                    context.getResources().getString(R.string.file_title_type),
                    context.getResources().getColor(R.color.file_title_color));
            Util.setText(headView, R.id.modified_time,
                    context.getResources().getString(R.string.file_title_modified),
                    context.getResources().getColor(R.color.file_title_color));
            Util.setText(headView, R.id.file_size,
                    context.getResources().getString(R.string.file_title_size),
                    context.getResources().getColor(R.color.file_title_color));
            lFileImage.setVisibility(View.GONE);
            mFileListView.addHeaderView(headView);
        }
    }

    //对于网格视图文件监听．
    public class GridViewOnGenericMotionListener implements View.OnTouchListener {
        private boolean mIsShowDialog = false;
        private boolean mIsItem = false;
        private List<Integer> integerList = new ArrayList<>();
        private float mDownX = -1;
        private float mDownY, mMoveX, mMoveY;
        private boolean isMove;
        private List<Integer> list = new ArrayList<>();
        private FileInfo info;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mMainActivity.clearNivagateFocus();
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    integerList = mAdapter.getSelectFileList();
                    calculateFileLocation(mFileGridView.getVerticalScrollDistance());
                    if (view.getTag() instanceof CommonFileAdapter.ViewHolder
                            || view.getId() == R.id.file_name) {
                            mDownX = -1;
                            mIsItem = true;
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            mIsShowDialog = true;
                        }
                        if (view.getId() == R.id.file_name) {
                            mPos = (int) view.getTag();
                        } else {
                            mPos = (int) ((CommonFileAdapter.ViewHolder) view.getTag()).name.getTag();
                        }
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                            mouseRightTag = "button_primary";
                        }
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            mouseRightTag = "button_secondary";
                        }
                        FileInfo fileInfo = mAdapter.getFileInfoList().get(mPos);
                        if (!isCtrlPress && "button_primary".equals(mouseRightTag)
                                && mLastClickId == mPos
                                && (Math.abs(System.currentTimeMillis() - mLastClickTime)
                                < Constants.DOUBLE_CLICK_INTERVAL_TIME)) {
                            mFileViewInteractionHub.onListItemClick(mPos,
                                    Constants.DOUBLE_TAG, motionEvent, fileInfo);
                            mPos = -1;
                            mLastClickId = -1;
                            integerList.clear();
                            mFileViewInteractionHub.clearSelection();
                        } else {
                            mLastClickTime = System.currentTimeMillis();
                            mLastClickId = mPos;
                        }
                        return true;
                    } else {
                        mPos = -1;
                        mDownX = motionEvent.getX();
                        mDownY = motionEvent.getY();
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            mIsShowDialog = true;
                        }
                        mIsItem = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        return true;
                    }
                    if (mDownX != -1 && !mIsItem) {
                        isMove = true;
                        mFrameSelectView.setVisibility(View.VISIBLE);
                        mMoveX = motionEvent.getX();
                        mMoveY = motionEvent.getY();
                        mFrameSelectView.setPositionCoordinate(mDownX < mMoveX? mDownX : mMoveX,
                                mDownY < mMoveY? mDownY : mMoveY,
                                mDownX > mMoveX? mDownX : mMoveX, mDownY > mMoveY? mDownY : mMoveY);
                        mFrameSelectView.invalidate();
                        int i;
                        integerList.clear();
                        for (i = 0; i < mFileListInfo.size(); i++) {
                            info = mFileListInfo.get(i);
                            if (frameSelectionJudge(info, mDownX, mDownY, mMoveX, mMoveY)) {
                                info.Selected = true;
                                integerList.add(i);
                            }
                        }
                        if (!(list.containsAll(integerList) && list.size() == integerList.size())) {
                            mAdapter.notifyDataSetChanged();
                        }
                        list.clear();
                        list.addAll(integerList);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    mFrameSelectView.setVisibility(View.INVISIBLE);
                    FileInfo fileInfo = null;
                    if (mPos != -1) {
                         fileInfo = mAdapter.getFileInfoList().get(mPos);
                        fileInfo.Selected = true;
                        if (isCtrlPress) {
                            if (!integerList.contains(mPos)) {
                                integerList.add(mPos);
                                mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                            } else {
                                integerList.remove(new Integer(mPos));
                                mFileViewInteractionHub.removeDialogSelectedItem(fileInfo);
                            }
                        } else if (!(mouseRightTag == "button_secondary"
                                && integerList.contains(mPos))) {
                            integerList.clear();
                            integerList.add(mPos);
                            mFileViewInteractionHub.clearSelection();
                            mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                        }
                    } else {
                        integerList.clear();
                        mFileViewInteractionHub.clearSelection();
                    }
                    float upX = motionEvent.getX();
                    float upY = motionEvent.getY();
                    if (isMove) {
                        isMove = false;
                        FileInfo info;
                        for (int i = 0; i < mFileListInfo.size(); i++) {
                            info = mFileListInfo.get(i);
                            if (frameSelectionJudge(info, mDownX, mDownY, upX, upY)) {
                                info.Selected = true;
                                if (!integerList.contains(i)) {
                                    integerList.add(i);
                                    mFileViewInteractionHub.addDialogSelectedItem(info);
                                } else {
                                    integerList.remove(new Integer(i));
                                    mFileViewInteractionHub.removeDialogSelectedItem(info);
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mouseRightTag = "mouse";

                    if (mIsShowDialog == true) {
                         int compressFileState = Constants.COMPRESSIBLE;
                         if (mIsItem) {
                            boolean isDirectory = true;
                            if (fileInfo != null) {
                                File file = new File(fileInfo.filePath);
                                isDirectory = file.isDirectory() ? true : false;
                                compressFileState = Util.getCompressFileState(fileInfo.filePath);
                            }
                            mFileViewInteractionHub.setIsBlank(false);
                            mFileViewInteractionHub.setIsDirectory(isDirectory);
                        } else {
                            mFileViewInteractionHub.setIsBlank(true);
                            mFileViewInteractionHub.setIsDirectory(true);
                        }
                        mFileViewInteractionHub.setCompressFileState(compressFileState);
                        mFileViewInteractionHub.showContextDialog(mFileViewInteractionHub,
                                motionEvent);
                        mIsShowDialog = false;
                    }
                    mDownX = -1;
            }
            return false;
        }
    }

    //对于列表视图文件监听．
    public class ListViewOnGenericMotionListener implements View.OnTouchListener {
        private boolean mIsShowDialog = false;
        private boolean mIsItem = false;
        private List<Integer> integerList;
        private FileInfo fileInfo;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    integerList = mAdapter.getSelectFileList();
                    if (view.getTag() instanceof CommonFileAdapter.ViewHolder) {
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            mIsShowDialog = true;
                            mIsItem = true;
                        }
                        mPos = (int) ((CommonFileAdapter.ViewHolder) view.getTag()).name.getTag();
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                            mouseRightTag = "button_primary";
                        }
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            mouseRightTag = "button_secondary";
                        }
                        fileInfo = mAdapter.getFileInfoList().get(mPos);
                        if (!isCtrlPress && "button_primary".equals(mouseRightTag)
                                && mLastClickId == mPos
                                && (Math.abs(System.currentTimeMillis() - mLastClickTime)
                                < Constants.DOUBLE_CLICK_INTERVAL_TIME)) {
                            mFileViewInteractionHub.onListItemClick(mPos,
                                    Constants.DOUBLE_TAG, motionEvent, fileInfo);
                            mPos = -1;
                            mLastClickId = -1;
                            integerList.clear();
                            mFileViewInteractionHub.clearSelection();
                        } else {
                            mLastClickTime = System.currentTimeMillis();
                            mLastClickId = mPos;
                        }
                        return true;
                    } else {
                        mPos = -1;
                        if (motionEvent.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            mIsShowDialog = true;
                            mIsItem = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (mPos != -1) {
                        fileInfo = mAdapter.getFileInfoList().get(mPos);
                        fileInfo.Selected = true;
                        if (isCtrlPress) {
                            if (!integerList.contains(mPos)) {
                                integerList.add(mPos);
                                mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                            } else {
                                integerList.remove(new Integer(mPos));
                                mFileViewInteractionHub.removeDialogSelectedItem(fileInfo);
                            }
                        } else if (mouseRightTag != "button_secondary"
                                || !integerList.contains(mPos)) {
                            integerList.clear();
                            integerList.add(mPos);
                            mFileViewInteractionHub.clearSelection();
                            mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                        }
                    } else {
                        integerList.clear();
                        mFileViewInteractionHub.clearSelection();
                    }
                    mAdapter.notifyDataSetChanged();
                    mouseRightTag = "mouse";

                    if (mIsShowDialog == true) {
                        int compressFileState = Constants.COMPRESSIBLE;
                        if (mIsItem) {
                            if (fileInfo != null) {
                                File file = new File(fileInfo.filePath);
                                compressFileState = Util.getCompressFileState(fileInfo.filePath);
                                mFileViewInteractionHub.setIsDirectory(file.isDirectory());
                            }
                            mFileViewInteractionHub.setIsBlank(false);
                        } else {
                            mFileViewInteractionHub.setIsBlank(true);
                            mFileViewInteractionHub.setIsDirectory(true);
                        }
                        mFileViewInteractionHub.setCompressFileState(compressFileState);
                        mFileViewInteractionHub.showContextDialog(mFileViewInteractionHub,
                                motionEvent);
                        mIsShowDialog = false;
                    }
            }
            return false;
        }
    }

    private boolean frameSelectionJudge(FileInfo info, float downX, float downY,
                                                       float toX, float toY) {
        return (((info.left >= Math.min(downX, toX) && info.left <= Math.max(downX, toX))
              || (info.right >= Math.min(downX, toX) && info.right <= Math.max(downX, toX)))
              && ((info.top >= Math.min(downY, toY) && info.top <= Math.max(downY, toY))
              || (info.bottom >= Math.min(downY, toY) && info.bottom <= Math.max(downY, toY))))
              || (((info.left <= Math.min(downX, toX) && info.right >= Math.max(downX, toX))
              && ((info.top >= Math.min(downY, toY) && info.top <= Math.max(downY, toY))
              || (info.bottom >= Math.min(downY, toY) && info.bottom <= Math.max(downY, toY))))
              || ((info.top <= Math.min(downY, toY) && info.bottom >= Math.max(downY, toY))
              && ((info.left >= Math.min(downX, toX) && info.left <= Math.max(downX, toX))
              || (info.right >= Math.min(downX, toX) && info.right <= Math.max(downX, toX)))));
    }

    //根据不同的视图填充数据．
    private void inflateFileData() {
        if (MainActivity.DEFAULT_VIEW_TAG_LIST.equals(LocalCacheLayout.getViewTag())) {
            mFileGridView.setVisibility(View.GONE);
            mFileListView.setVisibility(View.VISIBLE);
            mFileListView.setAdapter(mAdapter);
        } else if (MainActivity.DEFAULT_VIEW_TAG_GRID.equals(LocalCacheLayout.getViewTag())) {
            mFileListView.setVisibility(View.GONE);
            mFileGridView.setVisibility(View.VISIBLE);
            mFileGridView.setAdapter(mAdapter);
        }
    }

    private void initRegisterBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.switchview");
        intentFilter.addAction("com.switchmenu");
        intentFilter.addAction("com.isTouchEvent");
        intentFilter.addAction("com.isCtrlPress");
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mActivity.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        mActivity.unregisterReceiver(mReceiver);
        mAdapter.dispose();
        super.onDestroyView();
    }

    public boolean onBack() {
        if (!Util.isSDCardReady() || mFileViewInteractionHub == null) {
            return false;
        }
        return mFileViewInteractionHub.onBackPressed();
    }

    private class PathScrollPositionItem {
        String path;
        int pos;

        PathScrollPositionItem(String s, int p) {
            path = s;
            pos = p;
        }
    }

    // execute before change, return the memorized scroll position
    private int computeScrollPosition(String path) {
        int pos = 0;
        if (mPreviousPath != null) {
            if (path.startsWith(mPreviousPath)) {
                int firstVisiblePosition = 0;
                if ("list".equals(LocalCacheLayout.getViewTag())) {
                    firstVisiblePosition = mFileListView.getFirstVisiblePosition();
                } else if ("grid".equals(LocalCacheLayout.getViewTag())) {
                    firstVisiblePosition = mFileGridView.getFirstVisiblePosition();
                }
                if (mScrollPositionList.size() != 0
                    && mPreviousPath.equals(mScrollPositionList
                                    .get(mScrollPositionList.size() - 1).path)) {
                    mScrollPositionList.get(mScrollPositionList.size() - 1).pos
                    = firstVisiblePosition;
                    pos = firstVisiblePosition;
                } else {
                    mScrollPositionList.add(new PathScrollPositionItem(mPreviousPath,
                                                                       firstVisiblePosition));
                }
            } else {
                int i;
                for (i = 0; i < mScrollPositionList.size(); i++) {
                    if (!path.startsWith(mScrollPositionList.get(i).path)) {
                        break;
                    }
                }
                // navigate to a totally new branch, not in current stack
                if (i > 0) {
                    pos = mScrollPositionList.get(i - 1).pos;
                }

                for (int j = mScrollPositionList.size() - 1; j >= i - 1 && j >= 0; j--) {
                    mScrollPositionList.remove(j);
                }
            }
        }
        mPreviousPath = path;
        return pos;
    }

    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        final int pos = computeScrollPosition(path);
        ArrayList<FileInfo> fileList = mFileNameList;
        fileList.clear();

        File[] listFiles = file.listFiles(mFileCagetoryHelper.getFilter());
        if (listFiles == null)
            return true;

        for (File child : listFiles) {
            // do not show selected file if in move state
            if (mFileViewInteractionHub.isMoveState()
                && mFileViewInteractionHub.isFileSelected(child.getPath()))
                continue;

            String absolutePath = child.getAbsolutePath();
            if (Util.isNormalFile(absolutePath) && Util.shouldShowFile(absolutePath)) {
                FileInfo lFileInfo = Util.GetFileInfo(child, mFileCagetoryHelper.getFilter(),
                                     Settings.instance().getShowDotAndHiddenFiles());
                if (lFileInfo != null) {
                    fileList.add(lFileInfo);
                }
            }
        }

        sortCurrentList(sort);
        showEmptyView(fileList.size() == 0);
        if ("list".equals(LocalCacheLayout.getViewTag())) {
            mFileListView.post(new Runnable() {
                @Override
                public void run() {
                    mFileListView.setSelection(pos);
                }
            });
        } else if ("grid".equals(LocalCacheLayout.getViewTag())) {
            mFileGridView.post(new Runnable() {
                @Override
                public void run() {
                    mFileGridView.setSelection(pos);
                }
            });
        }
        mAdapter.getSelectFileList().clear();
        mAdapter.notifyDataSetChanged();
        return true;
    }

    private void updateUI() {
        mNoSdView.setVisibility(mSdCardReady ? View.GONE : View.VISIBLE);
        if ("list".equals(LocalCacheLayout.getViewTag())) {
            mFileListView.setVisibility(mSdCardReady ? View.VISIBLE : View.GONE);
        } else if ("grid".equals(LocalCacheLayout.getViewTag())) {
            mFileGridView.setVisibility(mSdCardReady ? View.VISIBLE : View.GONE);
        }

        if (mSdCardReady) {
            mFileViewInteractionHub.refreshFileList();
        }
    }

    private void showEmptyView(boolean show) {
        View mEmptyView = rootView.findViewById(R.id.empty_view);
        if (mEmptyView != null)
            mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public View getViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void onDataChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPick(FileInfo f) {
        try {
            Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath)).toString(), 0);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldShowOperationPane() {
        return true;
    }

    @Override
    public boolean onOperation(int id) {
        return false;
    }

    @Override
    public String getDisplayPath(String path) {
        if (path.startsWith(this.sdDir)
            && !FileManagerPreferenceActivity.showRealPath(mActivity)) {
            return getString(R.string.sd_folder) + path.substring(this.sdDir.length());
        } else {
            return path;
        }
    }

    @Override
    public String getRealPath(String displayPath) {
        final String perfixName = getString(R.string.sd_folder);
        if (displayPath.startsWith(perfixName)) {
            return sdDir + displayPath.substring(perfixName.length());
        } else {
            return displayPath;
        }
    }

    @Override
    public boolean onNavigation(String path) {
        return false;
    }

    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }
    //TODO  copyFile

    public interface SelectFilesCallback {
        // files equals null indicates canceled
        void selected(ArrayList<FileInfo> files);
    }

    @Override
    public FileIconHelper getFileIconHelper() {
        return mFileIconHelper;
    }

    public boolean setPath(String location) {
        if (mFileViewInteractionHub != null) {
            if (!location.startsWith(mFileViewInteractionHub.getRootPath())) {
                return false;
            }
            mFileViewInteractionHub.setCurrentPath(location);
            mFileViewInteractionHub.refreshFileList();
        }
        return true;
    }

    @Override
    public FileInfo getItem(int pos) {
        if (pos < 0 || pos > mFileNameList.size() - 1)
            return null;
        return mFileNameList.get(pos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sortCurrentList(FileSortHelper sort) {
        if (mSortMap.get(sort.getSortStyle())) {
            Collections.sort(mFileNameList, sort.getComparator());
        } else {
            Collections.sort(mFileNameList, Collections.reverseOrder(sort.getComparator()));
        }
        onDataChanged();
    }

    @Override
    public ArrayList<FileInfo> getAllFiles() {
        return mFileNameList;
    }

    @Override
    public void addSingleFile(FileInfo file) {
        mFileNameList.add(file);
        onDataChanged();
    }

    @Override
    public int getItemCount() {
        return mFileNameList.size();
    }

    @Override
    public void runOnUiThread(Runnable r) {
        mMainActivity.runOnUiThread(r);
    }

    public boolean canGoBack() {
        String currentPath = mFileViewInteractionHub.getCurrentPath();
        return !currentPath.trim().equals(curRootDir.trim());
    }

    public void goBack() {
        mAdapter.getSelectFileList().clear();
        mFileViewInteractionHub.clearSelection();
        mFileViewInteractionHub.onBackPressed();
    }

    public ArrayList<FileInfo> getFileInfoList() {
        return mFileViewInteractionHub.getCheckedFileList();
    }

    public FileViewInteractionHub.CopyOrMove getCurCopyOrMoveMode() {
        return mFileViewInteractionHub.getCurCopyOrMoveMode();
    }

    private int mLastClickId;
    private long mLastClickTime = 0;

/*    public class OnitemClickListener implements AdapterView.OnItemClickListener {
        MotionEvent motionEvent;
        public OnitemClickListener(MotionEvent event) {
            motionEvent = event;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mFileGridView.setIsBlankArea(false);
            FileInfo fileInfo = mAdapter.getFileInfoList().get(position);
            fileInfo.Selected = true;
            List<Integer> integerList = mAdapter.getSelectFileList();
            if (isCtrlPress) {
                if (integerList.contains(position)) {
                    integerList.remove(new Integer(position));
                    mFileViewInteractionHub.removeDialogSelectedItem(fileInfo);
                } else {
                    integerList.add(position);
                    mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                }
            } else {
                if ("button_primary".equals(mouseRightTag) && mLastClickId == position
                        && (Math.abs(System.currentTimeMillis() - mLastClickTime)
                            < Constants.DOUBLE_CLICK_INTERVAL_TIME)) {
                    mFileViewInteractionHub.onListItemClick(position,
                                            Constants.DOUBLE_TAG, motionEvent, fileInfo);
                    integerList.clear();
                    mFileViewInteractionHub.clearSelection();
                } else if (mouseRightTag.equals("button_secondary") && isDialogShow == false) {
                    mouseRightTag = "mouse";
                    isDialogShow = true;
                } else {
                    mLastClickTime = System.currentTimeMillis();
                    mLastClickId = position;
                    integerList.clear();
                    mFileViewInteractionHub.clearSelection();
                    integerList.add(position);
                    mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                    mouseRightTag = "mouse";
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }*/

    public String getCurrentPath() {
        return mFileViewInteractionHub.getCurrentPath();
    }

    public void refreshUI() {
        mFileViewInteractionHub.refreshFileList();
    }

    @Override
    public void enter() {
        mFileViewInteractionHub.onOperationOpen(null);
    }

    @Override
    protected void enter(String tag, String path) {
    }

    public CommonFileAdapter getAdapter() {
        return mAdapter;
    }

    public FileViewInteractionHub getFileViewInteractionHub() {
        return mFileViewInteractionHub;
    }

    public void setSortTag(Enum sort, boolean positive) {
        mSortMap.put(sort, positive);
    }

    public boolean getSortTag(Enum sort) {
        return mSortMap.get(sort);
    }

    public void calculateFileLocation(int fixY) {
        int[] gridViewParams = mFileGridView.getParams();
        int[] itemParams = mAdapter.getParams();
        for (int i = 0; i < mFileListInfo.size(); i++) {
            mFileListInfo.get(i).left = gridViewParams[GRID_LEFT_POS]
                    + (i % gridViewParams[GRID_NUMCOLUMNS_POS]) * (gridViewParams[GRID_WIDTH_POS]);
            mFileListInfo.get(i).top = gridViewParams[GRID_TOP_POS]
                    + (i / gridViewParams[GRID_NUMCOLUMNS_POS])
                    * (itemParams[ADAPTER_HEIGHT_POS] + gridViewParams[GRID_SPACE_POS]) - fixY;
            mFileListInfo.get(i).right = gridViewParams[GRID_LEFT_POS]
                    + itemParams[ADAPTER_WIDTH_POS]
                    + (i % gridViewParams[GRID_NUMCOLUMNS_POS]) * (gridViewParams[GRID_WIDTH_POS]);
            mFileListInfo.get(i).bottom = gridViewParams[GRID_TOP_POS]
                    + itemParams[ADAPTER_HEIGHT_POS] + (i / gridViewParams[GRID_NUMCOLUMNS_POS])
                    * (itemParams[ADAPTER_HEIGHT_POS] + gridViewParams[GRID_SPACE_POS]) - fixY;
        }
    }
}
