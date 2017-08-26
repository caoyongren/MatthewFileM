package com.openthos.filem.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.openthos.filem.fragment.base.BaseFragment;
import com.openthos.filem.R;
import com.openthos.filem.adapter.ChildAdapter;
import com.openthos.filem.bean.ImageBean;
import com.openthos.filem.system.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class DetailFragment extends BaseFragment {
    private GridView gv_detail_pictrue;
//    private int i;
    private List<String> childPathList;
//    private List<ImageBean> list;
//    HashMap<String, List<String>> mGruopMap;

    @SuppressLint("ValidFragment")
    public DetailFragment(HashMap<String, List<String>> mGruopMap, List<ImageBean> list, int i) {
        super();
    }

    public DetailFragment() {
        super();
    }

    public int getLayoutId() {
        return R.layout.gv_detail_layout;
    }

    protected void initView() {
        gv_detail_pictrue = (GridView) rootView.findViewById(R.id.gv_detail_pictrue);
    }

    protected void initListener() {
        gv_detail_pictrue.setOnItemClickListener(new DetailOnItemClickListener());
    }

    protected void initData() {
        childPathList = mGruopMap.get(list.get(index).getFolderName());
        ChildAdapter adapter = new ChildAdapter(getActivity(), childPathList, gv_detail_pictrue);
        gv_detail_pictrue.setAdapter(adapter);
    }

    private class DetailOnItemClickListener implements
                                            android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String path = childPathList.get(i);
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
