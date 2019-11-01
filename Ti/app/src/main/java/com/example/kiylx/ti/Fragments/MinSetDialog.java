package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kiylx.ti.Activitys.HistoryActivity;
import com.example.kiylx.ti.Activitys.SettingActivity;
import com.example.kiylx.ti.Activitys.BookmarkPageActivity;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.databinding.DialogHomepageSettingBinding;
import com.example.kiylx.ti.databinding.OptionsItemBinding;
import com.example.kiylx.ti.model.HomePageOptionsViewModel;

import static android.widget.Toast.LENGTH_SHORT;

public class MinSetDialog extends DialogFragment {
    /*设置，下载，收藏，历史记录，分享，隐身，工具箱，电脑模式*/
    private DialogHomepageSettingBinding homepageSettingBinding;
    private static final String TAG="MinSetDialog";
    private listAdapter mAdapter;
    private String[] optionslist=new String[]{"前进","分享","在页面上查找","添加到收藏夹","添加到阅读列表","设置"};
    private RecyclerView mRecyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homepageSettingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_homepage_setting, null, false);
        mRecyclerView= homepageSettingBinding.optionsRecyclerview;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(optionslist);
        return homepageSettingBinding.getRoot();
    }

    private void updateUI(String[] lists) {
        if (mAdapter==null){
            mAdapter=new listAdapter(lists);
        }else{
            mAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setAdapter(mAdapter);
    }


    public class listAdapter extends RecyclerView.Adapter<OptionViewHolder> {
        private String[] options;
        //SettingItemBinding mBinding;

        public listAdapter(String[] lists) {
            options = lists;
            Log.d(TAG, "onCreateView: "+options[0]);
        }

        public void setOptionList(String[] list) {
            this.options = list;
        }

        @NonNull
        @Override
        public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            OptionsItemBinding mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.options_item, parent, false);
            return new OptionViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
            holder.bind(options[position]);
            Log.d(TAG, "onCreateView: "+optionslist[position]);
        }

        @Override
        public int getItemCount() {
            return options.length;
        }
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OptionsItemBinding mBinding;

        public OptionViewHolder(@NonNull OptionsItemBinding binding) {
            super(binding.getRoot());
            mBinding=binding;
            mBinding.optionsName.setOnClickListener(this);
            mBinding.setOptionViewModel(new HomePageOptionsViewModel());

        }

        public void bind(String optionName) {
            mBinding.getOptionViewModel().setOptionsName(optionName);
            Log.d(TAG, "bind: "+optionName);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "点击选项 ");
            switch (v.getId()){

            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            window.setWindowAnimations(R.style.animate_dialog);
            window.setAttributes(lp);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }

    /**
     * 启动历史记录
     */
    private void startHistory(){
        Intent history_intent = new Intent(getActivity(), HistoryActivity.class);
        startActivity(history_intent);
        dismiss();
    }

    /**
     * 启动设置页面
     */
    private void startSetting(){
        startActivity(new Intent(getActivity(), SettingActivity.class));
        dismiss();
    }

    /**
     * 启动书签页面
     */
    private void startBookmarked(){
        Intent Bookmark_intent = new Intent(getContext(), BookmarkPageActivity.class);
        startActivity(Bookmark_intent);
        dismiss();
        Toast.makeText(getActivity(), "QQ", LENGTH_SHORT).show();
    }

    /**
     * 分享
     */
    private void sharing(){

    }
    private void find(){

    }
    private void addtobookmark(){

    }
    private void addtoreading(){

    }
    private void download(){

    }

}
