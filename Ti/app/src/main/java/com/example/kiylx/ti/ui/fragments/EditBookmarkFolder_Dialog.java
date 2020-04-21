package com.example.kiylx.ti.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.managercore.AboutBookmark;
import com.example.kiylx.ti.managercore.BookMarkFolderManager;
import com.example.kiylx.ti.myInterface.RefreshBookMark;
import com.example.kiylx.ti.R;

import java.util.Objects;

/**
 * 编辑书签文件夹时，打开这个对话框。
 */
public class EditBookmarkFolder_Dialog extends DialogFragment {
    private BookMarkFolderManager mBookMarkFolderManager;
    private EditText view1;
    private Context mContext;
    private static String folderName;//被传入的时候是旧有的名称
    private String oldFoldername;//保存没被修改的标签，在修改标签时会用到
    private String tmp2;
    private static final String TAG = "EditFolder_Dialog";
    private RefreshBookMark flashBookmark;


    /**
     * @param name 传入原有的文件夹名称，编辑文件夹名称。若传入null则是新建文件夹操作
     * @return fragmentdialog
     */
    public static EditBookmarkFolder_Dialog getInstance(@Nullable String name) {
        //编辑tag时会调用这个方法，并把它
        EditBookmarkFolder_Dialog editBookmarkFolder_dialog = new EditBookmarkFolder_Dialog();
        folderName = name;
        return editBookmarkFolder_dialog;
    }

    @NonNull
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
        mBookMarkFolderManager = BookMarkFolderManager.get(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_editbookmarkfolder, null);
        view1 = view.findViewById(R.id.editTagBox);
        mbuilder.setView(view);

        if (folderName != null) {
            //如果传入的文件夹名称不是null，说明是“编辑操作”，需要修改tag，并更新这个tag下的收藏记录
            view1.setText(folderName);

            oldFoldername = folderName;//备份原有的名称，在更新操作中会用到

            mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //更新文件夹名称
                    folderName = view1.getText().toString();//获取编辑框里改好的文件夹名称
                    Log.d(TAG, "onClick: tagname被改为" + folderName + "oldFoldername:" + oldFoldername + "tmp2:" + tmp2);

                    //更新该文件夹名称
                    mBookMarkFolderManager.updateitem(oldFoldername, folderName);
                    //更新该文件夹下的的书签
                    AboutBookmark bookmark = AboutBookmark.get(mContext);
                    bookmark.updateFolderforItems(oldFoldername, folderName);

                    //刷新BookmarkActivity里的视图
                    flashBookmark.refresh(folderName);
                    //if(getTargetRequestCode()==0)//只有BookmarkDialog调用过setTargetFragment()
                    returnResult();

                }
            });
        } else {
            //folderName是null，说明是“新建文件夹操作”，需要把它加入数据库
            mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*点击确定后调用returnResult方法*/
                    folderName = view1.getText().toString();
                    if (!(folderName.equals(""))) {
                        mBookMarkFolderManager.add(folderName);
                    }
                    if (getTargetFragment() == null) {
                        return;
                    }
                    //if(getTargetRequestCode()==0)
                    returnResult();//新建操作不仅是“书签activity”也有可能是“收藏fragment”启动的，所以这一句不能去掉

                }
            });
        }

        mbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return mbuilder.create();

    }

    /**
     * 包含返回给Bookmark_Dialog的数据，因为已经设置过targetFragment,直接调用“目标fragment”的onActivityResult方法
     */
    private void returnResult() {
        /*获取目标activity，调用目标activity的onActivityResult方法，传递数据*/
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        //上面把新添加的标签加入到数据库，这里再把它放进intent返回，这样，Bookmarkdialog就可以不用从数据库再读取一次数据，直接拿到返回的放进lists中刷新视图
        intent.putExtra("newTagName", folderName);
        getTargetFragment().onActivityResult(0, Activity.RESULT_OK, intent);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("EditBox", "onDetach");
        mContext = null;

    }

    public void setFlashBookmark(RefreshBookMark flashBookmark) {
        this.flashBookmark = flashBookmark;
    }
}
