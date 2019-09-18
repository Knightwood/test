package com.example.kiylx.ti.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.EditBox;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;
import java.util.Objects;

public class Star_Dialog extends DialogFragment {
    private GetInfo mGetInfo;
    private AboutStar mAboutStar;
    private AboutTag mAboutTag;
    private WebPage_Info info;
    private PopupMenu mPopupMenu;
    TextView diaplaytagView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAboutStar = AboutStar.get(context);
        mAboutTag = AboutTag.get(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGetInfo=(GetInfo)getActivity();

        assert mGetInfo != null;
        info = mGetInfo.getInfo();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //onCreateDialog在onCreate之后，onCreateView之前被调用
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.star_webpage_dialog,null);
        setMassage(view);//填充网页信息

        diaplaytagView=view.findViewById(R.id.showTags);

        builder.setView(view)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                  如果是主页，那就不加入收藏夹，以后可能会有变动
                */

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
                    //否则往收藏数据库添加收藏条目信息和添加tag到tag数据库

                    mAboutTag.add(tmp.getWebTags());
                    mAboutStar.add(tmp);}
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭dialog
            }
        });
        diaplaytagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopMenu(v);
            }
        });
        return builder.create();

    }

   /*private void setTags(View view) {
        //填充微调框
        ArrayList<String> lists =mAboutTag.getItems();
        Spinner spinner = view.findViewById(R.id.tag_select);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item,lists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }*/

    public interface GetInfo{
        //获取当前网页信息以填充收藏窗口
        //在MainActivity中实现的
        public WebPage_Info getInfo();
    }

    private void setMassage(View v) {
        //拿到当前网页信息填充收藏框

        EditText title=v.findViewById(R.id.edit_title);
        title.setText(info.getTitle());
        EditText url=v.findViewById(R.id.editUrl);
        url.setText(info.getUrl());


    }
    private WebPage_Info getMessage(View v){
        //获取收藏框的信息
        EditText title=v.findViewById(R.id.edit_title);
        EditText url=v.findViewById(R.id.editUrl);
        TextView tags=v.findViewById(R.id.showTags);
        return new WebPage_Info(title.getText().toString(),url.getText().toString(),tags.getText().toString(),-1);
    }

    public void showPopMenu(View v) {
        mPopupMenu=new PopupMenu(Objects.requireNonNull(getActivity()),v);
        //添加固定的选项：添加新标签，点击后打开一个编辑框
        MenuInflater menuInflater = mPopupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_new_tag,mPopupMenu.getMenu());

        MenuBuilder menuBuilder= (MenuBuilder) mPopupMenu.getMenu();
        //存着tag的lists
        ArrayList<String> mItems=mAboutTag.getItems();
        if(mItems==null){
            //如果tag的lists是null，也就是空的，那什么tag也不会显示
            mPopupMenu.show();
            return;
        }
        for(int i=0;i<mItems.size();i++){
            //group通常为0
            //第二个参数是自己赋予item的id
            //第三个选项通常为0
            //第四个选项是item的名称
            menuBuilder.add(0,i,0,mItems.get(i));
        }
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.newTag) {
                    //启动一个dialog，里面放上编辑框
                    EditBox editBox=new EditBox();
                    FragmentManager fm =getFragmentManager();
                    editBox.show(fm,"编辑框");
                }
                int i=item.getItemId();
                Log.d("Star_Dialog_Popmenu", String.valueOf(i));

                //设置标签筛选的标题
                setTags(item.getTitle().toString());
                return false;
            }
        });
        mPopupMenu.show();
    }

    private void setTags(String str) {
        //TextView view=LayoutInflater.from(getActivity()).inflate(R.layout.star_webpage_dialog,null).findViewById(R.id.editTags);
        diaplaytagView.setText(str);
    }
    public void setEditwithTag(){
        //从tag选择里选择一个tag，点击确定会从showTags里拿取tag，保存在收藏里
        //如果在edit_Tag里填写了tag，那就把它设置到showTags里
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
}
