package com.example.kiylx.ti;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multpage_dialog,null);
        mRecyclerView=v.findViewById(R.id.mult_item);
        updateUI();
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

    private void updateUI() {
CurrentUse_WebPage_Lists mCurrect_list = CurrentUse_WebPage_Lists.get();
        ArrayList<WebPage_Info> lists=mCurrect_list.getPageList();
        if(null==mWebSiteAdapter){
            mWebSiteAdapter=new WebSiteAdapter(lists);
            mRecyclerView.setAdapter(mWebSiteAdapter);
        }else{

        }
    }

    private class WebSiteAdapter extends RecyclerView.Adapter<WebsiteHolder>{
        private ArrayList<WebPage_Info> lists;

        public WebSiteAdapter(ArrayList<WebPage_Info> mlists) {
            this.lists = mlists;
        }

        @NonNull
        @Override
        public WebsiteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
            return new WebsiteHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull WebsiteHolder websiteHolder, int i) {

        }


        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class WebsiteHolder extends ViewHolder implements View.OnClickListener{
        TextView textView;
        ImageButton imageButton;
        public WebsiteHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.website_item);
            imageButton = itemView.findViewById(R.id.close_button);

        }

        public void bind(){

        }

        @Override
        public void onClick(View v) {

        }
    }
}
