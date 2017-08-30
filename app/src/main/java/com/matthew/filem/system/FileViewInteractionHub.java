package com.matthew.filem.system;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.GridView;
import android.widget.ListView;

import com.matthew.filem.R;
import com.matthew.filem.activity.MainActivity;
import com.matthew.filem.activity.base.BaseActivity;
import com.matthew.filem.component.MenuFirstDialog;
import com.matthew.filem.component.PropertyDialog;
import com.matthew.filem.fragment.leftbar.CommonRightFragment;
import com.matthew.filem.impl.IFileInteractionListener;
import com.matthew.filem.info.FileInfo;
import com.matthew.filem.utils.Constants;
import com.matthew.filem.utils.L;
import com.matthew.filem.utils.LocalCacheLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FileViewInteractionHub implements FileOperationHelper.IOperationProgressListener {
    private static final String TAG = "FileViewInteractionHub DEBUG::";
    private static final int FILE_NAME_LEGAL = 0;
    private static final int FILE_NAME_NULL = 1;
    private static final int FILE_NAME_ILLEGAL = 2;
    private static final int FILE_NAME_WARNING = 3;
    private static final String LOG_TAG = "FileViewInteractionHub";
    private IFileInteractionListener mFileViewListener;
    public static Map<String,Integer> saveMulti = new HashMap<>();
    private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<>();
    private FileOperationHelper mFileOperationHelper;
    private FileSortHelper mFileSortHelper;
    private ProgressDialog progressDialog;
    private Context mContext;
    private CopyOrMove copyOrMoveMode;
    private int selectedDialogItem;
    private MenuFirstDialog menuFirstDialog;
    private MainActivity mMainActivity;

    private boolean mIsBlank = true;
    private boolean mIsDirectory = false;
    private boolean mIsProtected = true;
    private int mCompressFileState;
    private boolean mConfirm;

    public enum Mode {
        View, Pick
    }

    public enum CopyOrMove {
        Copy, Move
    }

    public FileViewInteractionHub(IFileInteractionListener fileViewListener) {
        assert (fileViewListener != null);
        mFileViewListener = fileViewListener;
        setup();
        mFileOperationHelper = new FileOperationHelper(this);
        mContext = mFileViewListener.getContext();
        mFileSortHelper = ((BaseActivity)mContext).getFileSortHelper();
        mMainActivity = (MainActivity) mContext;
    }

    private void showProgress(String msg) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
    }

    public void sortCurrentList() {
        mFileViewListener.sortCurrentList(mFileSortHelper);
    }

/*    public boolean canShowCheckBox() {
        return true;
    }*/
    public void addDialogSelectedItem(FileInfo fileInfo) {
        mCheckedFileNameList.add(fileInfo);
    }

/*    public void addDragSelectedItem(int position) {
        if (mCheckedFileNameList.size() == 0) {
            selectedDialogItem = position;
            if (selectedDialogItem != -1) {
                FileInfo fileInfo = mFileViewListener.getItem(selectedDialogItem);
                if (fileInfo != null) {
                    fileInfo.Selected = true;
                    mCheckedFileNameList.add(fileInfo);
                }
            }
        }
    }*/

    public void removeDialogSelectedItem(FileInfo fileInfo){
        mCheckedFileNameList.remove(fileInfo);
    }

    public ArrayList<FileInfo> getSelectedFileList() {
        return mCheckedFileNameList;
    }

    public ArrayList<FileInfo> getCheckedFileList() {
        return mFileOperationHelper.getFileList();
    }

/*    public void onOperationDragConfirm(String filePath) {
        if (isSelectingFiles()) {
            mSelectFilesCallback.selected(mCheckedFileNameList);
            mSelectFilesCallback = null;
            clearSelected();
        } else if (mFileOperationHelper.isMoveState()) {
            if (mFileOperationHelper.EndMove(filePath)) {
                showProgress(mContext.getString(R.string.operation_moving));
            }
        } else {
            onOperationDragPaste(filePath);
        }
    }*/

    public void onOperationDragPaste(String filePath) {
        if (mFileOperationHelper.Paste(filePath)) {
            showProgress(mContext.getString(R.string.operation_pasting));
        }
    }

    public void setCheckedFileList(ArrayList<FileInfo> fileInfoList, CopyOrMove copyOrMove) {
        if (fileInfoList != null && fileInfoList.size() > 0)
            mCheckedFileNameList.addAll(fileInfoList);
        switch (copyOrMove) {
            case Move:
                onOperationMove();
                break;
            default:
            case Copy:
                doOnOperationCopy();
                break;
        }
    }

    public CopyOrMove getCurCopyOrMoveMode() {
        return copyOrMoveMode;
    }

/*    public boolean canPaste() {
        return mFileOperationHelper.canPaste();
    }*/

    // operation finish notification
    @Override
    public void onFinish() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        mFileViewListener.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                showConfirmOperationBar(false);
                clearSelected();
                refreshFileList();
            }
        });
    }

/*    public FileInfo getItem(int pos) {
        return mFileViewListener.getItem(pos);
    }*/

    public boolean isInSelection() {
        return mCheckedFileNameList.size() > 0;
    }

    public boolean isMoveState() {
        return mFileOperationHelper.isMoveState() || mFileOperationHelper.canPaste();
    }

    private void setup() {
        setupFileListView();
    }

    public void onOperationReferesh() {
        refreshFileList();
    }

/*    private void onOperationSetting() {
        Intent intent = new Intent(mContext, FileManagerPreferenceActivity.class);
        try {
            mContext.startActivity(intent);
            clearSelected();
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "fail to start setting: " + e.toString());
        }
    }*/

    public void onOperationShowSysFiles() {
        Settings.instance().setShowDotAndHiddenFiles(!Settings.instance()
                                                     .getShowDotAndHiddenFiles());
        refreshFileList();
    }

    public void onOperationSelectAllOrCancel() {
        if (!isSelectedAll()) {
            onOperationSelectAll();
        } else {
            clearSelected();
        }
    }

    //ctrl + A
    public void onOperationSelectAll() {
        mCheckedFileNameList.clear();
        for (FileInfo f : mFileViewListener.getAllFiles()) {
            f.Selected = true;
            mCheckedFileNameList.add(f);
        }
        L.i(TAG, "onOperationSelectAll-- >" + mCheckedFileNameList.size());
        mFileViewListener.onDataRefresh();
    }

    public boolean onOperationUpLevel() {
        if (mFileViewListener.onOperation(Constants.OPERATION_UP_LEVEL)) {
            return true;
        }
        if (!mRoot.equals(mCurrentPath)) {
            mCurrentPath = new File(mCurrentPath).getParent();
            refreshFileList();
            return true;
        }
        return false;
    }

    public void onOperationCreateFolder() {
        TextInputDialog dialog = new TextInputDialog(
                                 mContext,
                                 mContext.getString(R.string.operation_create_folder),
                                 mContext.getString(R.string.operation_create_folder_message),
                                 mContext.getString(R.string.new_folder_name),
                                 new TextInputDialog.OnFinishListener() {
                                     @Override
                                     public boolean onFinish(String text) {
                                         return doCreateFolder(text);
                                     }
                                 }
        );
        dialog.show();
    }

    public void onOperationCreateFile() {
        CreateFileDialog dialog = new CreateFileDialog(
                                 mContext,
                                 mContext.getString(R.string.operation_create_file),
                                 mContext.getString(R.string.operation_create_file_message),
                                 mContext.getString(R.string.new_file_name),
                                 new CreateFileDialog.OnFinishListener() {
                                     @Override
                                     public boolean onFinish(String text) {
                                         return doCreateFile(text);
                                     }
                                 }
        );
        dialog.show();
    }

    private boolean doCreateFolder(final String text) {
        switch (isValidFileName(text)) {
            case FILE_NAME_LEGAL:
                return createFolder(text);
            case FILE_NAME_NULL:
                failDialog().setMessage(R.string.file_name_not_null).create().show();
                return false;
            case FILE_NAME_ILLEGAL:
                failDialog().setMessage(R.string.file_name_illegal).create().show();
                return false;
            case FILE_NAME_WARNING:
                DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConfirm = createFolder(text);
                    }
                };
                DialogInterface.OnClickListener cancelClick =
                    new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConfirm = false;
                    }
                };
                warnDialog(okClick, cancelClick).setMessage(R.string.file_name_warning)
                                                .create().show();
                break;
        }
        return mConfirm;
    }

    private boolean createFolder(String text) {
        if (mFileOperationHelper.CreateFolder(mCurrentPath, text)) {
            mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(mCurrentPath, text)));
            if ("list".equals(LocalCacheLayout.getViewTag())) {
                mFileListView.setSelection(mFileListView.getCount() - 1);
            } else if ("grid".equals(LocalCacheLayout.getViewTag())) {
                mFileGridView.setSelection(mFileGridView.getCount() - 1);
            }
            clearSelected();
        } else {
            new AlertDialog.Builder(mContext)
                    .setMessage(mContext.getString(R.string.fail_to_create_folder))
                    .setPositiveButton(R.string.confirm, null).create().show();
            clearSelected();
            return false;
        }

        return true;
    }

    private AlertDialog.Builder failDialog() {
        AlertDialog.Builder failDialog = new AlertDialog.Builder(mContext)
                .setPositiveButton(R.string.confirm, null);
        return failDialog;
    }

    private AlertDialog.Builder warnDialog(DialogInterface.OnClickListener okClick,
                                           DialogInterface.OnClickListener cancelClick) {
        AlertDialog.Builder warnDialog = new AlertDialog.Builder(mContext)
                .setPositiveButton(R.string.confirm,okClick)
                .setNegativeButton(R.string.cancel, cancelClick);
        return warnDialog;
    }

    private boolean doCreateFile(final String text) {
        switch (isValidFileName(text)) {
            case FILE_NAME_LEGAL:
                return createFile(text);
            case FILE_NAME_NULL:
                failDialog().setMessage(R.string.file_name_not_null).create().show();
                return false;
            case FILE_NAME_ILLEGAL:
                failDialog().setMessage(R.string.file_name_illegal).create().show();
                return false;
            case FILE_NAME_WARNING:
                DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConfirm = createFile(text);
                    }
                };
                DialogInterface.OnClickListener cancelClick =
                    new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConfirm = false;
                    }
                };
                warnDialog(okClick, cancelClick).setMessage(R.string.file_name_warning)
                                                .create().show();
                break;
        }
        return mConfirm;
    }

    private boolean createFile(String text) {
        if (mFileOperationHelper.CreateFile(mCurrentPath, text)) {
            mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(mCurrentPath, text)));
            if ("list".equals(LocalCacheLayout.getViewTag())) {
                mFileListView.setSelection(mFileListView.getCount() - 1);
            } else if ("grid".equals(LocalCacheLayout.getViewTag())) {
                mFileGridView.setSelection(mFileGridView.getCount() - 1);
            }
            clearSelected();
        } else {
            new AlertDialog.Builder(mContext)
                           .setMessage(mContext.getString(R.string.fail_to_create_folder))
                           .setPositiveButton(R.string.confirm, null).create().show();
            clearSelected();
            return false;
        }
        return true;
    }

    //TODO
    public void onSortChanged(FileSortHelper.SortStyle s) {
        if (mFileSortHelper.getSortStyle() != s) {
            mFileSortHelper.setSortMethog(s);
            sortCurrentList();
        }
    }

    public void doOnOperationCopy() {
        copyOrMoveMode = CopyOrMove.Copy;
        onOperationCopy(getSelectedFileList());
    }

    public void onOperationCopy(ArrayList<FileInfo> files) {
        mFileOperationHelper.Copy(files);
    }

    public void onOperationCopyPath() {
        if (getSelectedFileList().size() == 1) {
            copy(getSelectedFileList().get(0).filePath);
        }
        clearSelected();
    }

    private void copy(CharSequence text) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(
                Context.CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    public void onOperationPaste() {
        if (mFileOperationHelper.Paste(mCurrentPath)) {
            showProgress(mContext.getString(R.string.operation_pasting));
        }
    }

    public void onOperationMove() {
        mFileOperationHelper.StartMove(getSelectedFileList());
        clearSelected();
        refreshFileList();
        copyOrMoveMode = CopyOrMove.Move;
    }

    public void refreshFileList() {
        clearSelected();
        updateNavigationPane();
        mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);
    }

    private void updateNavigationPane() {
        ((MainActivity) mContext).setFilePathBar(mFileViewListener.getDisplayPath(mCurrentPath));
    }

    public void onOperationSend() {
        ArrayList<FileInfo> selectedFileList = getSelectedFileList();
        for (FileInfo f : selectedFileList) {
            if (f.IsDir) {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                                         .setMessage(R.string.error_info_cant_send_folder)
                                         .setPositiveButton(R.string.confirm, null).create();
                dialog.show();
                return;
            }
        }

        Intent intent = IntentBuilder.buildSendFile(selectedFileList);
        if (intent != null) {
            try {
                mFileViewListener.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(LOG_TAG, "fail to view file: " + e.toString());
            }
        }
        clearSelected();
    }

    public void onOperationRename() {
        int pos = selectedDialogItem;
        if (pos == -1)
            return;

        if (getSelectedFileList().size() == 0)
            return;

        final FileInfo f = getSelectedFileList().get(0);

        TextInputDialog dialog = new TextInputDialog(mContext,
                                     mContext.getString(R.string.operation_rename),
                                     mContext.getString(R.string.operation_rename_message),
                                     f.fileName,
                                     new TextInputDialog.OnFinishListener() {
                                         @Override
                                         public boolean onFinish(String text) {
                                             return doRename(f, text);
                                         }
                                     }
        );

        dialog.show();
    }

    private boolean doRename(final FileInfo f, final String text) {
        switch (isValidFileName(text)) {
            case FILE_NAME_LEGAL:
                return rename(f, text);
            case FILE_NAME_NULL:
                failDialog().setMessage(R.string.file_name_not_null).create().show();
                return false;
            case FILE_NAME_ILLEGAL:
                failDialog().setMessage(R.string.file_name_illegal).create().show();
                return false;
            case FILE_NAME_WARNING:
                DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConfirm = rename(f, text);
                    }
                };
                DialogInterface.OnClickListener cancelClick =
                    new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConfirm = false;
                    }
                };
                warnDialog(okClick, cancelClick).setMessage(R.string.file_name_warning)
                                                .create().show();
                break;
        }
        return mConfirm;
    }

    private boolean rename(FileInfo f, String text) {
        String newPath = Util.makePath(Util.getPathFromFilepath(f.filePath), text);
        if (f.filePath.equals(newPath)) {
            return true;
        }
        if (mFileOperationHelper.Rename(f, text)) {
            f.fileName = text;
            mFileViewListener.onDataRefresh();
        } else {
            new AlertDialog.Builder(mContext)
                    .setMessage(mContext.getString(R.string.fail_to_rename))
                    .setPositiveButton(R.string.confirm, null).create().show();
            return false;
        }
        refreshFileList();
        return true;
    }

    private int isValidFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return FILE_NAME_NULL;
        } else {
            if (fileName.indexOf("/") != -1) {
                return FILE_NAME_ILLEGAL;
            }
            if (!Pattern.compile("[^@#\\$\\^&*\\(\\)\\[\\]]*").matcher(fileName).matches()) {
                return FILE_NAME_WARNING;
            }
            return FILE_NAME_LEGAL;
        }
    }

    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;
        final File f = new File(path);
        /*Build.VERSION_CODES.KITKAT*/
        if (Build.VERSION.SDK_INT >= 19) {
            String[] paths;
            paths = new String[]{path};
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        } else {
            final Intent intent;
            if (f.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media",
                                    "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
                Log.v(LOG_TAG, "directory changed, send broadcast:" + intent.toString());
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(path)));
                Log.v(LOG_TAG, "file changed, send broadcast:" + intent.toString());
            }
            mContext.sendBroadcast(intent);
        }
    }

    public void onOperationDelete() {
        doOperationDelete(getSelectedFileList());
    }

    public void onOperationDeleteDirect() {
        doOperationDeleteDirect(getSelectedFileList());
    }

    public void onOperationDelete(int position) {
        FileInfo file = mFileViewListener.getItem(position);
        if (file == null) {
            return;
        }
        ArrayList<FileInfo> selectedFileList = new ArrayList<FileInfo>();
        selectedFileList.add(file);
        doOperationDelete(selectedFileList);
    }

    private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
        final ArrayList<FileInfo> selectedFiles = new ArrayList<>(selectedFileList);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        if (selectedFileList.size() == 0) {
            return;
        }
        String path = selectedFiles.get(0).filePath;
        if (path.equals(FileOperationHelper.RECYCLE_PATH1)
                || path.equals(FileOperationHelper.RECYCLE_PATH2)
                || path.equals(FileOperationHelper.RECYCLE_PATH3)) {
            //clean Recycle
            dialog.setMessage(mContext.getString(R.string.delete_dialog_clean));
        } else if (path.contains(FileOperationHelper.RECYCLE_PATH1)
                || path.contains(FileOperationHelper.RECYCLE_PATH2)
                || path.contains(FileOperationHelper.RECYCLE_PATH3)
                || (path.split("/").length > 3 && path.startsWith("/storage/usb"))) {
            //delete file
            dialog.setMessage(mContext.getString(R.string.delete_dialog_delete));
        } else {
            //move to Recycle
            dialog.setMessage(mContext.getString(R.string.delete_dialog_move));
        }

        dialog.setPositiveButton(R.string.confirm, new DeleteClickListener(selectedFiles))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void doOperationDeleteDirect(final ArrayList<FileInfo> selectedFileList) {
        final ArrayList<FileInfo> selectedFiles = new ArrayList<>(selectedFileList);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        if (selectedFileList.size() == 0) {
            return;
        }
        String path = selectedFiles.get(0).filePath;
        if (path.equals(FileOperationHelper.RECYCLE_PATH1)
                || path.equals(FileOperationHelper.RECYCLE_PATH2)
                || path.equals(FileOperationHelper.RECYCLE_PATH3)) {
            //clean Recycle
            dialog.setMessage(mContext.getString(R.string.delete_dialog_clean));
        } else {
            //delete file
            dialog.setMessage(mContext.getString(R.string.delete_dialog_delete));
        }

        dialog.setPositiveButton(R.string.confirm, new DeleteDirectClickListener(selectedFiles))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.show();
    }

    class DeleteDirectClickListener implements DialogInterface.OnClickListener {
        ArrayList<FileInfo> mFiles;

        public DeleteDirectClickListener(ArrayList<FileInfo> selectedFiles) {
            super();
            mFiles = selectedFiles;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            new DeleteDirectThread(mFiles).start();
        }
    }

    class DeleteDirectThread extends Thread {
        ArrayList<FileInfo> mFiles;

        public DeleteDirectThread(ArrayList<FileInfo> selectedFiles) {
            super();
            mFiles = selectedFiles;
        }

        @Override
        public void run() {
            super.run();
            mFileOperationHelper.deleteDirectFile(mFiles);
        }
    }

    class DeleteClickListener implements DialogInterface.OnClickListener {
        ArrayList<FileInfo> mFiles;

        public DeleteClickListener(ArrayList<FileInfo> selectedFiles) {
            super();
            mFiles = selectedFiles;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            new DeleteThread(mFiles).start();
        }
    }

    class DeleteThread extends Thread {
        ArrayList<FileInfo> mFiles;

        public DeleteThread(ArrayList<FileInfo> selectedFiles) {
            super();
            mFiles = selectedFiles;
        }

        @Override
        public void run() {
            super.run();
            mFileOperationHelper.deleteFile(mFiles);
        }
    }

    public void onOperationInfo() {
        if (getSelectedFileList().size() == 0)
            return;

        FileInfo file = getSelectedFileList().get(0);
        if (file == null)
            return;
        PropertyDialog propertyDialog = new PropertyDialog(mContext, file.filePath);
        propertyDialog.showDialog();
    }

    public void onOperationButtonConfirm() {
        if (isSelectingFiles()) {
            mSelectFilesCallback.selected(mCheckedFileNameList);
            mSelectFilesCallback = null;
            clearSelected();
        } else if (mFileOperationHelper.isMoveState()) {
            if (mFileOperationHelper.EndMove(mCurrentPath)) {
                showProgress(mContext.getString(R.string.operation_moving));
            }
        } else {
            onOperationPaste();
        }
    }

    public void onOperationCompress() {
    }

    public void onOperationDecompress() {
        if (getSelectedFileList().size() == 0) {
            return;
        }
        final FileInfo file = getSelectedFileList().get(0);
        if (file == null) {
            return;
        }
        String[] files = FileOperationHelper.list(file.filePath);
        for (String s : files) {
            for (FileInfo info : ((CommonRightFragment) mFileViewListener).getAllFiles()) {
                if (info.fileName.equals(s)) {
                    new AlertDialog.Builder(mMainActivity)
                         .setMessage(String.format(mMainActivity.getResources().getString(
                                                            R.string.dialog_decompress_text), s))
                         .setPositiveButton(mMainActivity.getResources().getString(
                                                            R.string.dialog_delete_yes),
                             new android.content.DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     new Thread(){
                                         @Override
                                         public void run() {
                                             super.run();
                                             FileOperationHelper.decompress(file.filePath);
                                         }
                                     }.start();
                                 }
                             })
                         .setNegativeButton(mMainActivity.getResources().getString(
                                                            R.string.dialog_delete_no),
                             new android.content.DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     dialog.cancel();
                                 }
                             }).show();
                    return;
                }
            }
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                FileOperationHelper.decompress(file.filePath);
            }
        }.start();
    }

    public void onOperationButtonCancel() {
        mFileOperationHelper.clear();
//        showConfirmOperationBar(false);
        if (isSelectingFiles()) {
            mSelectFilesCallback.selected(null);
            mSelectFilesCallback = null;
        } else if (mFileOperationHelper.isMoveState()) {
            // refresh to show previously selected hidden files
            mFileOperationHelper.EndMove(null);
            refreshFileList();
        } else {
            refreshFileList();
        }
    }

    // File List view setup
    private GridView mFileGridView;
    private ListView mFileListView;

    private void setupFileListView() {
        final String title = LocalCacheLayout.getViewTag();
        if ("list".equals(title)) {
            mFileListView = (ListView) mFileViewListener.getViewById(R.id.right_layout_list);
        } else if ("grid".equals(title)) {
            mFileGridView = (GridView) mFileViewListener.getViewById(R.id.right_layout_grid);
        }
    }

    private FileViewInteractionHub.Mode mCurrentMode;
    private String mCurrentPath;
    private String mRoot;
    private CommonRightFragment.SelectFilesCallback mSelectFilesCallback;
    public boolean isFileSelected(String filePath) {
        return mFileOperationHelper.isFileSelected(filePath);
    }

    public void onListItemClick(int position, String doubleTag,
                                MotionEvent event, FileInfo fileInfo) {
        if (fileInfo == null) {
            Log.e(LOG_TAG, "file does not exist on position:" + position);
            return;
        }

        if (!fileInfo.IsDir && doubleTag != null) {
            if ("PickerActivity".equals(mContext.getClass().getSimpleName())) {
                mFileViewListener.onPick(fileInfo);
            } else {
                viewFile(fileInfo,event);
            }
        } else if (doubleTag != null && Constants.DOUBLE_TAG.equals(doubleTag)) {
//            mCheckedFileNameList.remove(lFileInfo);  //
            ((CommonRightFragment) mFileViewListener).getAdapter().getSelectFileList().clear();
            clearSelected();
            mCurrentPath = getAbsoluteName(mCurrentPath, fileInfo.fileName);
            refreshFileList();
            mMainActivity.setCurPath(mCurrentPath);
        }
    }

    public void onOperationOpen(MotionEvent event) {
        if (getSelectedFileList().size() != 0) {
            FileInfo fileInfo = getSelectedFileList().get(0);
            onListItemClick(selectedDialogItem, Constants.DOUBLE_TAG, event, fileInfo);
        }
    }

    public void setBackground(int position, FileInfo lFileInfo) {
        if (lFileInfo == null) {
            Log.e(LOG_TAG, "file does not exist on position:" + position);
            return;
        }
        if (!lFileInfo.Selected ) {
            lFileInfo.Selected = true;

            mCheckedFileNameList.add(lFileInfo);
//            view.setSelected(true);
        } else {
            lFileInfo.Selected = false;
            mCheckedFileNameList.remove(lFileInfo);
//            view.setSelected(false);
        }
//        lFileInfo.Selected = !selected;

        L.e("mCheckedFileNameList", mCheckedFileNameList.size() + "");
    }

/*    public void setMode(Mode m) {
        mCurrentMode = m;
    }

    public Mode getMode() {
        return mCurrentMode;
    }*/

    public void setRootPath(String path) {
        mRoot = path;
        mCurrentPath = path;
    }

    public String getRootPath() {
        return mRoot;
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    public void setCurrentPath(String path) {
        mCurrentPath = path;
    }

    private String getAbsoluteName(String path, String name) {
        return path.equals(Constants.SD_PATH) ? path + name : path + File.separator + name;
    }

/*    // check or uncheck
    public boolean onCheckItem(FileInfo f, View v) {
        if (isMoveState())
            return false;

        if (isSelectingFiles() && f.IsDir)
            return false;

        if (f.Selected) {
            mCheckedFileNameList.add(f);
        } else {
            mCheckedFileNameList.remove(f);
        }
        return true;
    }*/

    private boolean isSelectingFiles() {
        return mSelectFilesCallback != null;
    }

    public boolean isSelectedAll() {
        return mFileViewListener.getItemCount() != 0
               && mCheckedFileNameList.size() == mFileViewListener.getItemCount();
    }

    public void clearSelected() {
        if (mCheckedFileNameList.size() > 0) {
            L.i(TAG, "clearSelected::-- >" + mCheckedFileNameList.size());
            for (FileInfo f : mCheckedFileNameList) {
                if (f == null) {
                    continue;
                }
                f.Selected = false;
            }
            mCheckedFileNameList.clear();
            mFileViewListener.onDataRefresh();
        }
    }

    private void viewFile(FileInfo lFileInfo, MotionEvent event) {
        try {
            IntentBuilder.viewFile(mContext, lFileInfo.filePath, event);
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "fail to view file: " + e.toString());
        }
    }

    public boolean onBackPressed() {
        if (isInSelection()) {
            clearSelected();
        } else if (!onOperationUpLevel()) {
            return false;
        }
        return true;
    }

    @Override
    public void onFileChanged(String path) {
        notifyFileSystemChanged(path);
    }


    public void showContextDialog(FileViewInteractionHub fileViewInteractionHub,
                                   MotionEvent event) {
           if (mCurrentPath.startsWith(Constants.PERMISS_DIR_SDCARD)
                || mCurrentPath.startsWith(Constants.PERMISS_DIR_STORAGE_SDCARD)
                || mCurrentPath.startsWith(Constants.PERMISS_DIR_STORAGE_USB)
                || mCurrentPath.startsWith(Constants.PERMISS_DIR_STORAGE_EMULATED_LEGACY)
                || mCurrentPath.startsWith(Constants.PERMISS_DIR_SEAFILE)
                || mCurrentPath.startsWith(Constants.PERMISS_DIR_STORAGE_EMULATED_0)) {
            fileViewInteractionHub.setIsProtected(false);
        } else {
            fileViewInteractionHub.setIsProtected(true);
        }
        menuFirstDialog = new MenuFirstDialog(mContext, fileViewInteractionHub, event);
        menuFirstDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuFirstDialog.showDialog((int) event.getRawX(), (int) event.getRawY());
    }

    public void dismissContextDialog() {
        if (menuFirstDialog != null) {
            menuFirstDialog.dismiss();
            menuFirstDialog = null;
        }
    }

    public MainActivity getMainActivity() {
        return mMainActivity;
    }

    public void setIsBlank(boolean isBlank) {
        mIsBlank = isBlank;
    }

    public void setIsDirectory(boolean isDirectory) {
        mIsDirectory = isDirectory;
    }

    public void setCompressFileState(int compressFileState) {
        mCompressFileState = compressFileState;
    }

    public void setIsProtected(boolean isProtected) {
        mIsProtected = isProtected;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public boolean isBlank() {
        return mIsBlank;
    }

    public int getCompressFileState() {
        return mCompressFileState;
    }

    public boolean isProtected() {
        return mIsProtected;
    }
}
