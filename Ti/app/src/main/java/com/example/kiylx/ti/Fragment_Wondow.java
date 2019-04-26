package com.example.kiylx.ti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Fragment_Wondow extends Fragment {
    WebAdapter adapter;
    WebList listData= new WebList();
    private getdataInterface gett;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_window,container,false);
        ListView listView = view.findViewById(R.id.list_item);


        adapter = new WebAdapter(getActivity(),listData);
        //listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        gett=(getdataInterface) context;
        listData = gett.getWebList_data();
        }
    public interface getdataInterface {
        public WebList getWebList_data();
    }



}
