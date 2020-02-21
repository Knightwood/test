package com.example.kiylx.ti.settingFolders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.databinding.SelectItemBinding;
import com.example.kiylx.ti.model.Checked_item;
import com.example.kiylx.ti.model.Title_ViewModel;

import java.util.ArrayList;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/19 15:50
 */
public class SearchEngineSetting_Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchenginesetting, container, false);

        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }

    //=============================================适配器=====================================================//
    private class WebSiteAdapter extends RecyclerView.Adapter<SearchEngineSetting_Fragment.engineHolder> {
        private ArrayList<String> lists;
        SelectItemBinding itemBinding;


        WebSiteAdapter(ArrayList<String> mlists) {
            this.lists = mlists;
        }

        @NonNull
        @Override
        public SearchEngineSetting_Fragment.engineHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_item, viewGroup, false);
            return new SearchEngineSetting_Fragment.engineHolder(itemBinding);

        }

        @Override
        public void onBindViewHolder(@NonNull SearchEngineSetting_Fragment.engineHolder engineHolder, int i) {
            engineHolder.bind(lists.get(i),false);
        }


        @Override
        public int getItemCount() {

            return lists.size();
        }

        private void setLists(ArrayList<String> lists) {

            this.lists = lists;
        }
    }

    private class engineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //变量
        private String URL;
        private SelectItemBinding mBinding;


        engineHolder(@NonNull SelectItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            //绑定上viewmodel
            mBinding.setSearchEngine(new Title_ViewModel(""));
            mBinding.setCheck(new Checked_item(false));
        }

        void bind(String URL,Boolean b) {
            this.URL = URL;
            mBinding.getSearchEngine().setTitle(URL);
            mBinding.getCheck().setChecked(b);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.search_engine_checkbox:
                    break;
                case R.id.edit_engine_Button:
                    break;
            }
        }

    }
}
