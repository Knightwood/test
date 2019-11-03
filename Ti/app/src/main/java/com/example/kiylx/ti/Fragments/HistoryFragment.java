package com.example.kiylx.ti.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kiylx.ti.AboutHistory;
import com.example.kiylx.ti.Activitys.HistoryActivity;
import com.example.kiylx.ti.DateProcess.KindsofDate;
import com.example.kiylx.ti.DateProcess.TimeProcess;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    HistoryFragment.HistoryAdapter mAdapter;
    private ArrayList<WebPage_Info> mHistorys;
    View view;
    private RecyclerView listView;
    private AboutHistory sAboutHistory;
    private ChipGroup mChipGroup;
    String[] mDateli;
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        v=inflater.inflate(R.layout.fragment_history,null);
        listView = v.findViewById(R.id.showHistory_1);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
//初始化recyclerview为最近七天的数据
        mDateli = new String[2];
        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.THISWEEK, TimeProcess.getTime());
        updateUI(mDateli[0], mDateli[1]);
        CheckedChangeListener();
        return v;

    }

    //监听chipgroup以更新recyclerview视图
    private void CheckedChangeListener() {
        mChipGroup = v.findViewById(R.id.Date_ChipGroup);
        mChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                switch (i) {
                    case R.id.thisweek:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.THISWEEK, TimeProcess.getTime());
                        break;
                    case R.id.thismonth:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.THISWEEK, TimeProcess.getTime());
                        break;
                    case R.id.month1:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH1, TimeProcess.getTime());
                        break;
                    case R.id.month2:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH2, TimeProcess.getTime());
                        break;
                    case R.id.month3:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH3, TimeProcess.getTime());
                        break;
                    case R.id.month4:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH4, TimeProcess.getTime());
                        break;
                    case R.id.month5:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH5, TimeProcess.getTime());
                        break;
                }
                updateUI(mDateli[0], mDateli[1]);
            }
        });

    }


    private void updateUI(String startDate, String endDate) {
        //str1:开始日期。str2:结束日期
        sAboutHistory = AboutHistory.get(getActivity());
        mHistorys = sAboutHistory.getInfoFromDate(startDate, endDate);
        if (mHistorys.isEmpty()) {
            return;
        }
        if (null == mAdapter) {
            mAdapter = new HistoryFragment.HistoryAdapter(mHistorys);
            listView.setAdapter(mAdapter);
            Log.d("历史activity", "onClick: 创建adapter函数被触发");
        } else {
            mAdapter.setLists(mHistorys);
            mAdapter.notifyDataSetChanged();
        }


    }

    private void itemPopmenu(View v) {
        PopupMenu itemMenu = new PopupMenu(getActivity(), v);
        MenuInflater inflater = itemMenu.getMenuInflater();
        inflater.inflate(R.menu.history_item_option, itemMenu.getMenu());
        itemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
                        break;
                    case R.id.open1:
                        break;
                    case R.id.delete_hiatory:
                        break;
                    case R.id.addToBookmark:
                        break;
                }
                return false;
            }
        });
        itemMenu.show();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryFragment.HistoryViewHolder> {
        ArrayList<WebPage_Info> lists;

        HistoryAdapter(ArrayList<WebPage_Info> list) {
            this.lists = list;
            boolean ta = lists.isEmpty();
            Log.d("历史activity", "onClick: Adapter构造函数被触发  lists是否为空" + ta + lists.get(0).getUrl());
        }

        void setLists(ArrayList<WebPage_Info> lists) {
            this.lists = lists;
        }

        @NonNull
        @Override
        public HistoryFragment.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            Log.d("历史activity", "onCreateViewHolder函数被触发");
            return new HistoryFragment.HistoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryFragment.HistoryViewHolder holder, int position) {
            holder.bind(lists.get(position));
            Log.d("历史activity", " onBindViewHolder函数被触发");
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView url;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("历史activity", " HistoryViewHolder构造函数函数被触发");
            title = itemView.findViewById(R.id.itemTitle);
            url = itemView.findViewById(R.id.itemurl);
            itemView.setOnClickListener(this);
        }

        public void bind(WebPage_Info info) {
            title.setText(info.getTitle());
            url.setText(info.getUrl());
            Log.d("历史activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {

        }
    }
}