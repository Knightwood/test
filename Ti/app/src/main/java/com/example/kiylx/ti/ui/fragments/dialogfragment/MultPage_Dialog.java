package com.example.kiylx.ti.ui.fragments.dialogfragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.MainActivity;
import com.example.kiylx.ti.mvp.presenter.WebViewInfo_Manager;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.model.MultiPage_Bean;
import com.example.kiylx.ti.interfaces.MultiDialog_Functions;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.databinding.MultiPageItemBinding;
import com.example.kiylx.ti.model.WebPage_Info;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class MultPage_Dialog extends DialogFragment {
    private static final String TAG = "MultPage_Dialog";
    private RecyclerView mRecyclerView;
    private WebSiteAdapter mWebSiteAdapter;
    private int mCurrect;
    private static MultiDialog_Functions minterface;
    private List<WebPage_Info> lists;//webpageinfo 的list
    private int trashNum;//被删除的webview数量，大于0时在onStop中发消息让manager回收Webview
    private View v;//根视图
    private ImageView touchHieView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(MultPage_Dialog.STYLE_NORMAL, R.style.DialogThemes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_multpage, null);
        mRecyclerView = v.findViewById(R.id.mult_item);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();//每次打开多窗口都会触发更新视图

        //imageview占据剩余的空间，点击时隐藏dialog
        touchHieView=v.findViewById(R.id.hide_view3);
        touchHieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

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
        LogUtil.d(TAG, "onCreateView: ");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG, "onViewCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            WindowManager manager = getActivity().getWindowManager();
            Display d = manager.getDefaultDisplay();
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //如果是横屏
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                Point point = new Point();
                d.getSize(point);
                lp.width = (int) (0.5 * point.x);

            } else {
                //竖屏
                lp.gravity = Gravity.BOTTOM;
                //指定显示大小
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            }


            lp.height = WindowManager.LayoutParams.MATCH_PARENT;

            //window.setDimAmount(0);//dialog周围全透明
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(lp);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
        LogUtil.d(TAG, "onBookmarkt: ");
        //获取当前网页的pos
        mCurrect = MainActivity.getCurrent();

        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop: ");
        if (trashNum > 0) {
            notifyThrowTrash();//通知MainActivity销毁被删除的webview
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d(TAG, "onDestroyView: ");
    }

    /**
     * 删除标签页会把webview放进WebviewManager的trashList。
     * 在onStop()中发送消息，让mainActivity销毁trashList中的webview
     */
    private void notifyThrowTrash() {
        EventMessage message = new EventMessage(3, "销毁trashList中的垃圾");
        EventBus.getDefault().post(message);
    }


    public static void setInterface(MultiDialog_Functions minterface) {
        MultPage_Dialog.minterface = minterface;
    }

    /**
     * 接受网页加载完成信息，重新获取数据更新界面
     *
     * @param massage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateData(EventMessage massage) {
        if (massage.getType() == 2) {
            updateUI();
        }
    }

    private void updateUI() {
        lists = WebViewInfo_Manager.getPageList();
        LogUtil.d(TAG, "updateUI: 多窗口处的数组大小" + lists.size());
        if (null == mWebSiteAdapter) {
            mWebSiteAdapter = new WebSiteAdapter(lists);
            mRecyclerView.setAdapter(mWebSiteAdapter);
            LogUtil.d(TAG, "onClick: setAdapter方法被触发");
        } else {
            mCurrect = MainActivity.getCurrent();//重新拿到current值，用于当删除某个标签页时能正确设置颜色

            //mWebSiteAdapter.setLists(lists);
            //重新获取数据更新
            mWebSiteAdapter.notifyDataSetChanged();
        }
    }

    private class WebSiteAdapter extends RecyclerView.Adapter<WebsiteHolder> {
        private List<WebPage_Info> lists;
        MultiPageItemBinding pageitemBinding;

        WebSiteAdapter(List<WebPage_Info> mlists) {
            this.lists = mlists;
            boolean ta = lists.isEmpty();
            LogUtil.d("MultPage_Dialog", "onClick: Adapter构造函数被触发" + ta);
        }

        @NonNull
        @Override
        public WebsiteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LogUtil.d(TAG, "onClick: onCreateViewHolder构造方法被触发");
            pageitemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.multi_page_item, viewGroup, false);
            return new WebsiteHolder(pageitemBinding);

        }

        @Override
        public void onBindViewHolder(@NonNull WebsiteHolder websiteHolder, int i) {
            LogUtil.d(TAG, "onClick: onBindViewHolder方法被触发");
            websiteHolder.bind(lists.get(i), i);
        }


        @Override
        public int getItemCount() {

            return lists.size();
        }

        private void setLists(List<WebPage_Info> lists) {

            this.lists = lists;
        }
    }

    private class WebsiteHolder extends ViewHolder implements View.OnClickListener {
        //变量
        private int pos;
        private WebPage_Info minfo;

        private MultiPageItemBinding mBinding;


        WebsiteHolder(@NonNull MultiPageItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            //绑定上viewmodel
            mBinding.setInfos(new MultiPage_Bean());
            mBinding.setClickon(this);
            LogUtil.d(TAG, "onClick: WebsiteHolder构造方法被触发");


        }

        void bind(WebPage_Info item_info, int pos) {
            minfo = item_info;
            this.pos = pos;
            //获取点击的item的位置，也就是webview在list的位置，方便后面标记当前标签页
            String title = minfo.getTitle();
            if (SomeRes.default_homePage_url.equals(item_info.getUrl())) {
                title = getString(R.string.new_tab);
            }
            mBinding.getInfos().setTitle(title);
            mBinding.websiteItem.setTextColor(getResources().getColor(R.color.black));//设定字体颜色是黑色，防止复用item时字体颜色还是蓝色
            if (pos == mCurrect){//如果是pos等于current，字体颜色改成蓝色
                mBinding.websiteItem.setTextColor(getResources().getColor(R.color.blueColor));
            }

            //通过拿到currect值，改变文字颜色。
            LogUtil.d(TAG, "onClick: bind方法被触发,位置："+pos+"  current:"+mCurrect);

        }

        @Override
        public void onClick(View v) {
            switch ((v.getId())) {
                case R.id.close_button:
                    LogUtil.d("多窗口关闭点击", "onClick:" + pos);
                    minterface.delete_page(pos);//移除webview
                    trashNum += 1;
                    //mWebSiteAdapter.notifyItemRemoved(pos);
                    //mWebSiteAdapter.notifyItemRangeChanged(pos,lists.size());//上面移除webview就已经相当于删除了list中pos位置的元素
                    updateUI();

                    break;
                case R.id.website_item:
                    LogUtil.d(TAG, "onClick: 网页切换按钮被触发");
                    minterface.switchPage(pos);
                    dismiss();
                    break;
            }
        }
    }
}
