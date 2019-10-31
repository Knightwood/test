package com.example.kiylx.ti.Fragments;

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
import com.example.kiylx.ti.SearchProcess.ProcessRecordItem;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_DoSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_DoSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_DoSearch extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "fragmentLifeCycle";
    private ArrayList<WebPage_Info> mPage_infos =new ArrayList<>();
    private ProcessRecordItem mProcessRecordItem;//处理字符串的类

    // TODO: Rename and change types of parameters
    private String mParam1;
    //mParam1用来存储当前网页页面的网址，这样打开搜索，搜索栏里就会填充上网址
    //private String mParam2;

    private String searchbarTEXT;//搜索框输入的文本信息，可以拿来比较历史记录以及收藏
    private EditText searchbox;
    private RecyclerView mRecyclerView;
    private SearchrecordAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public Fragment_DoSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment Fragment_DoSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_DoSearch newInstance(String param1) {
        Fragment_DoSearch fragment = new Fragment_DoSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(null== mProcessRecordItem)
        mProcessRecordItem =new ProcessRecordItem();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_search_page, container, false);

        searchbox =v.findViewById(R.id.search_column);

        mRecyclerView =v.findViewById(R.id.show_history_for_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateList();//展示空的recyclerview
        textWatcher();
        enter_key();
        Log.d(TAG, "onCreateView: ");
        if(mParam1!=null)
        searchbox.setText(mParam1);//如果当前网页不是null，那就填充网址
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void beginSearch(String s) {
        if (mListener != null) {
            mListener.onFragmentInteraction(s);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "onDetach: ");
    }
    private void enter_key(){
        searchbox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    Log.d(TAG, "onKeyDown: ");
                    //监听回车键，按下的时候就开始执行搜索操作。
                    mListener.onFragmentInteraction(searchbarTEXT);
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
                mPage_infos = mProcessRecordItem.formatList(searchbarTEXT);
                //与历史记录以及收藏记录比对，拿到arraylist
                updateList();//更新界面


            }
        });
    }
    private void updateList(){
        if(null==adapter){
        adapter=new SearchrecordAdapter(mPage_infos);
        mRecyclerView.setAdapter(adapter);}else{
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String s);
    }
    private class SearchrecordAdapter extends RecyclerView.Adapter<SearchItemHolder>{
        private ArrayList<WebPage_Info> lists;

        public SearchrecordAdapter(ArrayList<WebPage_Info> lists) {
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
        public SearchItemHolder(@NonNull View itemView) {
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
            mListener.onFragmentInteraction(tmp);
        }
    }

}
