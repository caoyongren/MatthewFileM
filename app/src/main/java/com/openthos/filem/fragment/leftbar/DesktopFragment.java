package com.openthos.filem.fragment.leftbar;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.openthos.filem.BaseFragment;
import com.openthos.filem.R;
import com.openthos.filem.adapter.DeskAdapter;
import com.openthos.filem.bean.AppInfo;
import com.openthos.filem.utils.L;

import java.util.ArrayList;
import java.util.List;

public class DesktopFragment extends BaseFragment {
    private ArrayList<AppInfo> mAppInfos = new ArrayList<>();
    private String mPackageName;
    private DeskAdapter deskAdapter;
    private GridView mGridViewDesk;
    private PackageManager mPm;

    @Override
    public int getLayoutId() {
        return R.layout.desk_fragment_layout;
    }

    @Override
    protected void initView() {
        mGridViewDesk = (GridView) rootView.findViewById(R.id.gv_desk_icon);//.mGridViewDesk
    }

    protected void initData() {
        if (mAppInfos != null){
            mAppInfos.clear();
        }
        getInstallPackageInfo();
        deskAdapter = new DeskAdapter(mAppInfos, getActivity());
        mGridViewDesk.setAdapter(deskAdapter);
    }

    @Override
    protected void initListener() {
        mGridViewDesk.setOnGenericMotionListener(new DeskOnGenericMotionListener());
    }

    private void getInstallPackageInfo() {
        mPm = getActivity().getPackageManager();
        List<PackageInfo> packages
                          = mPm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for(int i = 0; i< packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo appInfo =new AppInfo();
            String appName = packageInfo.applicationInfo
                                        .loadLabel(getActivity().getPackageManager()).toString();
            appInfo.setAppName(appName);
            Drawable appIcon = packageInfo.applicationInfo
                                          .loadIcon(getActivity().getPackageManager());
            appInfo.setIcon(appIcon);
            String packageName = packageInfo.packageName;
            appInfo.setPackageName(packageName);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                mAppInfos.add(appInfo);
            }
        }
    }

    private class DeskOnGenericMotionListener implements View.OnGenericMotionListener {
        @Override
        public boolean onGenericMotion(View view, MotionEvent event) {
            switch (event.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:
                    IconItemClickListener();
                    break;
                case MotionEvent.BUTTON_SECONDARY:
                    IconUninstallItemClickListener();
                    break;
                case MotionEvent.BUTTON_TERTIARY:
                    IconItemClickListener();
                    break;
                case MotionEvent.ACTION_SCROLL:
                    MouseScrollAction(event);
                    break;
            }
            return false;
        }
    }

    private void IconItemClickListener() {
        mGridViewDesk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPackageName = mAppInfos.get(i).getPackageName();
                Intent intent = mPm.getLaunchIntentForPackage(mPackageName);
                if (null != intent){
                    startActivity(intent);
                }
            }
        });
    }

    private void IconUninstallItemClickListener() {
        mGridViewDesk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPackageName = mAppInfos.get(i).getPackageName();
                uninstallAPK(mPackageName);
            }
        });
    }

    private void MouseScrollAction(MotionEvent event) {
        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) {
            L.i("fortest::onGenericMotionEvent", "down");
//            T.showShort(getContext(), getContext.getString(R.string.scroll_down));
        }
        else {
            L.i("fortest::onGenericMotionEvent", "up");
//            T.showShort(getContext(), getContext.getString(R.string.scroll_up));
        }
    }

    private void uninstallAPK(String packageName){
        Uri uri=Uri.parse("package:"+packageName);
        Intent intent=new Intent(Intent.ACTION_DELETE,uri);
        startActivityForResult(intent,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){
            deskAdapter.notifyDataSetChanged();
            initData();
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
