package com.example.kiylx.ti;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class EditBox_Dialog extends DialogFragment {
    private AboutTag mAboutTag;
    private EditText view1;
    private Context mContext;
    private UPDATE_UI mUPDATE_UI;


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
                mUPDATE_UI.updateSpinnerUI();
                }

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return mbuilder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=getActivity();
        mAboutTag=AboutTag.get(mContext);
        mUPDATE_UI =(UPDATE_UI) mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public interface UPDATE_UI {
        public void updateSpinnerUI();
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
    public void onDetach() {
        super.onDetach();
        mContext=null;
    }
}
