package com.matthew.filem.view.fragment.computer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.matthew.filem.view.fragment.base.BaseFragment;
import com.matthew.filem.R;
import com.matthew.filem.adapter.PersonalAdapter;
import com.matthew.filem.view.fragment.leftbar.CommonRightFragment;
import com.matthew.filem.view.drag.DragGridView;
import com.matthew.filem.utils.Constants;
import com.matthew.filem.model.FileInfo;
import com.matthew.filem.system.FileViewInteractionHub;
import com.matthew.filem.utils.T;

import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonalDiskFragment extends BaseFragment {
    private DragGridView mPersonalGrid;
    private List<String> mPersonalList;
    private PersonalAdapter mPersonalAdaper;
    private LinkedHashMap<String, String> mPathMap;
    private Context mContext;
    private long mCurrentBackTime;
    private double mLastBackTime;
    public Fragment mCurFragment;
    private GridViewOnGenericMotionListener mMotionListener;
    ArrayList<FileInfo> mFileInfoArrayList = null;
    FileViewInteractionHub.CopyOrMove mCopyOrMove = null;

    @Override
    protected void initData() {
        mContext = getActivity();
        mPersonalList = new ArrayList<>();
        mMotionListener = new GridViewOnGenericMotionListener();
        mPathMap = new LinkedHashMap<>();
        mPathMap.put(getString(R.string.video), Constants.VIDEOS_PATH);
        mPathMap.put(getString(R.string.picture), Constants.PICTURES_PATH);
        mPathMap.put(getString(R.string.docement), Constants.DOCUMENT_PATH);
        mPathMap.put(getString(R.string.downloads), Constants.DOWNLOAD_PATH);
        mPathMap.put(getString(R.string.music), Constants.MUSIC_PATH);
        mPathMap.put(getString(R.string.qq_image), Constants.QQ_IMAGE_PATH);
        mPathMap.put(getString(R.string.qq_file), Constants.QQ_FILE_PATH);
        mPathMap.put(getString(R.string.winxin), Constants.WEIXIN_IMG_PATH);
        mPathMap.put(getString(R.string.baidu_disk), Constants.BAIDU_PAN_PATH);
        Iterator iterator = mPathMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String path = (String) entry.getValue();
            File file = new File(path);
            if (file.exists()) {
                mPersonalList.add((String) entry.getKey());
            }
        }
        mPersonalAdaper = new PersonalAdapter(mContext, mPersonalList, mMotionListener);
        mPersonalGrid.setAdapter(mPersonalAdaper);
    }

    @Override
    protected void initListener() {
        mPersonalGrid.setOnGenericMotionListener(mMotionListener);
    }

    @Override
    protected void initView() {
        mPersonalGrid = (DragGridView) rootView.findViewById(R.id.personal_fragment_grid);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_personal;

    }

    @Override
    public boolean canGoBack() {
        boolean canGoBack = false;
        Fragment baseFragment = mCurFragment;
        if (baseFragment instanceof CommonRightFragment) {
            CommonRightFragment commonRightFragment = (CommonRightFragment) baseFragment;
            canGoBack = commonRightFragment.canGoBack();
        }
        return canGoBack;
    }

    @Override
    public void goBack() {
        Fragment baseFragment = mCurFragment;
        if (baseFragment instanceof CommonRightFragment) {
            CommonRightFragment commonRightFragment = (CommonRightFragment) baseFragment;
            commonRightFragment.goBack();
        }
    }

    public class GridViewOnGenericMotionListener implements View.OnGenericMotionListener {
        List<Integer> integerList;
        @Override
        public boolean onGenericMotion(View v, MotionEvent event) {
            mMainActivity.clearNivagateFocus();
            integerList = mPersonalAdaper.getSelectFileInfoList();
            switch (event.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:
                    if (v.getTag() instanceof PersonalAdapter.ViewHolder) {
                        int pos = (int) ((PersonalAdapter.ViewHolder) v.getTag()).name.getTag();
                        if (integerList.contains(pos)) {
                            integerList.clear();
                        }else {
                            integerList.clear();
                            integerList.add(pos);
                        }
                        mCurrentBackTime = System.currentTimeMillis();
                        setDiskClickInfo(Constants.LEFT_FAVORITES, pos);
                        mPersonalAdaper.notifyDataSetChanged();
                    } else {
                        integerList.clear();
                    }
                    mPersonalAdaper.notifyDataSetChanged();
                    break;
                case MotionEvent.BUTTON_SECONDARY:
                    break;
                case MotionEvent.BUTTON_TERTIARY:
                    break;
                case MotionEvent.ACTION_SCROLL:
                    break;
                case MotionEvent.ACTION_HOVER_ENTER:
                    break;
            }
            return true;
        }
    }

    private void setDiskClickInfo(String tag, int id) {
        if (mCurrentBackTime - mLastBackTime > Constants.DOUBLE_CLICK_INTERVAL_TIME
                || id != mCurId) {
            mCurId = id;
            mLastBackTime = mCurrentBackTime;
        } else {
            enter(tag, mPathMap.get(mPersonalList.get(id)));
        }
    }

    @Override
    protected void enter(String tag, String path) {
        if (mCurFragment != null) {
            mFileInfoArrayList = ((CommonRightFragment) mCurFragment).getFileInfoList();
            mCopyOrMove = ((CommonRightFragment) mCurFragment).getCurCopyOrMoveMode();
        }
        if (mFileInfoArrayList != null && mCopyOrMove != null) {
            T.showShort(context,
                    context.getString(R.string.operation_failed_permission_refuse));
        }
        mCurFragment = new CommonRightFragment(tag, path, mFileInfoArrayList, mCopyOrMove, false);
        FragmentTransaction transaction = mManager.beginTransaction();
        transaction.hide(mMainActivity.mCurFragment);
        transaction.add(R.id.framelayout_right_main, mCurFragment, Constants.PERSONALSYSTEMSPACE_TAG).commit();
        mMainActivity.mCurFragment = mCurFragment;
    }
}
