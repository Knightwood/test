package com.example.kiylx.ti.Fragments;

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

import com.example.kiylx.ti.AboutBookmark;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.INTERFACE.RefreshBookMark_recyclerview;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class EditBox_Dialog extends DialogFragment {
    private AboutTag mAboutTag;
    private EditText view1;
    private Context mContext;
    private static String newtagname;
    private String oldtagname;//保存没被修改的标签，在修改标签时会用到
    private String tmp2;
    private static final String TAG="EditBox_Dialog";
    private RefreshBookMark_recyclerview flashBookmark;


    public static EditBox_Dialog getInstance(){
        EditBox_Dialog editBox_dialog=new EditBox_Dialog();
        newtagname=null;
        return editBox_dialog;
    }
    public static EditBox_Dialog getInstance(String tagname){
        //编辑tag时会调用这个方法，并把它
        EditBox_Dialog editBox_dialog=new EditBox_Dialog();
        newtagname=tagname;
        return editBox_dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=getActivity();
        mAboutTag=AboutTag.get(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder mbuilder =new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        final View view=LayoutInflater.from(getContext()).inflate(R.layout.edit_box,null);
        view1 = view.findViewById(R.id.editTagBox);
        mbuilder.setView(view);

        if(newtagname != null){
            //如果tagname不是null，说明是“编辑操作”，需要修改tag，并更新这个tag下的收藏记录
            view1.setText(newtagname);

            oldtagname =newtagname;//备份原有的名称，在更新操作中会用到

            mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //更新tag
                    newtagname=view1.getText().toString();//tag名称更新
                    Log.d(TAG, "onClick: tagname被改为"+newtagname+"oldtagname:"+ oldtagname +"tmp2:"+tmp2);

                    mAboutTag.updateTag(oldtagname,newtagname);
                    //更新相关tag的书签
                    AboutBookmark bookmark=AboutBookmark.get(mContext);
                    bookmark.updateTagsforItems(oldtagname,newtagname);

                    //刷新BookmarkActivity里的视图
                    flashBookmark.refresh(newtagname);
                    returnResult();

                }
            });
        }else{
            //tagname是null，说明是“新建tag操作”，需要把tag加入数据库
            mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*点击确定后调用returnResult方法*/
                    newtagname = view1.getText().toString();
                    if (!(newtagname.equals(""))){
                        mAboutTag.add(newtagname);
                    }
                    if (getTargetFragment() == null) {
                        return;
                    }
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

    private void returnResult() {
        /*获取目标activity，调用目标activity的onActivityResult方法，传递数据*/
        if(getTargetFragment()==null){
            return;
        }
        Intent intent =new Intent();
        //上面把新添加的标签加入到数据库，这里再把它放进intent返回，这样，Bookmarkdialog就可以不用从数据库再读取一次数据，直接拿到返回的放进lists中刷新视图
        intent.putExtra("newTagName",newtagname);
        getTargetFragment().onActivityResult(0, Activity.RESULT_OK,intent);
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
        Log.d("EditBox","onDetach");
        mContext=null;

    }

    public void setFlashBookmark(RefreshBookMark_recyclerview flashBookmark) {
        this.flashBookmark = flashBookmark;
    }
}
