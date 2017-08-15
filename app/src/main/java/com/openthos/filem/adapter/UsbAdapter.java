package com.openthos.filem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openthos.filem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/24/17.
 */

public class UsbAdapter extends BaseAdapter{
    private Context mContext;
    private List<String[]> mDatas;

    public UsbAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
        }
        return convertView;
    }

    public void setData(List<String[]> datas){
        if (datas != null){
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    class ViewHolder{
        private ImageView icon;
        private TextView usbName,totalSize,AvailSize;
        private ProgressBar pb;
        public ViewHolder(View view){
            icon = ((ImageView) view.findViewById(R.id.usb_item_icon));
        }
    }
}
