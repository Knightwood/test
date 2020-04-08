package com.crystal.customview.fileIndexView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.crystal.customview.R;


/**
 * 创建者 kiylx
 * 创建时间 2020/4/7 17:04
 */
public class SelectFolderFragment extends DialogFragment {
    private static final String TAG = "下载设置-选择文件夹";
    private View dialogRoot;
    private TextView positiveButton;
    private TextView backButton;
    private TextView pathView;
    private IndexView indexView;
    private static String path;
    private SendPath sendPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogRoot = inflater.inflate(R.layout.dialog_select_dir, container, false);

        positiveButton = dialogRoot.findViewById(R.id.save_folder);
        backButton = dialogRoot.findViewById(R.id.back_folder);
        pathView = dialogRoot.findViewById(R.id.folder_path);
        indexView = dialogRoot.findViewById(R.id.folder_index);
        indexView.setClickAfter(new IndexView.ClickAfter() {
            @Override
            public void after(String path) {
                SelectFolderFragment.path = path;
                pathView.setText(path);
            }
        });
        pathView.setText(indexView.getLastPath());
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+path);
                if (sendPath!=null){
                    sendPath.send(path);
                }
                dismiss();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexView.goBack();
            }
        });

        return dialogRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialogStyle();
    }


    public void dialogStyle() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //指定显示位置
            //layoutParams.gravity = Gravity.BOTTOM;
            /*WindowManager manager = getActivity().getWindowManager();
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);*/

            //指定显示大小
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height =WindowManager.LayoutParams.MATCH_PARENT;
            //设置背景，不然无法扩展到屏幕边缘
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }

    /**
     * 此接口用于：在当前dialog中选择了文件夹后，
     * 当点击确定时，将会调用此接口，把选择的文件夹的路径传给实现接口的类
     */
    public interface SendPath{
        void send(String path);
    }
    public void setSendPath(SendPath sendPath){
        this.sendPath=sendPath;
    }


}
