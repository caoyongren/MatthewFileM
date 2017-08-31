package com.matthew.filem.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.matthew.filem.fragment.base.BaseFragment;
import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.R;
import com.matthew.filem.fragment.leftbar.CommonRightFragment;
import com.matthew.filem.info.SearchInfo;
import com.matthew.filem.system.FileIconHelper;
import com.matthew.filem.system.IntentBuilder;
import com.matthew.filem.system.Util;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.Constants;
import java.io.File;
import java.util.ArrayList;

public class SearchFragment extends BaseFragment{
    private static final String TAG = "SearchFragment --> DEBUG::";
    private Fragment mCurFragment;
    private ListView mLvMianSearch;
    private SearchAdapter mSearchAdapter;
    private MainActivity mActivity;
    private LinearLayout mLlEmptyView;

    @SuppressLint({"NewApi", "ValidFragment"})
    public SearchFragment(FragmentManager manager, ArrayList<SearchInfo> mFileList) {
        super(manager,mFileList);
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public SearchFragment() {
        super();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_;
    }

    @Override
    protected void initView() {
        mLvMianSearch = (ListView) rootView.findViewById(R.id.lv_mian_search);
        mLlEmptyView = (LinearLayout) rootView.findViewById(R.id.ll_common_right_empty_view);
        if (mSearchList == null) {
            mLvMianSearch.setVisibility(View.GONE);
            mLlEmptyView.setVisibility(View.VISIBLE);
        } else {
            mLvMianSearch.setVisibility(View.VISIBLE);
            mLlEmptyView.setVisibility(View.GONE);
        }
    }

    protected void initData() {
        mSearchAdapter = new SearchAdapter();
        mLvMianSearch.setAdapter(mSearchAdapter);
    }

    @Override
    protected void initListener() {
        mLvMianSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fileRealPath = mSearchList.get(i).fileAbsolutePath;
                if (!new File(fileRealPath).isDirectory()) {
                    Context context = getActivity();
                    try {
                        IntentBuilder.viewFile(context,fileRealPath,null);
                    } catch (Exception e) {
                        Toast.makeText(context,getString(
                                       R.string.found_no_corresponding_application_to_open),
                                       Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (mCurFragment != null) {
                        mManager.beginTransaction().remove(mCurFragment).commit();
                    }
                    mActivity = (MainActivity) getActivity();
                    mManager.beginTransaction().hide(mActivity.getVisibleFragment()).commit();
                    mCurFragment = new CommonRightFragment(TAG, fileRealPath, null,null, false);
                    mManager.beginTransaction().add(R.id.framelayout_right_main, mCurFragment,
                            Constants.SEARCHSYSTEMSPACE_TAG).commit();
                    mActivity.mCurFragment = mCurFragment;
                    notifyModify();
                }
            }
        });
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {
    }

    private class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSearchList == null ? -1 : mSearchList.size();
        }

        @Override
        public Object getItem(int i) {
            return mSearchList == null ? -1 : mSearchList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (mSearchList != null) {
                view = View.inflate(getActivity(), R.layout.item_search_file,null);
                TextView search_file_name = (TextView) view.findViewById(R.id.search_file_name);
                TextView search_file_path = (TextView) view.findViewById(R.id.search_file_path);
                ImageView image = (ImageView) view.findViewById(R.id.search_file_bg);
                String fileName = mSearchList.get(i).fileName;
                search_file_name.setText(fileName);
                String fileAbsolutePath = mSearchList.get(i).fileAbsolutePath;
                String filePath = mSearchList.get(i).filePath;
                fileAbsolutePath = fileAbsolutePath.substring(0,
                                       fileAbsolutePath.lastIndexOf(Constants.SD_PATH));
                search_file_path.setText(fileAbsolutePath);
                boolean isDirectory = new File(filePath).isDirectory();
                int fileIcon = FileIconHelper.getFileIcon(Util.getExtFromFilename(filePath));
                image.setBackgroundResource(!isDirectory ? fileIcon : R.mipmap.folder);
                return view;
            } else {
                return null;
            }
        }
    }

    public void notifyModify() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSearchAdapter != null) {
                    mSearchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalCacheLayout.setSearchText(null);
    }

    @Override
    protected void enter(String tag, String path) {
    }
}
