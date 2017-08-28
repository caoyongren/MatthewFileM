package com.matthew.filem.fragment.leftbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.matthew.filem.BaseFragment;
import com.matthew.filem.R;
import com.matthew.filem.adapter.GroupAdapter;
import com.matthew.filem.bean.ImageBean;
import com.matthew.filem.fragment.DetailFragment;
import com.matthew.filem.system.Util;
import com.matthew.filem.system.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class PictrueFragment extends BaseFragment {
    private static final int SCAN_OK = 1;
    private GridView mGridViewPicture;
    private TextView mTvNoPictrue;
    private GroupAdapter mGroupAdapter;
    private ProgressDialog mProgressDialog;
    private ArrayList<ImageBean> mImageBeenList = new ArrayList<>();
    private HashMap<String, List<String>> mGruopMap = new HashMap<>();
    private ContentResolver mContentResolver;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    mImageBeenList = subGroupOfImage(mGruopMap);
                    if (null != mImageBeenList) {
                        mGroupAdapter = new GroupAdapter(getActivity(), mImageBeenList, mGridViewPicture);
                    } else {
                        mTvNoPictrue.setVisibility(View.VISIBLE);
                    }
                    if (mGroupAdapter != null) {
                        mGridViewPicture.setAdapter(mGroupAdapter);
                    }
                    mHandler.removeCallbacksAndMessages(null);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint({"NewApi", "ValidFragment"})
    public PictrueFragment(FragmentManager mManager) {
        super(mManager);
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public PictrueFragment() {
        super();
    }

    @Override
    public int getLayoutId() {
        return R.layout.pictrue_fragment_layout;
    }

    @Override
    protected void initView() {
        mGridViewPicture = (GridView) rootView.findViewById(R.id.gv_pictrue);
        mTvNoPictrue = (TextView) rootView.findViewById(R.id.tv_no_pictrue);
    }

    protected void initData() {
        if (null != mImageBeenList) {
            mImageBeenList.clear();
        }
        getImages();
    }

    @Override
    protected void initListener() {
        mGridViewPicture.setOnItemClickListener(new FolderOnItemClickListener());
    }

    private class FolderOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            DetailFragment fragment = new DetailFragment(mGruopMap, mImageBeenList, i);
            mManager.beginTransaction().hide(mMainActivity.mCurFragment).commit();
            mManager.beginTransaction().add(R.id.framelayout_right_mian, fragment, Constants.DETAILFRAGMENT_TAG)
                                           .addToBackStack(null).commit();
        }
    }

    private ArrayList<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : mGruopMap.entrySet()) {
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));
            mImageBeenList.add(mImageBean);
        }
        return mImageBeenList;
    }

    private void getImages() {
        if (!Util.isSDCardReady()) {
            Toast.makeText(getActivity(),
                           getString(R.string.external_storage_not_exist), LENGTH_SHORT).show();
            return;
        }

        mContentResolver = getActivity().getContentResolver();
        if (mContentResolver != null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "loading...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    Cursor mCursor = mContentResolver.query(mImageUri, null,
                            MediaStore.Images.Media.MIME_TYPE + "=? or "
                                    + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"},
                            MediaStore.Images.Media.DATE_MODIFIED);

                    assert mCursor != null;
                    while (mCursor.moveToNext()) {
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        String parentName = new File(path).getParentFile().getName();
                        if (!mGruopMap.containsKey(parentName)) {
                            List<String> chileList = new ArrayList<>();
                            chileList.add(path);
                            mGruopMap.put(parentName, chileList);
                        } else {
                            mGruopMap.get(parentName).add(path);
                        }
                    }
                    mCursor.close();
                    mHandler.sendEmptyMessage(SCAN_OK);
                }
            }).start();
        }
    }

    public boolean canGoBack() {
        return false;
    }

    public void goBack() {
    }

    @Override
    protected void enter(String tag, String path) {
    }
}
