package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kiylx.ti.Activitys.MainActivity;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.Converted_WebPage_Lists;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;


public class MultPage_DialogFragment extends DialogFragment {
    private static final String TAG = "MultPage_DialogFragment";
    private RecyclerView mRecyclerView;
    private WebSiteAdapter mWebSiteAdapter;
    private int mCurrect;
    private static MultPage_DialogF_interface minterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multpage_dialog, null);
        mRecyclerView = v.findViewById(R.id.mult_item);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();//每次打开多窗口都会触发更新视图

        ImageButton newPageImageButton = v.findViewById(R.id.newPagebutton);
        newPageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //新建网页
                minterface.click_newPagebutton();
                //执行操作后关闭对话框页面
                dismiss();
            }
        });
        Log.d(TAG, "onCreateView: ");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
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
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(91, 90, 92)));
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
        Log.d(TAG, "onBookmarkt: ");
        //获取当前网页的pos
        mCurrect = MainActivity.getCurrect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    public interface MultPage_DialogF_interface {
        void click_newPagebutton();

        void delete_page(int position);

        void switchPage(int pos);

    }

    public static void setInterface(MultPage_DialogF_interface minterface) {
        MultPage_DialogFragment.minterface = minterface;
    }


    private void updateUI() {
        Converted_WebPage_Lists mConverted_lists = Converted_WebPage_Lists.get();
        ArrayList<WebPage_Info> lists = mConverted_lists.getPageList();
        if (null == mWebSiteAdapter) {
            mWebSiteAdapter = new WebSiteAdapter(lists);
            mRecyclerView.setAdapter(mWebSiteAdapter);
            Log.d(TAG, "onClick: setAdapter方法被触发");
        } else {
            mCurrect = MainActivity.getCurrect();
            //重新拿到current值，用于当删除某个标签页时能正确设置颜色
            mWebSiteAdapter.setLists(lists);
            //重新获取数据更新
            mWebSiteAdapter.notifyDataSetChanged();
        }
    }

    private class WebSiteAdapter extends RecyclerView.Adapter<WebsiteHolder> {
        private ArrayList<WebPage_Info> lists;

        WebSiteAdapter(ArrayList<WebPage_Info> mlists) {
            this.lists = mlists;
            boolean ta = lists.isEmpty();
            Log.d("MultPage_DialogFragment", "onClick: Adapter构造函数被触发" + ta);
        }

        @NonNull
        @Override
        public WebsiteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Log.d(TAG, "onClick: onCreateViewHolder构造方法被触发");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
            return new WebsiteHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull WebsiteHolder websiteHolder, int i) {
            Log.d(TAG, "onClick: onBindViewHolder方法被触发");
            websiteHolder.bind(lists.get(i), i);
        }


        @Override
        public int getItemCount() {

            return lists.size();
        }

        private void setLists(ArrayList<WebPage_Info> lists) {

            this.lists = lists;
        }
    }

    private class WebsiteHolder extends ViewHolder implements View.OnClickListener {
        //控件
        TextView textView;
        ImageButton imageButton;
        //变量
        private int pos;
        private WebPage_Info minfo;


        WebsiteHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.website_item);
            imageButton = itemView.findViewById(R.id.close_button);
            Log.d(TAG, "onClick: WebsiteHolder构造方法被触发");
            textView.setOnClickListener(this);
            imageButton.setOnClickListener(this);

        }

        void bind(WebPage_Info item_info, int pos) {
            minfo = item_info;
            this.pos = pos;
            //获取点击的item的位置，也就是webview在list的位置，方便后面标记当前标签页
            String title = minfo.getTitle();
            if (0 == minfo.getFlags()) {
                title = getString(R.string.new_tab);
            }
            textView.setText(title);
            if (pos == mCurrect)
                textView.setTextColor(getResources().getColor(R.color.textColor));
            //通过拿到currect值，改变文字颜色。
            Log.d(TAG, "onClick: bind方法被触发");

        }

        @Override
        public void onClick(View v) {
            switch ((v.getId())) {
                case R.id.close_button:
                    Log.d(TAG, "onClick: 多窗口关闭按钮被触发" + pos);

                    minterface.delete_page(pos);
                    updateUI();
                    //删除完页面要更新视图
                    break;
                case R.id.website_item:
                    Log.d(TAG, "onClick: 网页切换按钮被触发");

                    minterface.switchPage(pos);
                    dismiss();
                    break;
            }
        }

    }
}
