package com.example.kiylx.ti.myFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.kiylx.ti.activitys.DownloadActivity;
import com.example.kiylx.ti.activitys.HistoryActivity;
import com.example.kiylx.ti.activitys.SettingActivity;
import com.example.kiylx.ti.activitys.BookmarkPageActivity;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.core1.WebViewManager;
import com.example.kiylx.ti.databinding.DialogHomepageSettingBinding;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.myInterface.SearchTextOnWebview;
import com.example.kiylx.ti.myInterface.Setmessage;

/**
 * 主界面的功能界面
 */
public class MinSetDialog extends DialogFragment implements View.OnClickListener {
    /*设置，下载，收藏，历史记录，分享，隐身，工具箱，电脑模式*/
    private DialogHomepageSettingBinding homepageSettingBinding;
    private static final String TAG = "MinSetDialog";
    //private listAdapter mAdapter;
    //private String[] optionslist = new String[]{"隐身", "电脑模式", "分享", "在页面上查找", "添加到书签", "设置"};
    //private RecyclerView mRecyclerView;
    //private View mView;
    private static WebPage_Info info;
    private SearchTextOnWebview mInterface;
    private WebViewManager webManager;


    public static MinSetDialog newInstance(WebPage_Info info) {
        MinSetDialog fragment = new MinSetDialog();
        MinSetDialog.info = info;

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homepageSettingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_homepage_setting, null, false);
        //mRecyclerView= homepageSettingBinding.optionsRecyclerview;
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //updateUI(optionslist);
        homepageSettingBinding.setClicklister(this);
        return homepageSettingBinding.getRoot();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload_webview:
                reload();
                break;
            case R.id.findtext:
                find();
                break;
            case R.id.share:
                sharing();
                break;
            case R.id.pcMode:
                usePcMode();
                break;
            case R.id.hideSelf:
                break;
            case R.id.addBookmark:
                addtobookmark();
                break;
            case R.id.menu:
                startSetting();
                break;
            case R.id.button_download:
                startDownload();
                break;
            case R.id.button_bookmark:
                startBookmarked();
                break;
            case R.id.button_history:
                startHistory();
                break;

        }
        dismiss();
        Log.d(TAG, "onClick: " + v.getId());
    }

    private void usePcMode() {
        webManager.reLoad_pcmode();
    }

    private void startDownload() {
        Intent intent = new Intent(getActivity(), DownloadActivity.class);
        startActivity(intent);
    }

    private void startHistory() {
        Intent history_intent = new Intent(getActivity(), HistoryActivity.class);
        startActivity(history_intent);
    }

    /**
     * 启动设置页面
     */
    private void startSetting() {
        startActivity(new Intent(getActivity(), SettingActivity.class));

    }

    /**
     * 启动书签页面
     */
    private void startBookmarked() {
        Intent Bookmark_intent = new Intent(getActivity(), BookmarkPageActivity.class);
        startActivity(Bookmark_intent);
    }

    /**
     * 分享
     */
    private void sharing() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, info.getUrl());
        i.putExtra(Intent.EXTRA_SUBJECT, "网址");
        startActivity(i);

    }
    public void setInterafce(SearchTextOnWebview mInterface){
        this.mInterface=mInterface;
    }

    /**
     * 调用mainActivity中实现的接口，打开网页内搜索
     */
    private void find() {
        mInterface.search();
    }

    private void addtobookmark() {
        WebPage_Info tmp = info;
        FragmentManager fm = getFragmentManager();
        //把当前网页信息传给收藏dialog
        Bookmark_Dialog dialog = Bookmark_Dialog.newInstance(1, tmp);
        dialog.show(fm, "收藏当前网页");

    }

    private void reload() {
        webManager.reLoad((AppCompatActivity) getActivity());
    }

    public void setWebViewManager(WebViewManager mWebViewManager) {
        this.webManager=mWebViewManager;
    }
}
/*private void updateUI(String[] lists) {
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
            Log.d(FOLDER, "onCreateView: "+options[0]);
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
            Log.d(FOLDER, "onCreateView: "+optionslist[position]);
        }

        @Override
        public int getItemCount() {
            return options.length;
        }
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OptionsItemBinding mBinding;
        private int i;

        public OptionViewHolder(@NonNull OptionsItemBinding binding) {
            super(binding.getRoot());
            mBinding=binding;
            mBinding.optionsName.setOnClickListener(this);
            mBinding.setOptionViewModel(new HomePageOptionsViewModel());

        }

        public void bind(String optionName) {
            mBinding.getOptionViewModel().setOptionsName(optionName);
            Log.d(FOLDER, "bind: "+optionName);
        }

        @Override
        public void onClick(View v) {
            Log.d(FOLDER, "点击选项 ");

        }
    }*/
