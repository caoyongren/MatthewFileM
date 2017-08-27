package com.openthos.filem.fragment.leftbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.openthos.filem.BaseFragment;
import com.openthos.filem.R;
import com.openthos.filem.adapter.VideoAdapter;
import com.openthos.filem.adapter.VideoItem;
import com.openthos.filem.system.Constants;

import java.io.File;
import java.util.ArrayList;

public class VideoFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ArrayList<VideoItem> videoItems;
    private ProgressDialog mProgressDialog;
    private TextView mTvNoVideo;
    private GridView mGridViewVideo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            if (videoItems != null && videoItems.size() > 0) {
                mGridViewVideo.setAdapter(new VideoAdapter(getActivity(), videoItems));
            }else {
                mTvNoVideo.setVisibility(View.VISIBLE);
            }
            handler.removeCallbacksAndMessages(null);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.video_fragment_layout;
    }

    @Override
    protected void initView() {
        mTvNoVideo = (TextView) rootView.findViewById(R.id.tv_no_video);
        mGridViewVideo = (GridView) rootView.findViewById(R.id.gv_video_pager);
    }

    protected void initData() {
        getVideoList();
    }

    @Override
    protected void initListener() {
        mGridViewVideo.setOnItemClickListener(this);
    }

    private void getVideoList() {
        mProgressDialog = ProgressDialog.show(getActivity(), null, "loading...");
        new Thread() {
            public void run() {
                videoItems = new ArrayList<> ();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getActivity().getContentResolver();
                String[] projection = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA
                };
                Cursor cursor = contentResolver.query(uri,projection,null,null,null);
                if (cursor!=null) {
                    while (cursor.moveToNext()) {
                        VideoItem item = new VideoItem();
                        String name = cursor.getString
                                      (cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        item.setName(name);
                        Long size = cursor.getLong
                                    (cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        item.setSize(size);
                        String data = cursor.getString
                                      (cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        item.setData(data);
                        videoItems.add(item);
                    }
                    cursor.close();
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        VideoItem videoItem = videoItems.get(i);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File f = new File(videoItem.getData());
        String type = Constants.getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
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
