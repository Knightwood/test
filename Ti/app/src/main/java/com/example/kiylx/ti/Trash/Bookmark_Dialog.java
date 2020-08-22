package com.example.kiylx.ti.trash;

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

import com.example.kiylx.ti.interfaces.RefreshBookMark;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.BookmarkManagerActivity;
import com.example.kiylx.ti.ui.fragments.dialogfragment.EditBookmarkFolder_Dialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.UUID;

public class Bookmark_Dialog extends DialogFragment {
    private static final String TAG = "添加书签的弹窗";
    private AboutBookmark mAboutBookmark;
    private static WebPage_Info beBookmarked_info;
    private MaterialButton newFolderButton;
    private Spinner mSpinner;
    private Context mContext;
    private List<String> bookmarkFolderlists;
    private EditBookmarkFolder_Dialog mEditBoxDialog;
    private BookMarkFolderManager mBookMarkFolderManager;
    private ArrayAdapter<String> adapter;
    private static final String ARGME = "wholauncherI";
    private static int intentid;//谁启动了这个对话框
    private static RefreshBookMark refresh;

    //private View view;
    private EditText titleView;
    private EditText urlView;


/*打开tag的选择界面，也就是popmenu，如果选择新建tag，那就打开一个新的dialog，
里面只有一个edittext，编辑好tag后，加入数据库，返回选择tag选择界面，选择其中一个后，把他放在showTags里。
当点击确定后，把信息加入收藏夹数据库*/


    /**
     * @param id   标识是谁启动的intent的id
     * @param info 要添加进数据库的网页信息,url,title是必须的，bookmarkFolder可以为null，即将新添加到收藏网页，bookmarkFolder一定是null
     * @return bookmark_dialog
     * id标识是哪个activity启动的这个对话框,1表示是MainActivity，2表示是BookmarkPageActivity
     */
    public static Bookmark_Dialog newInstance(int id, WebPage_Info info) {

        Bookmark_Dialog bookmark_dialog = new Bookmark_Dialog();
        Bookmark_Dialog.intentid = id;
        /*Bookmark_Dialog.beBookmarked_info = new WebPage_Info.Builder(info.getUrl())
                .uuid(info.getUuid())
                .title(info.getTitle())
                .bookmarkFolderUUID(info.getBookmarkFolderUUID())
                .build();*/
        Bookmark_Dialog.beBookmarked_info=info;
        return bookmark_dialog;
    }

    /**
     * @param refresh 实现了RefreshBookMark_recyclerview中接口的对象
     */
    public static void setRefresh(RefreshBookMark refresh) {
        Bookmark_Dialog.refresh = refresh;
    }

    @NonNull
    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(mContext);
        mAboutBookmark = AboutBookmark.get(mContext);
        mBookMarkFolderManager = BookMarkFolderManager.get(mContext);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookmarkFolderlists = mBookMarkFolderManager.getBookmarkFolderlists();
    }

    @Override
    public void onStart() {
        super.onStart();

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
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //注：onCreateDialog在onCreate之后，onCreateView之前被调用
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_bookmark_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        beBookmarked_info.setTitle(titleView.getText().toString());
                        beBookmarked_info.setUrl(urlView.getText().toString());

                        if (beBookmarked_info.getUuid() == null) {
                            beBookmarked_info.setUuid(UUID.randomUUID());
                            mAboutBookmark.InsertItem(beBookmarked_info);
                            LogUtil.d(TAG, "onClick: uuid是null");
                        } else {
                            LogUtil.d(TAG, "onClick: uuid不是null，所以更新数据库");
                            mAboutBookmark.updateItem(beBookmarked_info);
                        }
                        //如果intentid是2(BookmarkPageActivity打开的，用来编辑已经存在的书签)，更新视图
                        if (intentid == 2) {
                            LogUtil.d(TAG, "onClick: " + beBookmarked_info.getBookmarkFolderUUID() + "intentid" + intentid);
                            refresh.refresh(null);
                        }

                    }
                });

        setMassage(view);//填充网页信息
        /*newFolderButton = view.findViewById(R.id.tag_add);//添加新建tag dialog的关联
        newFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEditBox();
            }
        });
        mSpinner = view.findViewById(R.id.select_Tags);*/
        //设置spinner显示的选项，即将新添加的网页的书签文件夹信息是null，这里会默认显示为“未分类”。
        selectOneFolder(mBookMarkFolderManager.getPosfromLists(beBookmarked_info.getBookmarkFolderUUID()));

        return builder.create();

    }

    /**
     * @param i 点击的条目在list中的位置
     *          选择某一个“文件夹”后，让spinner显示这个文件夹
     */
    private void selectOneFolder(int i) {
        //填充微调框
        if (adapter == null) {
            adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, bookmarkFolderlists);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } else {
            adapter.notifyDataSetChanged();
        }
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.d(TAG, "onItemSelected: " + position + "数组大小" + mBookMarkFolderManager.getSize());

                //选择某一个文件夹后更新webviewinfo信息
                beBookmarked_info.setBookmarkFolderUUID(bookmarkFolderlists.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //显示spinner中的第i项
        mSpinner.setSelection(i);

    }

    /**
     * 开启编辑“文件夹名称”的对话框
     * 并设置目标fragment
     * EditBox的目标fragment是Bookmark_Dialog
     */
    private void newEditBox() {
        //启动编辑书签文件夹名称的fragment
        mEditBoxDialog = EditBookmarkFolder_Dialog.getInstance(null);
        FragmentManager fm = getFragmentManager();

        //EditBox设置目标fragment为Bookmark_Dialog Fragment。
        mEditBoxDialog.setTargetFragment(Bookmark_Dialog.this, 0);

        mEditBoxDialog.show(fm, "编辑框");
        //开启EditBox后关闭当前的Bookmark_Dialog界面，之后会在编辑完tag时按下确定按钮后通过onActivity传回数据，之后调用宿主activity实现的回调方法刷新界面
    }

    /**
     * @param v 书签收藏对话框的布局
     *          解析布局，把beBookmarked_info的信息填充到收藏对话框
     */
    private void setMassage(View v) {

        titleView = v.findViewById(R.id.edit_title);
        titleView.setText(beBookmarked_info.getTitle());
        urlView = v.findViewById(R.id.editUrl);
        urlView.setText(beBookmarked_info.getUrl());

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
     *                    只有一个fragment与之关联(EditBookmarkFolder_Dialog)，且此方法只有一个更新界面的方法，所以用不着验证是哪个fragment传来的
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //只有一个fragment与之关联，且此方法只有一个更新界面的方法，所以用不着验证是哪个fragment传来的
        LogUtil.d(TAG, "onActivityResult");
        //用新的list更新界面
        String name = data.getStringExtra("newTagName");
        bookmarkFolderlists = mBookMarkFolderManager.getBookmarkFolderlists();
        selectOneFolder(bookmarkFolderlists.indexOf(name));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d(TAG, "onDetach: ");
        mContext = null;

    }

    private void selectFolder() {
        Intent intent = new Intent(getActivity(), BookmarkManagerActivity.class);
        intent.putExtra("isShowBookmarks", false);
        intent.putExtra("url",urlView.getText());
        intent.putExtra("title",titleView.getText());
        startActivity(intent);
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
        menuBuilder.InsertItem(0,-1,0,"添加标签");
        //存着tag的lists
        /*List<String> mItems=mBookMarkFolderManager.getfolderListfromDB();
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
            menuBuilder.InsertItem(0,i,0,"bug"+i);

        }
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i=item.getItemId();
                if(i==-1){

                }else{
                LogUtil.d("Bookmark_Dialog_Popmenu", String.valueOf(i));
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