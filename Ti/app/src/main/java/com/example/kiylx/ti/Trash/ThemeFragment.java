package com.example.kiylx.ti.trash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.tool.PreferenceTools;

public class ThemeFragment extends Fragment implements Switch.OnCheckedChangeListener {
    private View rootview;
    private Switch oldSearch;
    private Switch searchMatcherVview;
    private Switch uploadHtmlView;

    public static ThemeFragment newInstance() {
        return new ThemeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_theme, container, false);
        initView();
        return rootview;
    }

    private void initView() {
        oldSearch = rootview.findViewById(R.id.oldSearch);
        oldSearch.setChecked(PreferenceTools.getBoolean(getActivity(), SomeRes.SearchViewStyle));
        oldSearch.setOnCheckedChangeListener(this);

        searchMatcherVview = rootview.findViewById(R.id.searchMatcherVview);
        searchMatcherVview.setOnCheckedChangeListener(this);
        searchMatcherVview.setChecked(PreferenceTools.getBoolean(getActivity(), SomeRes.searchMatcher));

        uploadHtmlView = rootview.findViewById(R.id.newUploadView);
        uploadHtmlView.setOnCheckedChangeListener(this);
        uploadHtmlView.setChecked(PreferenceTools.getBoolean(getActivity(), WebviewConf.uploadMode));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.searchMatcherVview:
                PreferenceTools.putBoolean(getActivity(), SomeRes.searchMatcher, isChecked);
                break;
            case R.id.oldSearch:
                PreferenceTools.putBoolean(getActivity(), SomeRes.SearchViewStyle, isChecked);
                break;
            case R.id.newUploadView:
                PreferenceTools.putBoolean(getActivity(), WebviewConf.uploadMode, isChecked);
                break;
        }
    }
}
