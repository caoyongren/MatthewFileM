package com.matthew.filem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.matthew.filem.R;
import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.info.FileInfo;
import com.matthew.filem.system.FileIconHelper;
import com.matthew.filem.system.FileListItem;
import com.matthew.filem.system.FileViewInteractionHub;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.IconHolder;

import java.util.ArrayList;
import java.util.List;

public class CommonFileAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private FileViewInteractionHub mFileViewInteractionHub;
    private FileIconHelper mFileIcon;
    private int layoutId;
    private Context mContext;
    private List<FileInfo> mFileInfoList;
    private List<Integer> mSelectFileList = new ArrayList<>();
    private View.OnTouchListener mMotionListener;
    private int mWidth, mHeight;
    private IconHolder mIconHolder;

    public CommonFileAdapter(Context context, int resource,
                             List<FileInfo> objects, FileViewInteractionHub f,
                             FileIconHelper fileIcon,
                             View.OnTouchListener motionListener) {
        mFileInfoList = objects;
        layoutId = resource;
        mInflater = LayoutInflater.from(context);
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
        mContext = context;
        mMotionListener = motionListener;
        initIconHolder();
    }

    private void initIconHolder() {
        mIconHolder = IconHolder.getIconHolder(mContext);
    }

    public List<FileInfo> getFileInfoList() {
        return mFileInfoList;
    }

    public List<Integer> getSelectFileList() {
        return mSelectFileList;
    }

    @Override
    public int getCount() {
        return mFileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null)  {
            if (MainActivity.DEFAULT_VIEW_TAG_LIST.equals(LocalCacheLayout.getViewTag())) {
                convertView = mInflater.inflate(R.layout.file_browser_item_list, parent, false);
            } else if (MainActivity.DEFAULT_VIEW_TAG_GRID.equals(LocalCacheLayout.getViewTag())) {
                convertView = mInflater.inflate(R.layout.file_browser_item_grid, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            convertView.setOnTouchListener(mMotionListener);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            setParams(params.width, params.height);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setTag(position);
        viewHolder.name.setOnTouchListener(mMotionListener);

        FileInfo lFileInfo = mFileInfoList.get(position);
        FileListItem.setupFileListItemInfo(mContext, convertView, lFileInfo,
                                           mIconHolder, mFileViewInteractionHub);
        LinearLayout background = (LinearLayout)convertView;
        background.setBackgroundResource(mSelectFileList.contains(position) ?
                                         R.drawable.list_item_bg_shape : R.color.white);
        return convertView;
    }

    public static class ViewHolder {
        public TextView name;
        public ImageView icon;
        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.file_name);
            icon = (ImageView) view.findViewById(R.id.file_image);
        }
    }

    private void setParams(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int[] getParams() {
        return new int[] {mWidth, mHeight};
    }

    public void dispose() {
        if (mIconHolder != null) {
            mIconHolder.cleanup();
            mIconHolder = null;
        }
    }
}