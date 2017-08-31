package com.matthew.filem.system;

import android.content.Context;

import com.matthew.filem.R;

import java.io.FilenameFilter;
import java.util.HashMap;



public class FileCategoryHelper {
    private static final String TAG = "FileCategoryHelper -- > DEBUG::";

    public enum FileCategory {
        All, Music, Video, Picture, Theme, Doc, Zip, Apk, Custom, Other, Favorite
    }

    private static String[] ZIP_EXTS  = new String[] {
            "zip", "rar"
    };

    public static HashMap<FileCategory, FilenameExtFilter> filters = new HashMap<>();

    public static HashMap<FileCategory, Integer> categoryNames = new HashMap<>();

    static {
        categoryNames.put(FileCategory.All, R.string.category_all);
        categoryNames.put(FileCategory.Music, R.string.category_music);
        categoryNames.put(FileCategory.Video, R.string.category_video);
        categoryNames.put(FileCategory.Picture, R.string.category_picture);
        categoryNames.put(FileCategory.Theme, R.string.category_theme);
        categoryNames.put(FileCategory.Doc, R.string.category_document);
        categoryNames.put(FileCategory.Zip, R.string.category_zip);
        categoryNames.put(FileCategory.Apk, R.string.category_apk);
        categoryNames.put(FileCategory.Other, R.string.category_other);
        categoryNames.put(FileCategory.Favorite, R.string.category_favorite);
    }

    private FileCategory mCategory;
    private Context mContext;

    public FileCategoryHelper(Context context) {
        mContext = context;
        mCategory = FileCategory.All;
    }

    public FilenameFilter getFilter() {
        return filters.get(mCategory);
    }

    public static FileCategory getCategoryFromPath(String path) {
        MediaFile.MediaFileType type = MediaFile.getFileType(path);
        if (type != null) {
            if (MediaFile.isAudioFileType(type.fileType)) return FileCategory.Music;
            if (MediaFile.isVideoFileType(type.fileType)) return FileCategory.Video;
            if (MediaFile.isImageFileType(type.fileType)) return FileCategory.Picture;
            if (Util.sDocMimeTypesSet.contains(type.mimeType)) return FileCategory.Doc;
        }

        int dotPosition = path.lastIndexOf('.');
        if (dotPosition < 0) {
            return FileCategory.Other;
        }

        String ext = path.substring(dotPosition + 1);
        String APK_EXT = "apk";
        if (ext.equalsIgnoreCase(APK_EXT)) {
            return FileCategory.Apk;
        }

        String THEME_EXT = "mtz";
        if (ext.equalsIgnoreCase(THEME_EXT)) {
            return FileCategory.Theme;
        }

        if (matchExts(ext, ZIP_EXTS)) {
            return FileCategory.Zip;
        }

        return FileCategory.Other;
    }

    private static boolean matchExts(String ext, String[] exts) {
        for (String ex : exts) {
            if (ex.equalsIgnoreCase(ext))
                return true;
        }
        return false;
    }
}
