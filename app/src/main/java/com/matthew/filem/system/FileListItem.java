package com.matthew.filem.system;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.matthew.filem.R;
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

        ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
        if ("list".equals(LocalCacheLayout.getViewTag())) {
            Util.setText(view, R.id.file_name, fileInfo.fileName,
                         context.getResources().getColor(R.color.file_name_color));
            Util.setText(view, R.id.file_count, (fileInfo.IsDir ?
                         context.getResources().getString(R.string.file_type_folder) :
                         context.getResources().getString(R.string.file_type_file)),
                         context.getResources().getColor(R.color.file_name_color));
            Util.setText(view, R.id.modified_time,
                         Util.formatDateString(context, fileInfo.ModifiedDate),
                         context.getResources().getColor(R.color.file_name_color));
            Util.setText(view, R.id.file_size,
                         fileInfo.IsDir ? String.valueOf(fileInfo.Count) :
                         Util.convertStorage(fileInfo.fileSize),
                         context.getResources().getColor(R.color.file_name_color));
            lFileImage.setVisibility(View.VISIBLE);
            if (fileInfo.IsDir) {
                lFileImage.setImageResource(R.mipmap.folder);
            } else {
                iconHolder.loadDrawable(lFileImage, fileInfo.filePath);
            }
        } else {
            Util.setText(view, R.id.file_name, fileInfo.fileName,
                         context.getResources().getColor(R.color.file_name_color));
            if (fileInfo.IsDir) {
                lFileImage.setImageResource(R.mipmap.folder);
            } else {
                iconHolder.loadDrawable(lFileImage, fileInfo.filePath);
            }
        }
    }
}
