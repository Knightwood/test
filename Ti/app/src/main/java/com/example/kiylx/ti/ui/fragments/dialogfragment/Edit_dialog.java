package com.example.kiylx.ti.ui.fragments.dialogfragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.interfaces.Edit_dialog_interface;
import com.example.kiylx.ti.tool.LogUtil;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/31 10:41
 * packageName：com.example.kiylx.ti.ui.fragments.dialogfragment
 * 描述：
 */
public class Edit_dialog extends DialogFragment {
    private Edit_dialog_interface edit_dialog_interface;

    private EditText view1;
    private String requestName;//被传入的时候是旧有的名称
    private String oldRequestName;//保存没被修改的标签，在修改标签时会用到
    private static final String TAG = "Edit_Dialog";
    private int setActionCode;


    /**
     * @param name 传入原有的文件夹名称，编辑文件夹名称。若传入null则是新建文件夹操作
     * @return fragmentdialog
     */
    public static Edit_dialog getInstance(@Nullable String name, @NonNull Edit_dialog_interface edit_interface, int actionCode) {
        //编辑tag时会调用这个方法，并把它
        Edit_dialog edit_dialog = new Edit_dialog();
        edit_dialog.setRequestName(name);
        edit_dialog.setEdit_dialog_interface(edit_interface);
        edit_dialog.setActionCode=actionCode;

        return edit_dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(requireContext());
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_editbookmarkfolder, null);
        view1 = view.findViewById(R.id.editTagBox);
        mbuilder.setView(view);


        view1.setText(requestName==null? "":requestName);
        oldRequestName = requestName;//备份原有的名称，在更新操作中会用到

        mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //更新request名称
                requestName = view1.getText().toString();//获取编辑框里改好的名称

                //更新该request的名称
                edit_dialog_interface.setResult(setActionCode,oldRequestName, requestName);
                returnResult();
            }
        });

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
        intent.putExtra("newName", requestName);
        getTargetFragment().onActivityResult(0, Activity.RESULT_OK, intent);
    }

    public void setEdit_dialog_interface(Edit_dialog_interface edit_dialog_interface) {
        this.edit_dialog_interface = edit_dialog_interface;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
}

