package com.matthew.filem.view.fragment.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matthew.filem.R;
import com.matthew.filem.impl.UiInterface;
import com.matthew.filem.view.activity.MainActivity;
import com.matthew.filem.model.ImageInfo;
import com.matthew.filem.model.SearchInfo;
import com.matthew.filem.utils.Constants;
import com.matthew.filem.model.FileInfo;
import com.matthew.filem.system.FileViewInteractionHub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseFragment extends Fragment implements UiInterface {

    public View rootView;
    public FragmentManager mManager;
    public String usbDeviceIsAttached;
    public Context context;
    public MainActivity mMainActivity;
    public HashMap<String, List<String>> mGruopMap;
    public List<ImageInfo> list;
    public int index;
    public int mCurId;

    public ArrayList<SearchInfo> mSearchList = new ArrayList<>();
    public String sdOrSystem;
    public String directorPath;
    public ArrayList<FileInfo> mFileInfoList;
    public FileViewInteractionHub.CopyOrMove mCopyOrMove;
    public FileViewInteractionHub mFileViewInteractionHub;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                                      @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(),container,false);
        mManager = getFragmentManager();
        mMainActivity = (MainActivity) getActivity();

        initView();
        initData();
        initListener();
        return rootView;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public BaseFragment(FragmentManager manager, String usbDeviceIsAttached, MainActivity context) {
        mManager = manager;
        this.usbDeviceIsAttached = usbDeviceIsAttached;
        this.context = context;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public BaseFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public BaseFragment(HashMap<String, List<String>> mGruopMap, List<ImageInfo> list, int index) {
        this.mGruopMap = mGruopMap;
        this.list = list;
        this.index = index;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public BaseFragment(FragmentManager manager) {
        mManager = manager;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public BaseFragment(FragmentManager manager, ArrayList<SearchInfo> mFileList) {
        this.mSearchList = mFileList;
        mManager = manager;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public BaseFragment(String sdSpaceFragment, String directPath, ArrayList<FileInfo> fileInfoList,
                                                FileViewInteractionHub.CopyOrMove copyOrMove) {
        this.sdOrSystem = sdSpaceFragment;
        mFileInfoList = fileInfoList;
        mCopyOrMove = copyOrMove;
        this.directorPath = directPath;
    }

    public void enter() {
        switch (mCurId) {
            case R.id.rl_system_computer_fg:
                enter(Constants.SYSTEM_SPACE_FRAGMENT, null);
                break;
            case R.id.rl_disk_computer_fg:
                enter(Constants.SD_SPACE_FRAGMENT, Constants.SD_PATH);
                break;
            case R.id.rl_cloud_service_computer_fg:
                enter(Constants.YUN_SPACE_FRAGMENT, null);
                break;
            case R.id.rl_personal_space_computer_fg:
                enter(Constants.PERSONAL_TAG, null);
                break;
            case R.id.ll_personal_videos:
                enter(Constants.LEFT_FAVORITES, Constants.VIDEOS_PATH);
                break;
            case R.id.ll_personal_pictures:
                enter(Constants.LEFT_FAVORITES, Constants.PICTURES_PATH);
                break;
            case R.id.ll_personal_document:
                enter(Constants.LEFT_FAVORITES, Constants.DOCUMENT_PATH);
                break;
            case R.id.ll_personal_downloads:
                enter(Constants.LEFT_FAVORITES, Constants.DOWNLOAD_PATH);
                break;
            case R.id.ll_personal_music:
                enter(Constants.LEFT_FAVORITES, Constants.MUSIC_PATH);
                break;
            case R.id.ll_personal_recycle:
                enter(Constants.LEFT_FAVORITES, Constants.RECYCLE_PATH);
                break;
            case R.id.ll_personal_qq_image:
                enter(Constants.LEFT_FAVORITES, Constants.QQ_IMAGE_PATH);
                break;
            case R.id.ll_personal_qq_file:
                enter(Constants.LEFT_FAVORITES, Constants.QQ_FILE_PATH);
                break;
            case R.id.ll_personal_weixin:
                enter(Constants.LEFT_FAVORITES, Constants.WEIXIN_IMG_PATH);
                break;
            case R.id.ll_personal_baidudisk:
                enter(Constants.LEFT_FAVORITES, Constants.BAIDU_PAN_PATH);
                break;

        }
    }

    protected abstract void enter(String tag, String path);

    public abstract int getLayoutId();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initListener();
}
