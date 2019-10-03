package com.example.kiylx.ti.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;
import java.util.Objects;

public class Star_Dialog extends DialogFragment {
    private AboutStar mAboutStar;
    private WebPage_Info beStared_info;
    private TextView tagadd;
    private Spinner mSpinner;
    private Context mContext;
    private ArrayList<String> taglists;
    private EditBox_Dialog mEditBoxDialog;
    private AboutTag mAboutTag;
    private ArrayAdapter<String> adapter;
    private static Star_DialogF_interface minterface;

/*打开tag的选择界面，也就是popmenu，如果选择新建tag，那就打开一个新的dialog，
里面只有一个edittext，编辑好tag后，加入数据库，返回选择tag选择界面，选择其中一个后，把他放在showTags里。
当点击确定后，把信息加入收藏夹数据库*/


    public static Star_Dialog newInstance(){
        Star_Dialog  star_dialog = new Star_Dialog();
        return star_dialog;
    }
    public void putInfo(WebPage_Info info){
        beStared_info = info;
    }

    @Override
    public void onAttach(Context context) {
        mContext=context;
        super.onAttach(mContext);
        mAboutStar = AboutStar.get(mContext);
        mAboutTag= AboutTag.get(mContext);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taglists=mAboutTag.getItems();
        taglists.add(0,"未分类");
    }
    public interface Star_DialogF_interface {
        void updatelistui();
    }
    public static void setInterface(Star_DialogF_interface minterface){
        Star_Dialog.minterface=minterface;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //onCreateDialog在onCreate之后，onCreateView之前被调用
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.star_webpage_dialog,null);
        setMassage(view);//填充网页信息

        tagadd=view.findViewById(R.id.tag_add);//添加新建tag dialog的关联
        tagadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEditBox();
            }
        });

        mSpinner =view.findViewById(R.id.select_Tags);
        selectTags(getPosinTaglists(beStared_info.getWebTags()));//设置tag的选项

        builder.setView(view)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*如果是主页，那就不加入收藏夹，以后可能会有变动*/
                //WebPage_Info tmp =getMessage(view);
                //把网页加入收藏database;查询网页是否被收藏再决定是收藏还是更新
                if(mAboutStar.isStar(beStared_info)){
                    /*已经收藏了，更新数据库信息，这里的更新是更新标题和tag，如果还被用户瞎改了网址，也要更新。
                     *网址未修改，那就更新标题和tag，
                     *如果网址被修改，那就算是一个新的，之后插入数据库*/
                    mAboutStar.updateItem(beStared_info);
                    /*判断tag文件里是否有当前写的tag，如果没有，那就添加进tag文件。
                     *当点击spinner时要读取tag文件，转换成arraylist，放进spinner。*/
                }else{
                    //否则往收藏数据库添加收藏条目信息
                    mAboutStar.add(beStared_info);}
                //更新StarpageActivity视图
                    minterface.updatelistui();
                Log.d("网页tag", "onClick: "+ beStared_info.getWebTags());
            }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭dialog


            }
        });
        return builder.create();

    }

   private void selectTags(int i) {
        //填充微调框
       if(adapter==null) {
           adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, taglists);
           adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       }else{
           adapter.notifyDataSetChanged();
       }mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag序号", "onItemSelected: "+position+"数组大小"+taglists.size());

                //选择某一个tag后更新webviewinfo信息
                updateWebinfo(taglists.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setSelection(i);

    }

    private void newEditBox() {
        //启动编辑tag的fragment
        mEditBoxDialog =new EditBox_Dialog();
        FragmentManager fm =getFragmentManager();
        //EditBox设置目标fragment为Star_Dialog Fragment。
        mEditBoxDialog.setTargetFragment(Star_Dialog.this,0);
        mEditBoxDialog.show(fm,"编辑框");
        //开启EditBox后关闭当前的Star_Dialog界面，之后会在编辑完tag时按下确定按钮后通过onActivity传回数据，之后调用宿主activity实现的回调方法刷新界面
    }

    private void updateWebinfo(String str){
        beStared_info.setWebTag(str);
    }

    private void setMassage(View v) {
        //拿到当前网页信息填充收藏框

        EditText title=v.findViewById(R.id.edit_title);
        title.setText(beStared_info.getTitle());
        EditText url=v.findViewById(R.id.editUrl);
        url.setText(beStared_info.getUrl());


    }
    private int getPosinTaglists(String str){
        //获取tag在lists中的位置，以此来设置spinner显示特定项
        if (str.equals("")){
            return 0;//如果网页信息中tag是空的，那就返回0，显示成：“未分类”。
        }
        return taglists.indexOf(str);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        //只有一个fragment与之关联，且此方法只有一个更新界面的方法，所以用不着验证是哪个fragment传来的
        Log.d("唉","iggs");
        //用新的list更新界面
        String tmptag=data.getStringExtra("newTagName");
        taglists.add(tmptag);
        selectTags(getPosinTaglists(tmptag));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Star_Dialog", "onDetach: ");
        mContext=null;

    }
}
/*
    private WebPage_Info getMessage(View v){
        //获取收藏框的信息
        EditText title=v.findViewById(R.id.edit_title);
        EditText url=v.findViewById(R.id.editUrl);

        return new WebPage_Info(title.getText().toString(),url.getText().toString(),"未分类",-1);
    }*/

    /*public void showPopMenu(View v) {
        mPopupMenu=new PopupMenu(Objects.requireNonNull(getActivity()),v);
        //添加固定的选项：添加新标签，点击后打开一个编辑框
       //MenuInflater menuInflater = mPopupMenu.getMenuInflater();
        //menuInflater.inflate(R.menu.menu_new_tag,mPopupMenu.getMenu());
        MenuBuilder menuBuilder= (MenuBuilder) mPopupMenu.getMenu();
        menuBuilder.add(0,-1,0,"添加标签");
        //存着tag的lists
        /*ArrayList<String> mItems=mAboutTag.getItems();
        if(mItems==null||mItems.isEmpty()){
            //如果tag的lists是null，也就是空的，那什么tag也不会显示
            mPopupMenu.show();
            return;
        }*/
/*
        for(int i=0;i<10;i++){
            //group通常为0
            //第二个参数是自己赋予item的id
            //第三个选项通常为0
            //第四个选项是item的名称
            menuBuilder.add(0,i,0,"bug"+i);

        }
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i=item.getItemId();
                if(i==-1){

                }else{
                Log.d("Star_Dialog_Popmenu", String.valueOf(i));
                //设置标签筛选的标题
                setTags(item.getTitle().toString());}
                return false;
            }
        });
        mPopupMenu.show();
    }*/

    /*private void setTags(String str) {
        diaplaytagView.setText(str);
    }*/