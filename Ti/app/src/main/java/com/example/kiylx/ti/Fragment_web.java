package com.example.kiylx.ti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ListView;

public class Fragment_web extends Fragment {
    WebAdapter adapter;
    WebList listData= new WebList();
    //private getdataInterface gett;
    private webSet set1;
    public WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_window,container,false);

        webView = new WebView(getActivity());
        FrameLayout webgroup = view.findViewById(R.id.webview_page);
        webgroup.addView(webView);
        webView.loadUrl("http://www.baidu.com");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        set1 = (webSet) context;
        set1.setting(webView);
    }

    public interface webSet{
        public void setting(WebView vi);
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
