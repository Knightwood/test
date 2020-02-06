package com.crystal.android.nlauncher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crystal.android.nlauncher.R;


/**
 * @author kiylx
 * @time 2020/2/5 15:18
 */
public class NerdLauncherFeagment extends Fragment {
    private RecyclerView mRecyclerView;


    public static Fragment newInstance() {
        return new NerdLauncherFeagment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView=v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }
}
