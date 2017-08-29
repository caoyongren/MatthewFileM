package com.matthew.filem.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.matthew.filem.R;
import com.matthew.filem.fragment.leftbar.RightCommonFragment;
import com.matthew.filem.system.FileSortHelper;
import com.matthew.filem.system.FileViewInteractionHub;

public class MenuSecondDialog extends Dialog implements View.OnClickListener {
    private TextView dialog_sort_name;
    private TextView dialog_sort_size;
    private TextView dialog_sort_time;
    private TextView dialog_sort_type;

    FileViewInteractionHub mFileViewInteractionHub;

    public MenuSecondDialog(Context context, int i,
                            FileViewInteractionHub mFileViewInteractionHub) {
        super(context);
        this.mFileViewInteractionHub = mFileViewInteractionHub;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_sort_dialog);
        initView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.refreshFileList();
            }
        });
        initData();
    }

    private void initData() {
        dialog_sort_name.setOnClickListener(this);
        dialog_sort_size.setOnClickListener(this);
        dialog_sort_time.setOnClickListener(this);
        dialog_sort_type.setOnClickListener(this);
    }

    private void initView() {
        dialog_sort_name = (TextView) findViewById(R.id.dialog_sort_name);
        dialog_sort_size = (TextView) findViewById(R.id.dialog_sort_size);
        dialog_sort_time = (TextView) findViewById(R.id.dialog_sort_time);
        dialog_sort_type = (TextView) findViewById(R.id.dialog_sort_type);
    }

    public void showSecondDialog(int x, int y, int height, int width) {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.width = width;
        lp.height = height;
        lp.x = x + 220;
        lp.y = y + 50;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_sort_name:
                setSortPositive(FileSortHelper.SortStyle.name);
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortStyle.name);
                this.dismiss();
                break;
            case R.id.dialog_sort_size:
                setSortPositive(FileSortHelper.SortStyle.size);
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortStyle.size);
                this.dismiss();
                break;
            case R.id.dialog_sort_time:
                setSortPositive(FileSortHelper.SortStyle.date);
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortStyle.date);
                this.dismiss();
                break;
            case R.id.dialog_sort_type:
                setSortPositive(FileSortHelper.SortStyle.type);
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortStyle.type);
                this.dismiss();
                break;
            default:
                break;
        }
    }

    private void setSortPositive(Enum sort) {
        RightCommonFragment fragment = (RightCommonFragment)(mFileViewInteractionHub.
                                                     getMainActivity().mCurFragment);
        fragment.setSortTag(sort, !fragment.getSortTag(sort));
    }
}
