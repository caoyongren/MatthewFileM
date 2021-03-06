package com.matthew.filem.model;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Wang Zhixu on 12/23/16.
 */

public class SeafileAccountInfo {
    public String mUserName;
    public int mUserId;
    public ArrayList<SeafileLibraryInfo> mLibrarys;
    public File mFile;

    public SeafileAccountInfo(){
        mLibrarys = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < mLibrarys.size(); i++) {
            sb.append("{\"id\":\"" + mLibrarys.get(i).libraryId);
            sb.append("\",\"name\":\"" + mLibrarys.get(i).libraryName + "\"},");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
