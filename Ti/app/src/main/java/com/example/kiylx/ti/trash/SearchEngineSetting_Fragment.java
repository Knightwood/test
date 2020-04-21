package com.example.kiylx.ti.Trash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Tool.HashMapProcess;
import com.example.kiylx.ti.Tool.PreferenceTools;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.databinding.SelectItemBinding;
import com.example.kiylx.ti.Tool.Action;
import com.example.kiylx.ti.model.Checked_item;
import com.example.kiylx.ti.model.Title_ViewModel;
import com.example.kiylx.ti.ui.mFragments.EditText_Dialog;
import com.example.kiylx.ti.myInterface.EditTextInterface;
import com.example.kiylx.ti.myInterface.Setmessage;
import com.example.kiylx.ti.DB.search_engine_db.SearchEngineDao;
import com.example.kiylx.ti.DB.search_engine_db.SearchEngineDatabase;
import com.example.kiylx.ti.DB.search_engine_db.SearchEngineEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/19 15:50
 */
public class SearchEngineSetting_Fragment extends DialogFragment implements Setmessage {
    private static final String TAG = "搜索引擎数据库";
    private SearchUrlAdapter adapter;
    private View rootView;
    private RecyclerView recyclerView;
    public List<SearchEngineEntity> urlList;
    private SearchEngineDao mdao;
    //private MyTask myTask;
    private HashMap<String,String> searchEngine;//搜索引擎的hashmap
    private List<String> searchEngineNameList;//提取key值存进去

    private String engineUrl = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchEngine=new LinkedHashMap<>(PreferenceTools.getHashMap2(getActivity(), WebviewConf.searchengineList));
        searchEngineNameList=new ArrayList<>(HashMapProcess.getKeys(searchEngine));

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_searchenginesetting,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()).setTitle("搜索引擎列表");
        dialogBuilder.setView(rootView)
        .setPositiveButton("添加搜索引擎", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        recyclerView = rootView.findViewById(R.id.engine_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mdao = SearchEngineDatabase.getInstance(getActivity()).searchEngineDao();

        //init2();

       /* FloatingActionButton button=rootView.findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        initData();
        new MyTask().execute(Action.GETALL);
        //myTask.execute(Action.UPDATEINFO,)
        //return rootView;//super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateDialog: 对话框显示");

        return dialogBuilder.create();
    }

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        return super.onCreateView(inflater, container, savedInstanceState);
    }*/

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //指定显示位置
            //layoutParams.gravity = Gravity.BOTTOM;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 测试用
     */
    private void initData() {
        urlList = new ArrayList<>();

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                SearchEngineEntity a1 = new SearchEngineEntity();
                a1.setUrl("123456");
                a1.setCheck_b(true);
                SearchEngineEntity a2 = new SearchEngineEntity();
                a2.setUrl("111");
                a2.setCheck_b(false);
                mdao.insert(a1, a2);
            }
        }).start();*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                urlList= mdao.getAll();
                Log.d(TAG, "run: "+urlList.isEmpty());
                Log.d(TAG, "run: "+urlList.get(0).getUrl());
            }
        }).start();

    }

    @Override
    public void setInfos() {
        new MyTask().execute(Action.GETALL);
    }

    /**
     * 虽然数据库菜的数据不会很多，在主线程里操作也没啥问题（在build数据库时添加.allowMainThreadQueries()实现），
     * 但是这里我还是想用多线程写一下。
     * <p>
     * object参数：
     * 0位置表示行为，行为有：添加，删除，更新，获取所有值
     * 1位置表示要改的原数据：在添加删除更新时放所需的数据（searchengine_info）
     * 2位置表示要改成的新数据
     */
    private class MyTask extends AsyncTask<Object, java.lang.Void, List<SearchEngineEntity>> {

        @Override
        protected List<SearchEngineEntity> doInBackground(Object... objects) {

            switch ((Action) objects[0]) {
                case ADD:
                    /*
                     * 添加某个SearchEngineEntity对象
                     * */
                    mdao.insert((SearchEngineEntity) objects[1]);
                    break;
                case DELETE:
                    /*
                     * 1位置放url字符串
                     * 删除某个项目
                     * */
                    mdao.deleteitem((String) objects[1]);
                    break;
                case FIND:
                    break;
                case GETALL:
                    //获取唯一一个被选择的搜索引擎url
                    if (mdao.getItem(true).isEmpty()) {
                        engineUrl = mdao.getAll().get(0).getUrl();
                    } else {
                        engineUrl = mdao.getItem(true).get(0).getUrl();
                    }

                    break;
                /*case UPDATEINFO:
                 *//*
                 * 更新url，1位置是旧url，2位置是新url
                 * *//*
                    mdao.updateURL((String) objects[1], (String) objects[2]);
                    break;*/
                case UPDATEBOOLEAN:
                    //把旧的check_b改成false，新的改成true，再把旧的engineUrl用新的覆盖，如此完成单选功能
                    mdao.updateBooleaan(engineUrl, false);
                    mdao.updateBooleaan((String) objects[1], true);
                    engineUrl = (String) objects[1];
                    break;

            }
            return mdao.getAll();
        }

        @Override
        protected void onPostExecute(List<SearchEngineEntity> searchEngineEntities) {
            for (int i = 0; i < searchEngineEntities.size(); i++) {
                Log.d(TAG, "更改之后的数据: " + searchEngineEntities.get(i).getUrl() + "    " + searchEngineEntities.get(i).isCheck_b());
            }
            uodateUI(searchEngineEntities);
            Log.d(TAG, "线程执行完: " + searchEngineEntities.size());
        }
    }

    public void uodateUI(List<SearchEngineEntity> list) {
        if (list == null || list.isEmpty())
            return;
        if (this.adapter == null) {
            this.adapter = new SearchUrlAdapter(list);
            recyclerView.setAdapter(this.adapter);
        } else {
            this.adapter.setLists(list);
            this.adapter.notifyDataSetChanged();
        }
    }


    //=============================================适配器=====================================================//
    private class SearchUrlAdapter extends RecyclerView.Adapter<SearchEngineSetting_Fragment.engineHolder> {
        private List<SearchEngineEntity> lists = null;
        SelectItemBinding itemBinding;


        SearchUrlAdapter(List<SearchEngineEntity> mlists) {
            this.lists = mlists;
            Log.d(TAG, "SearchUrlAdapter: " + lists.isEmpty());
        }

        void setLists(List<SearchEngineEntity> mlists) {
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
            engineHolder.bind(lists.get(i).getUrl(), lists.get(i).isCheck_b());
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
            mBinding.setCheck(new Checked_item(editTextInterface, false));

        }

        void bind(String URL, Boolean b) {
            this.URL = URL;
            mBinding.getSearchEngine().setTitle(URL);
            mBinding.getCheck().setChecked(b);

        }

        @Override
        public void onClick(View v) {
        }

    }

    public EditTextInterface editTextInterface = new EditTextInterface() {
        @Override
        public void changeSelect(String s) {
            if (!engineUrl.equals(s)) {
                //更新布尔值，把旧的改成false，把新的条目的check_b改成true
                new MyTask().execute(Action.UPDATEBOOLEAN, s);
            }

        }

        @Override
        public void editText(String olds) {
            EditText_Dialog dialog = EditText_Dialog.getInstance(olds);
            dialog.setInterface(SearchEngineSetting_Fragment.this);
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            dialog.show(manager, "编辑文本");

            Log.d(TAG, "搜索引擎条目的点击事件---编辑文本" + olds);
        }

        @Override
        public void deleteItem(String s) {
            new MyTask().execute(Action.DELETE, s);
            Log.d(TAG, "搜索引擎条目的点击事件---删除条目" + s);
        }
    };
}
