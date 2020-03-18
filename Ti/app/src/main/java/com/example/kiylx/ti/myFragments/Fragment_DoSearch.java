package com.example.kiylx.ti.myFragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.Tool.ProcessUrl;
import com.example.kiylx.ti.corebase.WebPage_Info;

import java.util.ArrayList;
import java.util.List;


public class Fragment_DoSearch extends Fragment {
    private static final String TAG = "fragmentLifeCycle";

    private static final String ARG_PARAM1 = "param1";//这是创建fragment时要保存在bundle中数据的名称，存着的值是一个字符串（它是网址或是普通的文字）
    private List<WebPage_Info> webPage_infoArrayList =new ArrayList<>();//用输入文字匹配之后的网址列表
    private ProcessUrl mProcessUrl;//处理字符串的类
    private String mCurrentURL;//用来存储当前网页页面的网址，这样打开搜索，搜索栏里就会填充上网址
    private String searchbarTEXT;//搜索框输入的文本信息，可以拿来比较历史记录以及收藏

    private EditText searchbox;//搜索框

    private RecyclerView mRecyclerView;
    private SearchrecordAdapter adapter;

    private OnFragmentInterfaceListener mListener;

    public Fragment_DoSearch() {

    }


    /**
     * @param param1
     * @return
     */
    public static Fragment_DoSearch newInstance(String param1) {
        Fragment_DoSearch fragment = new Fragment_DoSearch();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, param1);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInterfaceListener) {
            mListener = (OnFragmentInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInterfaceListener");
        }
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentURL = getArguments().getString(ARG_PARAM1);

        }
        if(null== mProcessUrl)
        mProcessUrl =new ProcessUrl(getActivity());
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_search_page, container, false);

        searchbox =v.findViewById(R.id.search_column);

        mRecyclerView =v.findViewById(R.id.show_history_for_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateList();//展示空的recyclerview
        textWatcher();
        enter_key();
        Log.d(TAG, "onCreateView: ");
        if(!mCurrentURL.equals(SomeRes.default_homePage_url))
        searchbox.setText(mCurrentURL);//如果当前网页网址不是默认的主页，那就填充网址到搜索栏
        searchbox.selectAll();//让文字被全选
        return v;
    }

    // 开始搜索的方法
    public void beginSearch(String s) {
        if (mListener != null) {
            mListener.analysisText(s);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "onDetach: ");
    }

    /**
     * 搜索框文字编辑完后按下回车键
     */
    private void enter_key(){
        searchbox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    Log.d(TAG, "onKeyDown: ");
                    //监听回车键，按下的时候就开始执行搜索操作。
                    mListener.analysisText(searchbarTEXT);
                    return true;
                }
                return false;
            }
        });
    }

    private void textWatcher(){
        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int Bookmarkt, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int Bookmarkt, int before, int count) {
                Log.d(TAG, "onTextChanged: ");

            }

            @Override
            public void afterTextChanged(Editable s) {
            //把文本与历史记录进行对比，筛选出符合的信息，刷新listview
                searchbarTEXT =searchbox.getText().toString();
                webPage_infoArrayList = mProcessUrl.formatList(searchbarTEXT);
                //与历史记录以及收藏记录比对，拿到arraylist
                updateList();//更新界面


            }
        });
    }
    public interface OnFragmentInterfaceListener {

        void analysisText(String s);
    }


    private void updateList(){
        if(null==adapter){
        adapter=new SearchrecordAdapter(webPage_infoArrayList);
        mRecyclerView.setAdapter(adapter);}else{
            adapter.notifyDataSetChanged();
        }
    }

    private class SearchrecordAdapter extends RecyclerView.Adapter<SearchItemHolder>{
        private List<WebPage_Info> lists;

        SearchrecordAdapter(List<WebPage_Info> lists) {
            this.lists = lists;
        }

        @NonNull
        @Override
        public SearchItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item,null,false);
            return new SearchItemHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchItemHolder searchItemHolder, int i) {
            searchItemHolder.bind(lists.get(i));
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }


    private class SearchItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView view1;
        TextView view2;
        ImageView view3;
        SearchItemHolder(@NonNull View itemView) {
            super(itemView);
            view1=itemView.findViewById(R.id.itemTitle);
            view2=itemView.findViewById(R.id.itemurl);
            view3=itemView.findViewById(R.id.Bookmarkimage);
            itemView.setOnClickListener(this);
        }
        public void bind(WebPage_Info info){
            view1.setText(info.getTitle());
            view2.setText(info.getUrl());
            if(info.getWEB_feature()==-1){
                //如果是被收藏网址，那就把图片替换位已收藏
                view3.setImageResource(R.drawable.ic_star_black_24dp);
            }
        }

        @Override
        public void onClick(View v) {
            String tmp=view2.getText().toString();
            mListener.analysisText(tmp);
        }
    }

}
