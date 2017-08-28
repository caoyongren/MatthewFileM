package com.matthew.filem.system;

import com.matthew.filem.bean.FileInfo;

import java.util.Comparator;
import java.util.HashMap;

public class FileSortHelper {
    public enum SortStyle {
        name, size, date, type
    }
    private SortStyle mSort;
    private boolean mFileFirst;
    private HashMap<SortStyle, Comparator> mComparatorList = new HashMap<>();
    public FileSortHelper() {
        mSort = SortStyle.name;
        Comparator cmpName = new FileComparator() {
            @Override
            public int doCompare(FileInfo object1, FileInfo object2) {
                return object1.fileName.compareToIgnoreCase(object2.fileName);
            }
        };
        mComparatorList.put(SortStyle.name, cmpName);
        Comparator cmpSize = new FileComparator() {
            @Override
            public int doCompare(FileInfo object1, FileInfo object2) {
                return longToCompareInt(object1.fileSize - object2.fileSize);
            }
        };
        mComparatorList.put(SortStyle.size, cmpSize);
        Comparator cmpDate = new FileComparator() {
            @Override
            public int doCompare(FileInfo object1, FileInfo object2) {
                return longToCompareInt(object2.ModifiedDate - object1.ModifiedDate);
            }
        };
        mComparatorList.put(SortStyle.date, cmpDate);
        Comparator cmpType = new FileComparator() {
            @Override
            public int doCompare(FileInfo object1, FileInfo object2) {
                int result = Util.getExtFromFilename(object1.fileName).compareToIgnoreCase(
                        Util.getExtFromFilename(object2.fileName));
                if (result != 0)
                    return result;

                return Util.getNameFromFilename(object1.fileName).compareToIgnoreCase(
                        Util.getNameFromFilename(object2.fileName));
            }
        };
        mComparatorList.put(SortStyle.type, cmpType);
    }

    public void setSortMethog(SortStyle s) {
        mSort = s;
    }

    public SortStyle getSortStyle() {
        return mSort;
    }

    public void setFileFirst(boolean f) {
        mFileFirst = f;
    }

    public Comparator getComparator() {
        return mComparatorList.get(mSort);
    }

    private abstract class FileComparator implements Comparator<FileInfo> {

        @Override
        public int compare(FileInfo object1, FileInfo object2) {
            if (object1.IsDir == object2.IsDir) {
                return doCompare(object1, object2);
            }

            if (mFileFirst) {
                // the files are listed before the dirs
                return (object1.IsDir ? 1 : -1);
            } else {
                // the dir-s are listed before the files
                return object1.IsDir ? -1 : 1;
            }
        }

        protected abstract int doCompare(FileInfo object1, FileInfo object2);
    }

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }
}
