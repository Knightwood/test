package com.example.kiylx.ti.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kiylx.ti.AboutBookmark;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.INTERFACE.RefreshBookMark_recyclerview;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;
import java.util.Objects;

public class Bookmark_Dialog extends DialogFragment {
    private static final String TAG = "Bookmark_Dialog";
    private AboutBookmark mAboutBookmark;
    private WebPage_Info beBookmarked_info;
    private TextView tagadd;
    private Spinner mSpinner;
    private Context mContext;
    private ArrayList<String> taglists;
    private EditBox_Dialog mEditBoxDialog;
    private AboutTag mAboutTag;
    private ArrayAdapter<String> adapter;
    private static final String ARGME = "wholauncherI";
    private static int intentid;//谁启动了这个对话框
    private static RefreshBookMark_recyclerview refresh;


/*打开tag的选择界面，也就是popmenu，如果选择新建tag，那就打开一个新的dialog，
里面只有一个edittext，编辑好tag后，加入数据库，返回选择tag选择界面，选择其中一个后，把他放在showTags里。
当点击确定后，把信息加入收藏夹数据库*/


    /**
     * @param id 标识是谁启动的intent的id
     * @return bookmark_dialog
     * id标识是哪个activity启动的这个对话框,1表示是MainActivity，2表示是BookmarkPageActivity
     */
    public static Bookmark_Dialog newInstance(int id) {

        Bookmark_Dialog bookmark_dialog = new Bookmark_Dialog();
        Bookmark_Dialog.intentid = id;
        return bookmark_dialog;
    }

    /**
     * @param refresh 实现了RefreshBookMark_recyclerview中接口的对象
     */
    public static void setRefresh(RefreshBookMark_recyclerview refresh) {
        Bookmark_Dialog.refresh = refresh;
    }

    /**
     * @param info WebPage_info,用作填充在对话框
     */
    public void putInfo(WebPage_Info info) {
        beBookmarked_info = info;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(mContext);
        mAboutBookmark = AboutBookmark.get(mContext);
        mAboutTag = AboutTag.get(mContext);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taglists = mAboutTag.getTagListfromDB();

    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //指定显示位置
            layoutParams.gravity = Gravity.BOTTOM;
            //指定显示大小
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置背景，不然无法扩展到屏幕边缘
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(91, 90, 92)));
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }*/
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //onCreateDialog在onCreate之后，onCreateView之前被调用
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bookmark_webpage, null);
        setMassage(view);//填充网页信息

        tagadd = view.findViewById(R.id.tag_add);//添加新建tag dialog的关联
        tagadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEditBox();
            }
        });

        mSpinner = view.findViewById(R.id.select_Tags);
        //设置tag的选项
        selectTags(mAboutTag.getPosfromLists(beBookmarked_info.getWebTags()));

        builder.setView(view)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*如果是主页，那就不加入收藏夹，以后可能会有变动*/
                        //WebPage_Info tmp =getMessage(view);
                        //把网页加入收藏database;查询网页是否被收藏再决定是收藏还是更新
                        if (mAboutBookmark.isMarked(beBookmarked_info)) {
                            /*已经收藏了，更新数据库信息，这里的更新是更新标题和tag，如果还被用户瞎改了网址，也要更新。
                             *网址未修改，那就更新标题和tag，
                             *如果网址被修改，那就算是一个新的，之后插入数据库*/
                            mAboutBookmark.updateItem(beBookmarked_info);
                            /*判断tag文件里是否有当前写的tag，如果没有，那就添加进tag文件。
                             *当点击spinner时要读取tag文件，转换成arraylist，放进spinner。*/
                        } else {
                            //否则往收藏数据库添加收藏条目信息
                            mAboutBookmark.add(beBookmarked_info);
                        }

                        //如果intentid是2(BookmarkPageActivity)，更新视图
                        if (intentid == 2) {
                            Log.d("网页tag", "onClick: " + beBookmarked_info.getWebTags() + "intentid" + intentid);
                            refresh.refresh(null);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭dialog


            }
        });
        return builder.create();

    }

    /**
     * @param i 点击的条目在list中的位置
     *          选择某一个“标签”后
     */
    private void selectTags(int i) {
        //填充微调框
        if (adapter == null) {
            adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, taglists);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } else {
            adapter.notifyDataSetChanged();
        }
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag序号", "onItemSelected: " + position + "数组大小" + mAboutTag.getSize());

                //选择某一个tag后更新webviewinfo信息
                updateWebinfo(mAboutTag.getTagfromList(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //根据点击的位置，显示spinner中的选项
        mSpinner.setSelection(i);

    }

    /**
     * 开启编辑“标签”的对话框
     * 并设置目标fragment
     * EditBox的目标fragment是Bookmark_Dialog
     */
    private void newEditBox() {
        //启动编辑tag的fragment
        mEditBoxDialog = EditBox_Dialog.getInstance();
        FragmentManager fm = getFragmentManager();
        //EditBox设置目标fragment为Bookmark_Dialog Fragment。
        mEditBoxDialog.setTargetFragment(Bookmark_Dialog.this, 0);
        mEditBoxDialog.show(fm, "编辑框");
        //开启EditBox后关闭当前的Bookmark_Dialog界面，之后会在编辑完tag时按下确定按钮后通过onActivity传回数据，之后调用宿主activity实现的回调方法刷新界面
    }

    /**
     * @param str “标签”名称
     *            更新beBookmarked_info的“标签”信息
     */
    private void updateWebinfo(String str) {
        beBookmarked_info.setWebview_marked_name(str);
    }

    /**
     * @param v 书签收藏对话框的布局
     *          解析布局，把beBookmarked_info的信息填充到收藏对话框
     */
    private void setMassage(View v) {

        EditText title = v.findViewById(R.id.edit_title);
        title.setText(beBookmarked_info.getTitle());
        EditText url = v.findViewById(R.id.editUrl);
        url.setText(beBookmarked_info.getUrl());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    /**
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     *                    用于BookmarkPageActivity更新视图使用
     *                    只有一个fragment与之关联(EditBox_Dialog)，且此方法只有一个更新界面的方法，所以用不着验证是哪个fragment传来的
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //只有一个fragment与之关联，且此方法只有一个更新界面的方法，所以用不着验证是哪个fragment传来的
        Log.d("唉", "iggs");
        //用新的list更新界面
        String tmptag = data.getStringExtra("newTagName");
        selectTags(mAboutTag.getPosfromLists(tmptag));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Bookmark_Dialog", "onDetach: ");
        mContext = null;

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
        /*ArrayList<String> mItems=mAboutTag.getTagListfromDB();
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
                Log.d("Bookmark_Dialog_Popmenu", String.valueOf(i));
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