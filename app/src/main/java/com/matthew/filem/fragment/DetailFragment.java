package com.matthew.filem.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.matthew.filem.fragment.base.BaseFragment;
import com.matthew.filem.R;
import com.matthew.filem.adapter.ChildAdapter;
import com.matthew.filem.bean.ImageBean;
import com.matthew.filem.system.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class DetailFragment extends BaseFragment {
    private GridView mDetailPictrueGridView;
    private List<String> mChildPathList;

    @SuppressLint("ValidFragment")
    public DetailFragment(HashMap<String, List<String>> mGruopMap,
                                          List<ImageBean> list, int index) {
        super(mGruopMap, list, index);
    }

    public DetailFragment() {
        super();
    }

    public int getLayoutId() {
        return R.layout.gv_detail_layout;
    }

    protected void initView() {
        mDetailPictrueGridView = (GridView) rootView.findViewById(R.id.gv_detail_pictrue);
    }

    protected void initData() {
        mChildPathList = mGruopMap.get(list.get(index).getFolderName());
        ChildAdapter adapter = new ChildAdapter(getActivity(), mChildPathList, mDetailPictrueGridView);
        mDetailPictrueGridView.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        mDetailPictrueGridView.setOnItemClickListener(new DetailOnItemClickListener());
    }

    private class DetailOnItemClickListener implements
                                            android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String path = mChildPathList.get(i);
            File file = new File(path);
            if (file.isFile()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String type = Constants.getMIMEType(file);
                intent.setDataAndType(Uri.fromFile(file), type);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }

    @Override
    protected void enter(String tag, String path) {
    }
}
