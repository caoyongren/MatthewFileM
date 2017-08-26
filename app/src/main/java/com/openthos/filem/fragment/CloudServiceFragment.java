package com.openthos.filem.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.Window;

import com.openthos.filem.BaseFragment;
import com.openthos.filem.R;
import com.openthos.filem.adapter.CloudAdapter;
import com.openthos.filem.component.CloudDialog;
import com.openthos.filem.utils.SeafileUtils;

import java.util.ArrayList;

public class CloudServiceFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String OPENTHOS_URI = "content://com.otosoft.tools.myprovider/openthosID";
    private GridView mGvCloud;
    private CloudAdapter mAdapter;
    private ArrayList<String> mList;
    private String mId;
    private String mPassword;

    @SuppressLint({"NewApi", "ValidFragment"})
    public CloudServiceFragment() {
        super();
    }

    //@SuppressLint({"NewApi", "ValidFragment"})
    //public CloudServiceFragment(FragmentManager mManager,
    //                         String usbDeviceIsAttached, MainActivity context) {
    //    super(mManager,usbDeviceIsAttached,context);
    //}

    @Override
    public int getLayoutId() {
        return R.layout.cloud_fragment_layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        ContentResolver mResolver = getActivity().getContentResolver();
        Uri uriQuery = Uri.parse(OPENTHOS_URI);
        Cursor cursor = mResolver.query(uriQuery, null, null, null, null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                //current openthos id and password
                mId = cursor.getString(cursor.getColumnIndex("openthosID"));
                mPassword = cursor.getString(cursor.getColumnIndex("password"));
            }
        }
        mId = "ln01276294@126.com";
        mPassword = "luning";
        String s = SeafileUtils.listRemote();
    }

    @Override
    protected void initView() {
        mGvCloud = (GridView) rootView.findViewById(R.id.gv_cloud_service);

        mList = new ArrayList<>();
        mAdapter = new CloudAdapter(mMainActivity, mList);
        mGvCloud.setAdapter(mAdapter);
        mGvCloud.setOnGenericMotionListener(new GridViewOnGenericMotionListener());

    }

    protected void initData() {
        if (null != list) {
            list.clear();
        }
        getList();
    }

    private void getList() {
    }

    @Override
    protected void initListener() {
        mGvCloud.setOnItemClickListener(this);
    }

    public boolean canGoBack() {
        return false;
    }

    public void goBack() {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class GridViewOnGenericMotionListener implements View.OnGenericMotionListener {

        @Override
        public boolean onGenericMotion(View view, MotionEvent motionEvent) {
            switch (motionEvent.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:
                    break;
                case MotionEvent.BUTTON_SECONDARY:
                    CloudDialog cloudDialog = new CloudDialog(context);
                    cloudDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    cloudDialog.showDialog((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
                    break;
                case MotionEvent.BUTTON_TERTIARY:
                    break;
                case MotionEvent.ACTION_SCROLL:
                    break;
                case MotionEvent.ACTION_HOVER_ENTER:
                    break;
            }
            return false;
        }
    }

    @Override
    protected void enter(String tag, String path) {
    }
}
