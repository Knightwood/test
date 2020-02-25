package com.example.kiylx.ti.myFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.myInterface.Setmessage;
import com.example.kiylx.ti.search_engine_db.SearchEngineDao;
import com.example.kiylx.ti.search_engine_db.SearchEngine_db_Util;

import java.util.Objects;
import java.util.Set;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/25 15:38
 */
public class EditText_Dialog extends DialogFragment {

    private static final String BUNDLE_PARAM1 = "param1";

    private EditText view1;//编辑框
    String oldString;
    private Setmessage mInterface;//用于执行某些操作，比如点击确定后的更新界面


    public static EditText_Dialog getInstance(String s){
        EditText_Dialog dialog=new EditText_Dialog();

        Bundle oldString=new Bundle();
        oldString.putString(BUNDLE_PARAM1,s);
        dialog.setArguments(oldString);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            oldString=getArguments().getString(BUNDLE_PARAM1);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(Objects.requireNonNull(getContext()));

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_box, null);
        view1 = view.findViewById(R.id.editTagBox);
        view1.setText(oldString);

        mBuilder.setView(view);
        mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //获取旧string，从textview中拿取新的string，然后从多线程中更新数据库
                new MyTask().execute(oldString,view1.getText().toString());

            }
        });

        return mBuilder.create();
    }

    /**
     * 0位置存放旧string，1位置存放新string
     */
    private class MyTask extends AsyncTask<String,Void,Void> {
        SearchEngineDao dao;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dao= SearchEngine_db_Util.getDao(getContext());
        }

        @Override
        protected Void doInBackground(String... strings) {
            dao.updateURL(strings[0],strings[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mInterface.setInfos();
        }
    }
    public void setInterface(Setmessage mInterface){
        this.mInterface=mInterface;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInterface=null;
    }
}
