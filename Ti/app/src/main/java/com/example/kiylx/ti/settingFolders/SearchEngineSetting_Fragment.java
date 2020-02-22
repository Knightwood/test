package com.example.kiylx.ti.settingFolders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.databinding.SelectItemBinding;
import com.example.kiylx.ti.model.Checked_item;
import com.example.kiylx.ti.model.Title_ViewModel;
import com.example.kiylx.ti.search_engine_db.SearchEngineDao;
import com.example.kiylx.ti.search_engine_db.SearchEngineDatabase;
import com.example.kiylx.ti.search_engine_db.SearchEngineEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/19 15:50
 */
public class SearchEngineSetting_Fragment extends Fragment {
private static final String TAG="搜索引擎数据库";
    private SearchUrlAdapter adapter;
    private View rootView;
    private RecyclerView recyclerView;
    private List<SearchEngineEntity> urlList;
    public SearchEngineDao mdao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_searchenginesetting, container, false);

        recyclerView=rootView.findViewById(R.id.engine_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mdao = SearchEngineDatabase.getInstance(getActivity()).searchEngineDao();
        //initData();
        init2();
       /* FloatingActionButton button=rootView.findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        uodateUI();
        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initData() {
        urlList=new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchEngineEntity a1=new SearchEngineEntity();
                a1.setUrl("123456");
                a1.setCheck(true);
                SearchEngineEntity a2=new SearchEngineEntity();
                a2.setUrl("111");
                a2.setCheck(false);
                //mdao.insert(a1,a2);
            }
        }).start();
new Thread(new Runnable() {
    @Override
    public void run() {
        urlList= mdao.getAll();
        Log.d(TAG, "run: "+urlList.isEmpty());
        Log.d(TAG, "run: "+urlList.get(0).getUrl());
    }
}).start();

    }
    void init2(){
        urlList=new ArrayList<>();

        SearchEngineEntity a1=new SearchEngineEntity();
        a1.setUrl("123456");
        a1.setCheck(true);
        SearchEngineEntity a2=new SearchEngineEntity();
        a2.setUrl("111");
        a2.setCheck(false);

        urlList.add(a1);
        urlList.add(a2);
    }

    public void uodateUI(){
        if (this.adapter==null){
            this.adapter=new SearchUrlAdapter(urlList);
            recyclerView.setAdapter(this.adapter);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    urlList= mdao.getAll();
                }
            }).start();
            this.adapter.notifyDataSetChanged();
        }
    }

    //=============================================适配器=====================================================//
    private class SearchUrlAdapter extends RecyclerView.Adapter<SearchEngineSetting_Fragment.engineHolder> {
        private List<SearchEngineEntity> lists;
        SelectItemBinding itemBinding;


        SearchUrlAdapter(List<SearchEngineEntity> mlists) {
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
            engineHolder.bind(lists.get(i).getUrl(),lists.get(i).isCheck());
        }


        @Override
        public int getItemCount() {

            return lists.size();
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
                case R.id.searchengine_url:
                    break;
                case R.id.edit_engine_Button:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mdao.delete(urlList.get(1));
                        }
                    }).start();

                    uodateUI();
                    break;
            }
        }

    }
}
