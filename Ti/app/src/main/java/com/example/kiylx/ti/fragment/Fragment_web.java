package com.example.kiylx.ti.fragment;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.CuViewModel;

public class Fragment_web extends Fragment {


    //private getdataInterface gett;
    private create createweb;
    WebView ff;
    private CuViewModel vmodel;

    @Override
    public void onCreate(@Nullable Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        vmodel = ViewModelProviders.of(getActivity()).get(CuViewModel.class);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_window,container,false);
        FrameLayout frameLayout = view.findViewById(R.id.webview_page);
        ff.loadUrl("http://www.baidu.com");
        frameLayout.addView(ff);
        //vmodel.getWebViewlist().observe(this,ArrayList<View>);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createweb = (create) context;
        ff =createweb.addWebview();

    }
    public interface create{
        public WebView addWebview();
    }

    /*
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        gett=(getdataInterface) context;
        listData = gett.getWebList_data();
        }
    public interface getdataInterface {
        public WebList getWebList_data();
    }
*/



}
