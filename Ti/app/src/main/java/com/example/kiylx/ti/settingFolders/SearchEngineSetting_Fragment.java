package com.example.kiylx.ti.settingFolders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.corebase.SomeRes;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.databinding.MultiPageItemBinding;
import com.example.kiylx.ti.model.MultiPage_ViewModel;
import com.example.kiylx.ti.myFragments.MultPage_DialogFragment;

import java.util.ArrayList;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/19 15:50
 */
public class SearchEngineSetting_Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_searchenginesetting,container,false);

        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }

//=============================================适配器=====================================================//
    private class WebSiteAdapter extends RecyclerView.Adapter<SearchEngineSetting_Fragment.engineHolder> {
        private ArrayList<WebPage_Info> lists;
        MultiPageItemBinding pageitemBinding;

        WebSiteAdapter(ArrayList<WebPage_Info> mlists) {
            this.lists = mlists;
            boolean ta = lists.isEmpty();

        }

        @NonNull
        @Override
        public SearchEngineSetting_Fragment.engineHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            pageitemBinding = DataBindingUtil.inflate(getLayoutInflater(),R.layout.multi_page_item,viewGroup,false);
            return new SearchEngineSetting_Fragment.engineHolder(pageitemBinding);

        }

        @Override
        public void onBindViewHolder(@NonNull SearchEngineSetting_Fragment.engineHolder engineHolder, int i) {
            engineHolder.bind(lists.get(i), i);
        }


        @Override
        public int getItemCount() {

            return lists.size();
        }

        private void setLists(ArrayList<WebPage_Info> lists) {

            this.lists = lists;
        }
    }

    private class engineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //变量
        private int pos;
        private WebPage_Info minfo;

        private MultiPageItemBinding mBinding;


        engineHolder(@NonNull MultiPageItemBinding binding) {
            super(binding.getRoot());
            mBinding=binding;

            //绑定上viewmodel
            mBinding.setInfos(new MultiPage_ViewModel());
            mBinding.setClickon(this);


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

                mBinding.websiteItem.setTextColor(getResources().getColor(R.color.textColor));

        }

        @Override
        public void onClick(View v) {

        }

    }
}
