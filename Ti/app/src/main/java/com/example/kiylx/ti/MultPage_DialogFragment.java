package com.example.kiylx.ti;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


public class MultPage_DialogFragment extends DialogFragment {
private RecyclerView mRecyclerView;
private WebSiteAdapter mWebSiteAdapter;
private NewPagebutton_click mNewPagebutton_click;
private ImageButton mNewPageImageButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multpage_dialog,null);
        mRecyclerView=v.findViewById(R.id.mult_item);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        mNewPageImageButton = v.findViewById(R.id.newPagebutton);
        mNewPageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewPagebutton_click= (NewPagebutton_click) getActivity();
                assert mNewPagebutton_click != null;
                mNewPagebutton_click.click_newPagebutton();
                //执行操作后关闭对话框页面
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if(dialog!=null&&dialog.getWindow()!=null){
            Window window = dialog.getWindow();
            //指定显示位置
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            //指定显示大小
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            setCancelable(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public interface NewPagebutton_click {
        public void click_newPagebutton();
    }
    public interface DeletePage {
        public void delete_page(int position);
    }
    public interface SwitchPage{
        public void switchPage(int pos);
    }

    private void updateUI() {
        CurrentUse_WebPage_Lists mCurrect_list = CurrentUse_WebPage_Lists.get();
        ArrayList<WebPage_Info> lists=mCurrect_list.getPageList();
        if(null==mWebSiteAdapter){
            mWebSiteAdapter=new WebSiteAdapter(lists);
            mRecyclerView.setAdapter(mWebSiteAdapter);
            Log.d("MultPage_DialogFragment", "onClick: setAdapter方法被触发");
        }else{
            mWebSiteAdapter.notifyDataSetChanged();
        }
    }

    private class WebSiteAdapter extends RecyclerView.Adapter<WebsiteHolder>{
        private ArrayList<WebPage_Info> lists;

        public WebSiteAdapter(ArrayList<WebPage_Info> mlists) {
            this.lists = mlists;
            boolean ta=lists.isEmpty();
            Log.d("MultPage_DialogFragment", "onClick: Adapter构造函数被触发"+ta);
        }

        @NonNull
        @Override
        public WebsiteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Log.d("MultPage_DialogFragment", "onClick: onCreateViewHolder构造方法被触发");
            View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
            return new WebsiteHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull WebsiteHolder websiteHolder, int i) {
            Log.d("MultPage_DialogFragment", "onClick: onBindViewHolder方法被触发");
            websiteHolder.bind(lists.get(i),i);
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private class WebsiteHolder extends ViewHolder implements View.OnClickListener{
        TextView textView;
        ImageButton imageButton;
        private WebPage_Info minfo;
        private DeletePage mDeletePage;
        private SwitchPage mSwitchPage;
        private int pos;
        public WebsiteHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.website_item);
            imageButton = itemView.findViewById(R.id.close_button);
            Log.d("MultPage_DialogFragment", "onClick: WebsiteHolder构造方法被触发");
            textView.setOnClickListener(this);
            imageButton.setOnClickListener(this);

        }
        public void bind(WebPage_Info item_info,int pos){
            minfo=item_info;
            this.pos=pos;
            //获取点击的item的位置，也就是webview在list的位置，方便后面使用
            String title=minfo.getTitle();
            if(0==minfo.getFlags()){
                title=getString(R.string.new_tab);
            }
            textView.setText(title);
            Log.d("MultPage_DialogFragment", "onClick: bind方法被触发");

        }

        @Override
        public void onClick(View v) {
            switch ((v.getId())) {
                case R.id.close_button:
                    Log.d("MultPage_DialogFragment", "onClick: 多窗口关闭按钮被触发"+pos);
                    mDeletePage=(DeletePage) getActivity();
                    assert mDeletePage != null;
                    mDeletePage.delete_page(pos);
                    updateUI();
                    //删除完页面要更新视图
                    break;
                case R.id.website_item:
                    Log.d("MultPage_DialogFragment", "onClick: 网页切换按钮被触发");
                    mSwitchPage = (SwitchPage)getActivity();
                    assert mSwitchPage != null;
                    mSwitchPage.switchPage(pos);
                    dismiss();
                    break;
            }
        }

    }
}
