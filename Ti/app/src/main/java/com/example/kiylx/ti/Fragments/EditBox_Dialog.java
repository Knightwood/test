package com.example.kiylx.ti.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.Fragments.Star_Dialog;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class EditBox_Dialog extends DialogFragment {
    private AboutTag mAboutTag;
    private EditText view1;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=getActivity();
        mAboutTag=AboutTag.get(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder mbuilder =new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        final View view=LayoutInflater.from(getContext()).inflate(R.layout.edit_box,null);
        view1 = view.findViewById(R.id.editTagBox);

        mbuilder.setView(view);
        mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = view1.getText().toString();
                if (!(str.equals(""))){
                    mAboutTag.add(str);
                }
                if (getTargetFragment() == null) {
                    return;
                }
                returnResult();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return mbuilder.create();

    }

    private void returnResult() {
        if(getTargetFragment()==null){
            return;
        }
        Intent intent =new Intent();
        getTargetFragment().onActivityResult(0, Activity.RESULT_OK,intent);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("EditBox","onDetach");
        mContext=null;

    }
}
