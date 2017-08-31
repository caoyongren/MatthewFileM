package com.matthew.filem.system;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.matthew.filem.R;
import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.info.FileInfo;
import com.matthew.filem.utils.LocalCacheLayout;
import com.matthew.filem.utils.IconHolder;

public class FileListItem {

    public static void setupFileListItemInfo(Context context, View view,
                                             FileInfo fileInfo, IconHolder iconHolder,
                                             FileViewInteractionHub fileViewInteractionHub) {

        // if in moving mode, show selected file always
        if (fileViewInteractionHub.isMoveState()) {
            fileInfo.Selected = fileViewInteractionHub.isFileSelected(fileInfo.filePath);
        }

        ImageView lFileImage = (ImageView) view.findViewById(R.id.iv_file_image_item_grid);
        if (MainActivity.DEFAULT_VIEW_TAG_LIST.equals(LocalCacheLayout.getViewTag())) {
            Util.setText(view, R.id.et_file_name_item_grid, fileInfo.fileName,
                         context.getResources().getColor(R.color.file_name_color));
            Util.setText(view, R.id.tv_file_count_item_grid, (fileInfo.IsDir ?
                         context.getResources().getString(R.string.file_type_folder) :
                         context.getResources().getString(R.string.file_type_file)),
                         context.getResources().getColor(R.color.file_name_color));
            Util.setText(view, R.id.tv_modified_time_item_grid,
                         Util.formatDateString(context, fileInfo.ModifiedDate),
                         context.getResources().getColor(R.color.file_name_color));
            Util.setText(view, R.id.tv_file_size_item_grid,
                         fileInfo.IsDir ? String.valueOf(fileInfo.Count) :
                         Util.convertStorage(fileInfo.fileSize),
                         context.getResources().getColor(R.color.file_name_color));
            lFileImage.setVisibility(View.VISIBLE);
            if (fileInfo.IsDir) {
                lFileImage.setImageResource(R.mipmap.folder);
            } else {
                iconHolder.loadDrawable(lFileImage, fileInfo.filePath);
            }
        } else if (MainActivity.DEFAULT_VIEW_TAG_GRID.equals(LocalCacheLayout.getViewTag())) {
            Util.setText(view, R.id.et_file_name_item_grid, fileInfo.fileName,
                         context.getResources().getColor(R.color.file_name_color));
            if (fileInfo.IsDir) {
                lFileImage.setImageResource(R.mipmap.folder);
            } else {
                iconHolder.loadDrawable(lFileImage, fileInfo.filePath);
            }
        }
    }
}
