package com.example.kiylx.ti.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Star_Dialog extends DialogFragment {
    private GetInfo mGetInfo;
    private AboutStar mAboutStar;
    private AboutTag mAboutTag;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAboutStar = AboutStar.get(context);
        mAboutTag = AboutTag.get(context);

        /*获取tag列表，tag列表这个字符数组是static的。
        if(!mAboutStar.fileExist()){
            //创建文件，如果不存在的话
            mAboutStar.WriteContent(getActivity(),"未分类","TAG_0");
        }
        tagList=mAboutStar.getDataFromFile(context,"TAG_0");
        tagList[0]="第一个选项";
        tagList[1]="第二个选项";*/

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGetInfo=(GetInfo)getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //onCreateDialog在onCreate之后，onCreateView之前被调用
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.star_webpage_dialog,null);
        setMassage(view);//填充网页信息
        setTags(view);//填充微调框

        builder.setView(view)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WebPage_Info tmp =getMessage(view);
                //把网页加入收藏database;查询网页是否被收藏再决定是收藏还是更新
                if(mAboutStar.isStar(tmp)){
                    /*已经收藏了，更新数据库信息，这里的更新是更新标题和tag，如果还被用户瞎改了网址，也要更新。
                     *网址未修改，那就更新标题和tag，
                     *如果网址被修改，那就算是一个新的，之后插入数据库*/
                    mAboutStar.updateItem(tmp);
                    /*判断tag文件里是否有当前写的tag，如果没有，那就添加进tag文件。
                     *当点击spinner时要读取tag文件，转换成arraylist，放进spinner。*/
                }else{
                    //否则往数据库添加条目信息
                mAboutStar.add(tmp);}
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭dialog
            }
        });
        return builder.create();

    }

    private void setTags(View view) {
        //填充微调框
        ArrayList<String> lists =mAboutTag.getItems();
        Spinner spinner = view.findViewById(R.id.tag_select);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item,lists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public interface GetInfo{
        //获取当前网页信息以填充收藏窗口
        public WebPage_Info getInfo();
    }

    private void setMassage(View v) {
        //拿到当前网页信息填充收藏框
        WebPage_Info info = mGetInfo.getInfo();
        EditText title=v.findViewById(R.id.edit_title);
        title.setText(info.getTitle());
        EditText url=v.findViewById(R.id.editUrl);
        url.setText(info.getUrl());
        TextView tags=v.findViewById(R.id.editTags);

    }
    private WebPage_Info getMessage(View v){
        //获取收藏框的信息
        EditText title=v.findViewById(R.id.edit_title);
        EditText url=v.findViewById(R.id.editUrl);
        EditText tags=v.findViewById(R.id.editTags);
        WebPage_Info info=new WebPage_Info(title.getText().toString(),url.getText().toString(),tags.getText().toString(),-1);
        return info;
    }

    @Override
    public void onStart() {

        super.onStart();

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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
