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
import com.openthos.filem.adapter.AudioAdapter;
import com.openthos.filem.bean.AudioItem;
import com.openthos.filem.system.Constants;
import com.openthos.filem.utils.L;

import java.io.File;
import java.util.ArrayList;

public class MusicFragment  extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "MusicFragment.java -- > DEBUG:";
    private static final int MUSIC_OK = 0;
    private ArrayList<AudioItem> audioItems;
    private ContentResolver mContentResolver;
    private ProgressDialog mProgressDialog;
    private GridView mGridViewMusic;
    private TextView mTvNoMusic;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MUSIC_OK:
                    mProgressDialog.dismiss();
                    if (audioItems != null && audioItems.size() > 0) {
                        mGridViewMusic.setAdapter(new AudioAdapter(getActivity(), audioItems));
                    } else {
                        mTvNoMusic.setVisibility(View.VISIBLE);
                    }
                    handler.removeCallbacksAndMessages(null);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.music_fragment_layout;
    }

    @Override
    protected void initView() {
        mGridViewMusic = (GridView) rootView.findViewById(R.id.gv_audio_pager);
        mTvNoMusic = (TextView) rootView.findViewById(R.id.tv_no_audio);
    }

    protected void initData() {
        getAudioList();
    }

    @Override
    protected void initListener() {
        mGridViewMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        L.i("MasterMan", TAG + "click");
        File f = new File(audioItems.get(i).getData());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = Constants.getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    private void getAudioList() {
        mProgressDialog = ProgressDialog.show(getActivity(), null, "loading...");
        new Thread() {
            public void run() {
                audioItems = new ArrayList<> ();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                mContentResolver = getActivity().getContentResolver();
                String[] projection = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA
                };
                Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        AudioItem item = new AudioItem();
                        String name = cursor.getString
                                      (cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        item.setName(name);
                        Long size = cursor.getLong
                                    (cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        item.setSize(size);
                        String data = cursor.getString
                                      (cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        item.setData(data);
                        audioItems.add(item);
                    }
                    cursor.close();
                    handler.sendEmptyMessage(MUSIC_OK);
                }
            }
        }.start();
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
