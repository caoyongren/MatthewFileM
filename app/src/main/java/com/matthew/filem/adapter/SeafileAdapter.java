package com.matthew.filem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import com.matthew.filem.R;
import com.matthew.filem.model.SeafileLibraryInfo;
import com.matthew.filem.utils.SeafileUtils;
import com.matthew.filem.view.fragment.computer.CloudDiskFragment;

import java.util.ArrayList;

public class SeafileAdapter extends BaseAdapter {
    private ArrayList<SeafileLibraryInfo> mList;
    private Context mContext;
    private CloudDiskFragment.GridViewOnGenericMotionListener mMotionListener;
    private View mView;

    public SeafileAdapter(Context context, ArrayList<SeafileLibraryInfo> mList,
                          CloudDiskFragment.GridViewOnGenericMotionListener motionListener) {
        this.mList = mList;
        mContext = context;
        mMotionListener = motionListener;
    }

    public void setData(ArrayList<SeafileLibraryInfo> librarys) {
        mList = librarys;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearSelected() {
        if (mView != null) {
            mView.setSelected(false);
        }
        mView = null;
    }

    public void setSelected(View v) {
        mView = v;
        mView.setSelected(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_icon, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            convertView.setOnTouchListener(mMotionListener);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(mList.get(position).libraryName);
        viewHolder.name.setTag(position);
        int isSync = mList.get(position).isSync;
        if (isSync == SeafileUtils.SYNC) {
            viewHolder.state.setImageResource(R.drawable.sync);
        } else {
            viewHolder.state.setImageResource(R.drawable.desync);
        }
        return convertView;
    }


    public static class ViewHolder {
        public TextView name;
        public ImageView state;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_icon);
            state = (ImageView) view.findViewById(R.id.iv_icon);
        }
    }
}
