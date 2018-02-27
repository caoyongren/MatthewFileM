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
import com.matthew.filem.view.activity.MainActivity;
import com.matthew.filem.model.FileInfo;
import com.matthew.filem.system.FileIconTypeHelper;
import com.matthew.filem.system.FileListItem;
import com.matthew.filem.system.FileViewInteractionHub;
import com.matthew.filem.utils.L;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.IconHolder;

import java.util.ArrayList;
import java.util.List;

public class CommonFileAdapter extends BaseAdapter {
    public static final String TAG = "CommonFileAdapter: -- >DEBUG::";
    private LayoutInflater mInflater;
    private FileViewInteractionHub mFileViewInteractionHub;
    private FileIconTypeHelper mFileIcon;
    private int layoutId;
    private Context mContext;
    private List<FileInfo> mFileInfoTotalList;//数据源
    private List<Integer> mSelectedFileList = new ArrayList<>();
    private View.OnTouchListener mMotionListener;
    private int mWidth, mHeight;
    private IconHolder mIconHolder;

    //1.上下文　2.　资源布局　3. 数据源　 4. ? 5. 图标分类　6. 事件监听
    public CommonFileAdapter(Context context, int resource,
                             List<FileInfo> list, FileViewInteractionHub f,
                             FileIconTypeHelper fileIcon,
                             View.OnTouchListener motionListener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFileInfoTotalList = list;
        layoutId = resource;
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
        mMotionListener = motionListener;
        initIconHolder();
    }

    private void initIconHolder() {
        mIconHolder = IconHolder.getIconHolder(mContext);
    }

    @Override
    public int getCount() {
        return mFileInfoTotalList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileInfoTotalList.get(position);
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
                convertView = mInflater.inflate(R.layout.item_file_list_common, parent, false);
            } else if (MainActivity.DEFAULT_VIEW_TAG_GRID.equals(LocalCacheLayout.getViewTag())) {
                convertView = mInflater.inflate(R.layout.item_file_grid_common, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            convertView.setOnTouchListener(mMotionListener);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            L.i(TAG, "getView--> mWidth:::" + params.width + "mHeight :::" + params.height);
            setParams(params.width, params.height);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setTag(position);
        viewHolder.name.setOnTouchListener(mMotionListener);

        FileInfo lFileInfo = mFileInfoTotalList.get(position);
        FileListItem.setupFileListItemInfo(mContext, convertView, lFileInfo,
                                           mIconHolder, mFileViewInteractionHub);
        LinearLayout background = (LinearLayout)convertView;
        background.setBackgroundResource(mSelectedFileList.contains(position) ?
                                         R.drawable.list_item_bg_shape : R.color.white);
        return convertView;
    }

    public static class ViewHolder {

        public TextView name;
        public ImageView icon;
        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.et_file_name_item_grid);
            icon = (ImageView) view.findViewById(R.id.iv_file_image_item_grid);
        }
    }

    private void setParams(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public List<FileInfo> getFileInfoTotalList() {
        L.i(TAG, "mFileInfoTotalList -->" + mFileInfoTotalList.size());
        return mFileInfoTotalList;
    }

    public List<Integer> getSelectedFileList() {
        return mSelectedFileList;
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
