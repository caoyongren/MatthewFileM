package com.matthew.filem.fragment.leftbar;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.matthew.filem.fragment.base.BaseFragment;
import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.R;
import com.matthew.filem.component.DiskDialog;
import com.matthew.filem.bean.FileInfo;
import com.matthew.filem.system.FileViewInteractionHub;
import com.matthew.filem.system.Util;
import com.matthew.filem.utils.L;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.T;
import com.matthew.filem.utils.Constants;
import com.matthew.filem.view.CustomGridView;

import java.io.File;
import java.util.ArrayList;

public class ComputerFragment extends BaseFragment {
    private static final String TAG = "ComputerFragment -- > DEBUG:";
    ArrayList<FileInfo> mFileInfoArrayList = null;
    FileViewInteractionHub.CopyOrMove copyOrMove = null;

    private RelativeLayout mRlSystem;
    private RelativeLayout mRlDisk;
    private RelativeLayout mRlCloudService;
    private RelativeLayout mRlUsbOne;
    private RelativeLayout mRlUsbTwo;
    private RelativeLayout mRlUsbThree;
    private RelativeLayout mRlPersonal;
    private TextView mTvSystemTotal;
    private TextView mTvSystemAvail;
    private ProgressBar mProgressbarSystem;
    private TextView mTvDiskTotal;
    private TextView mTvDiskAvail;
    private ProgressBar mProgressbarDisk;
    private TextView mTvUsbTotalOne;
    private TextView mTvUsbAvailOne;
    private ProgressBar mProgressbarUsbOne;
    private TextView mTvUsbTotalTwo;
    private TextView mTvUsbAvailTwo;
    private ProgressBar mProgressbarUsbTwo;
    private TextView mTvUsbTotalThree;
    private TextView mTvUsbAvailThree;
    private ProgressBar mProgressbarUsbThree;
    private ProgressBar mProgressbarService;

    public BaseFragment mCurFragment;
    private long lastBackTime = 0;
    private ArrayList<File> mountUsb = null;
    private String mountPathOne;
    private String mountPathTwo;
    private String mountPathThree;
    private long currentBackTime;
    private String mountDiskPath = null;
    private LinearLayout mLlComputerFg;
    private LinearLayout mLlMobileDevice;
    private CustomGridView mUsbGrid;

    @SuppressLint({"NewApi", "ValidFragment"})
    public ComputerFragment(FragmentManager mManager,
                            String usbDeviceIsAttached, MainActivity context) {
        super(mManager,usbDeviceIsAttached,context);
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public ComputerFragment() {
        super();
    }

    @Override
    public int getLayoutId() {
        return R.layout.computer_fragment_layout;
    }


    @Override
    protected void initView() {
        mLlComputerFg = (LinearLayout) rootView.findViewById(R.id.ll_layout_computer_fg);
        mRlPersonal = (RelativeLayout) rootView.findViewById(R.id.rl_personal_space_computer_fg);
        mRlSystem = (RelativeLayout) rootView.findViewById(R.id.rl_system_computer_fg);
        mRlDisk = (RelativeLayout) rootView.findViewById(R.id.rl_disk_computer_fg);

        mRlCloudService = (RelativeLayout) rootView.findViewById(R.id.rl_cloud_service_computer_fg);
        mRlUsbOne = (RelativeLayout) rootView.findViewById(R.id.rl_usb_one_computer_fg);
        mRlUsbTwo = (RelativeLayout) rootView.findViewById(R.id.rl_mount_space_two);
        mRlUsbThree = (RelativeLayout) rootView.findViewById(R.id.rl_mount_space_three);

        mLlMobileDevice = (LinearLayout) rootView.findViewById(R.id.ll_mobile_device_computer_fg);
        mTvSystemTotal = (TextView) rootView.findViewById(R.id.tv_system_total_computer_fg);
        mTvSystemAvail = (TextView) rootView.findViewById(R.id.tv_system_avail_computer_fg);
        mTvDiskTotal = (TextView) rootView.findViewById(R.id.tv_disk_total_computer_fg);
        mTvDiskAvail = (TextView) rootView.findViewById(R.id.tv_disk_avail_computer_fg);

        mTvUsbTotalOne = (TextView) rootView.findViewById(R.id.tv_usb_total_one_computer_fg);
        mTvUsbAvailOne = (TextView) rootView.findViewById(R.id.tv_usb_avail_one_computer_fg);
        mTvUsbTotalTwo = (TextView) rootView.findViewById(R.id.tv_usb_total_two);
        mTvUsbAvailTwo = (TextView) rootView.findViewById(R.id.tv_usb_avail_two);
        mTvUsbTotalThree = (TextView) rootView.findViewById(R.id.tv_usb_total_three);
        mTvUsbAvailThree = (TextView) rootView.findViewById(R.id.tv_usb_avail_three);

        mProgressbarSystem = (ProgressBar) rootView.findViewById(R.id.progressbar_system_computer_fg);
        mProgressbarDisk = (ProgressBar) rootView.findViewById(R.id.progress_disk_computer_fg);
        mProgressbarUsbOne = (ProgressBar) rootView.findViewById(R.id.progressbar_usb_one_computer_fg);
        mProgressbarUsbTwo = (ProgressBar) rootView.findViewById(R.id.pb_usb_two);
        mProgressbarUsbThree = (ProgressBar) rootView.findViewById(R.id.pb_usb_three);
        mProgressbarService = (ProgressBar) rootView.findViewById(R.id.progressbar_cloud_service_computer_fg);
        mUsbGrid = (CustomGridView) rootView.findViewById(R.id.usb_computer_fg);
    }

    @Override
    protected void initData() {
        showStorageInfo();
        if (usbDeviceIsAttached != null && usbDeviceIsAttached.equals("usb_device_attached")) {
            String[] cmd = {"df"};
            ArrayList<String[]> list = Util.execUsb(cmd);
            if (list.size() >= 1) {
                String[] usb1 = list.get(0);
                mountPathOne = usb1[0];
                if (usb1 != null && usb1.length > 0) {
                    showMountDevices(usb1, 1);
                    mLlMobileDevice.setVisibility(View.VISIBLE);
                    mRlUsbOne.setVisibility(View.VISIBLE);
                    mRlUsbOne.setOnGenericMotionListener
                            (new MouseRelativeOnGenericMotionListener());
                    mMainActivity.mHandler.sendEmptyMessage(Constants.USB1_READY);
                    if (list.size() >= 2) {
                        String[] usb2 = list.get(1);
                        mountPathTwo = usb2[0];
                        if (usb2 != null && usb2.length > 0) {
                            showMountDevices(usb2, 2);
                            mRlUsbTwo.setVisibility(View.VISIBLE);
                            mRlUsbTwo.setOnGenericMotionListener
                                    (new MouseRelativeOnGenericMotionListener());
                            mMainActivity.mHandler.sendEmptyMessage(Constants.USB2_READY);
                        }
                    }
                }
            }
        } else if (usbDeviceIsAttached != null
                && usbDeviceIsAttached.equals("usb_device_detached")) {
            mLlMobileDevice.setVisibility(View.GONE);
            mRlUsbOne.setVisibility(View.GONE);
            mRlUsbTwo.setVisibility(View.GONE);
            mRlUsbThree.setVisibility(View.GONE);
            T.showShort(mMainActivity, getResources().getString(R.string.USB_device_disconnected));
        }
    }

    @Override
    protected void initListener() {
        mLlComputerFg.setOnGenericMotionListener(new MouseRelativeOnGenericMotionListener());
        mRlSystem.setOnGenericMotionListener(new MouseRelativeOnGenericMotionListener());
        mRlDisk.setOnGenericMotionListener(new MouseRelativeOnGenericMotionListener());
        mRlCloudService.setOnGenericMotionListener(new MouseRelativeOnGenericMotionListener());
        mRlPersonal.setOnGenericMotionListener(new MouseRelativeOnGenericMotionListener());
    }

    public void hideMountSpaceOne() {
        if (mRlUsbOne != null) {
            mRlUsbOne.setVisibility(View.GONE);
        }
    }

    public void hideMountSpaceTwo() {
        if (mRlUsbTwo != null) {
            mRlUsbTwo.setVisibility(View.GONE);
        }
    }

    public void hideMountSpaceThree() {
        if (mRlUsbThree != null) {
            mRlUsbThree.setVisibility(View.GONE);
        }
    }

    //展示各个存储的信息．
    private void showStorageInfo() {
        Util.SystemInfo systemInfo = Util.getRomMemory();
        if (null != systemInfo) {
            mTvSystemTotal.setText(Util.convertStorage(systemInfo.totalMemory));
            mTvSystemAvail.setText(Util.convertStorage(systemInfo.avilMemory));
            mProgressbarSystem.setMax((int) Double.parseDouble(Util.convertStorage(systemInfo.totalMemory)
                                                          .substring(0, 3)) * 10);
            mProgressbarSystem.setSecondaryProgress
                      ((int) (Double.parseDouble
                      (Util.convertStorage(systemInfo.totalMemory - systemInfo.avilMemory)
                           .substring(0, 3)) * 10));
        }

        String[] cmd = {"df"};
        String[] usbs = Util.execDisk(cmd);
        if (usbs != null && usbs.length > 0) {
            showDiskInfo(usbs);
        } else {
            showSdcardInfo();
        }
    }

    //展示存储信息．
    private void showDiskInfo(String[] usbs) {
        mountDiskPath = usbs[0];
        mTvDiskTotal.setText(usbs[1]);
        mTvDiskAvail.setText(usbs[3]);
        int max = (int) Double.parseDouble(usbs[1].substring(0, 3)) * 10;
        int avail = (int) Double.parseDouble(usbs[3].substring(0, 2)) * 10;
        mProgressbarDisk.setMax(max);
        mProgressbarDisk.setProgress(max - avail);
    }

    private void showSdcardInfo() {
        Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
        if (null != sdCardInfo) {
            mTvDiskTotal.setText(Util.convertStorage(sdCardInfo.total));
            mTvDiskAvail.setText(Util.convertStorage(sdCardInfo.free));

            L.e("mTvDiskTotal", Util.convertStorage(sdCardInfo.total).substring(0, 3));
            L.e("mTvDiskAvail", Util.convertStorage(sdCardInfo.free).substring(0, 3));

            mProgressbarDisk.setMax((int) Double.parseDouble
                               (Util.convertStorage(sdCardInfo.total).substring(0, 3)) * 10);
            mProgressbarDisk.setProgress((int) (Double.parseDouble
                                    (Util.convertStorage(sdCardInfo.total - sdCardInfo.free)
                                         .substring(0, 3)) * 10));
        }
    }

    private class MouseRelativeOnGenericMotionListener implements View.OnGenericMotionListener {
        @Override
        public boolean onGenericMotion(View v, MotionEvent event) {
            switch (event.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:
                    mMainActivity.clearNivagateFocus();
                    primaryClick(v);
                    break;
                case MotionEvent.BUTTON_SECONDARY:
                    mMainActivity.clearNivagateFocus();
                    secondaryClick(v, event);
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

    private void showMountDevices(String[] usbs, int flag) {
        switch (flag) {
            case 1:
                mTvUsbTotalOne.setText(usbs[1]);
                mTvUsbAvailOne.setText(usbs[3]);
                int maxOne = (int) (Double.parseDouble(usbs[1].substring(0, 3)) * 100);
                int availOne = (int) (Double.parseDouble(usbs[3].substring(0, 3)) * 100);
                int progressOne = maxOne - availOne >= 0?
                                  maxOne - availOne : maxOne -  (availOne / 1024);
                mProgressbarUsbOne.setMax(maxOne);
                mProgressbarUsbOne.setProgress(progressOne);
                break;
            case 2:
                mTvUsbTotalTwo.setText(usbs[1]);
                mTvUsbAvailTwo.setText(usbs[3]);
                int maxTwo = (int) (Double.parseDouble(usbs[1].substring(0, 3)) * 100);
                int availTwo = (int) (Double.parseDouble(usbs[3].substring(0, 3)) * 100);
                int progressTwo = maxTwo - availTwo >= 0?
                                  maxTwo - availTwo : maxTwo -  (availTwo / 1024);
                mProgressbarUsbTwo.setMax(maxTwo);
                mProgressbarUsbTwo.setProgress(progressTwo);
                break;
            case 3:
                mTvUsbTotalThree.setText(usbs[1]);
                mTvUsbAvailThree.setText(usbs[3]);
                int maxThree = (int) (Double.parseDouble(usbs[1].substring(0, 3)) * 100);
                int availThree = (int) (Double.parseDouble(usbs[3].substring(0, 3)) * 100);
                int progressThree = maxThree - availThree >= 0?
                                    maxThree - availThree : maxThree - (availThree / 1024);
                mProgressbarUsbThree.setMax(maxThree);
                mProgressbarUsbThree.setProgress(progressThree);
                break;
        }
    }

    public void primaryClick(View view) {
        currentBackTime = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.rl_system_computer_fg:
                setDiskClickInfo(R.id.rl_system_computer_fg, Constants.SYSTEM_SPACE_FRAGMENT, null);
                break;
            case R.id.rl_disk_computer_fg:
                setDiskClickInfo(R.id.rl_disk_computer_fg, Constants.SD_SPACE_FRAGMENT,
                                 Constants.SD_PATH);
                break;
            case R.id.rl_usb_one_computer_fg:
                setDiskClickInfo(R.id.rl_usb_one_computer_fg, Constants.USB_SPACE_FRAGMENT,
                                 mountPathOne);
                break;
            case R.id.rl_mount_space_two:
                setDiskClickInfo(R.id.rl_mount_space_two, Constants.USB_SPACE_FRAGMENT,
                                 mountPathTwo);
                break;
            case R.id.rl_mount_space_three:
                setDiskClickInfo(R.id.rl_mount_space_three, Constants.USB_SPACE_FRAGMENT,
                                 mountPathThree);
                break;
            case R.id.rl_cloud_service_computer_fg:
                setDiskClickInfo(R.id.rl_cloud_service_computer_fg, Constants.YUN_SPACE_FRAGMENT, null);
                break;
            case R.id.rl_personal_space_computer_fg:
                setDiskClickInfo(R.id.rl_personal_space_computer_fg, Constants.PERSONAL_TAG, null);
                break;
            default:
                setSelectedCardBg(Constants.RETURN_TO_WHITE);
                break;
        }
    }

    public void secondaryClick(View view, MotionEvent event) {
        primaryClick(view);
        switch (view.getId()) {
            case R.id.rl_usb_one_computer_fg:
                showDiskDialog(view, event, true);
                break;
            case R.id.rl_mount_space_two:
                showDiskDialog(view, event, true);
                break;
            case R.id.rl_mount_space_three:
                showDiskDialog(view, event, true);
                break;

            default:
                showDiskDialog(view, event,false);
                break;
        }
    }

    private void showDiskDialog(View view, MotionEvent event, boolean isUSB) {
        DiskDialog diskDialog = new DiskDialog(context, isUSB, view);
        diskDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        diskDialog.showDialog((int) event.getRawX(), (int) event.getRawY());
    }

    private void setDiskClickInfo(int id, String tag, String path) {
        if (currentBackTime - lastBackTime > Constants.DOUBLE_CLICK_INTERVAL_TIME
                 || id != mCurId) {
            setSelectedCardBg(id);
            mCurId = id;
            lastBackTime = currentBackTime;
        } else {
            enter(tag, path);
        }
    }

    @Override
    public void enter() {
        super.enter();
        if (mCurId == R.id.rl_usb_one_computer_fg) {
            enter(Constants.USB_SPACE_FRAGMENT, mountPathOne);
        } else if (mCurId == R.id.rl_mount_space_two) {
            enter(Constants.USB_SPACE_FRAGMENT, mountPathTwo);
        } else if (mCurId == R.id.rl_mount_space_three) {
            enter(Constants.USB_SPACE_FRAGMENT, mountPathThree);
        }
    }

   public void uninstallUSB() {
        switch (mCurId) {
            case R.id.rl_usb_one_computer_fg:
                mMainActivity.uninstallUSB(Constants.USB_ONE);
                break;
            case R.id.rl_mount_space_two:
                mMainActivity.uninstallUSB(Constants.USB_TWO);
                break;
            case R.id.rl_mount_space_three:
                mMainActivity.uninstallUSB(Constants.USB_THREE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void enter(String tag, String path) {
        if (mCurFragment != null) {
            if (mCurFragment instanceof RightShowFileFragment) {
                mFileInfoArrayList = ((RightShowFileFragment) mCurFragment).getFileInfoList();
                copyOrMove = ((RightShowFileFragment) mCurFragment).getCurCopyOrMoveMode();
            }
        }
        if (mFileInfoArrayList != null && copyOrMove != null) {
            T.showShort(context,
                        context.getString(R.string.operation_failed_permission_refuse));
        }
        FragmentTransaction transaction = mManager.beginTransaction();
        transaction.hide(mMainActivity.mCurFragment);
        if (Constants.PERSONAL_TAG.equals(tag)) {
            mMainActivity.setNavigationPath("SDCard");
            transaction.show(mMainActivity.mPersonalDiskFragment).commit();
            mCurFragment = mMainActivity.mPersonalDiskFragment;
        } else {
            mCurFragment = new RightShowFileFragment(tag, path,
                                                   mFileInfoArrayList, copyOrMove, false);
            transaction.add(R.id.framelayout_right_main, mCurFragment, Constants.SDSSYSTEMSPACE_TAG).commit();
        }
        mMainActivity.mCurFragment = mCurFragment;
        mCurId = Constants.RETURN_TO_WHITE;
    }

    public void setSelectedCardBg(int id) {
        switch (id) {
            case R.id.rl_system_computer_fg:
                mRlSystem.setSelected(true);
                mRlDisk.setSelected(false);
                mRlUsbOne.setSelected(false);
                mRlUsbTwo.setSelected(false);
                mRlUsbThree.setSelected(false);
                mRlCloudService.setSelected(false);
                mRlPersonal.setSelected(false);
                break;
            case R.id.rl_disk_computer_fg:
                mRlSystem.setSelected(false);
                mRlDisk.setSelected(true);
                mRlUsbOne.setSelected(false);
                mRlUsbTwo.setSelected(false);
                mRlUsbThree.setSelected(false);
                mRlCloudService.setSelected(false);
                mRlPersonal.setSelected(false);
                break;
            case R.id.rl_usb_one_computer_fg:
                mRlSystem.setSelected(false);
                mRlDisk.setSelected(false);
                mRlUsbOne.setSelected(true);
                mRlUsbTwo.setSelected(false);
                mRlUsbThree.setSelected(false);
                mRlCloudService.setSelected(false);
                mRlPersonal.setSelected(false);
                break;
            case R.id.rl_mount_space_two:
                mRlSystem.setSelected(false);
                mRlDisk.setSelected(false);
                mRlUsbOne.setSelected(false);
                mRlUsbTwo.setSelected(true);
                mRlUsbThree.setSelected(false);
                mRlCloudService.setSelected(false);
                mRlPersonal.setSelected(false);
                break;
            case R.id.rl_mount_space_three:
                mRlSystem.setSelected(false);
                mRlDisk.setSelected(false);
                mRlUsbOne.setSelected(false);
                mRlUsbTwo.setSelected(false);
                mRlUsbThree.setSelected(true);
                mRlCloudService.setSelected(false);
                mRlPersonal.setSelected(false);
                break;
            case R.id.rl_cloud_service_computer_fg:
                mRlSystem.setSelected(false);
                mRlDisk.setSelected(false);
                mRlUsbOne.setSelected(false);
                mRlUsbTwo.setSelected(false);
                mRlUsbThree.setSelected(false);
                mRlCloudService.setSelected(true);
                mRlPersonal.setSelected(false);
                break;
            case R.id.rl_personal_space_computer_fg:
                mRlSystem.setSelected(false);
                mRlDisk.setSelected(false);
                mRlUsbOne.setSelected(false);
                mRlUsbTwo.setSelected(false);
                mRlUsbThree.setSelected(false);
                mRlCloudService.setSelected(false);
                mRlPersonal.setSelected(true);
                break;
            case Constants.RETURN_TO_WHITE:
                if (mRlSystem != null) {
                    mRlSystem.setSelected(false);
                    mRlDisk.setSelected(false);
                    mRlUsbOne.setSelected(false);
                    mRlUsbTwo.setSelected(false);
                    mRlUsbThree.setSelected(false);
                    mRlCloudService.setSelected(false);
                    mRlPersonal.setSelected(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalCacheLayout.setSearchText(null);
    }

    public boolean canGoBack() {
        boolean canGoBack = false;
        Fragment baseFragment = mCurFragment;
        if (baseFragment instanceof RightShowFileFragment) {
            RightShowFileFragment rightShowFileFragment = (RightShowFileFragment) baseFragment;
            canGoBack = rightShowFileFragment.canGoBack();
        }
        return canGoBack;
    }

    public void goBack() {
        Fragment baseFragment = mCurFragment;
        if (baseFragment instanceof RightShowFileFragment) {
            RightShowFileFragment rightShowFileFragment = (RightShowFileFragment) baseFragment;
            rightShowFileFragment.goBack();
        }
    }
}
